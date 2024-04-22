package com.jhh.rossystem.utils;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.*;

@Slf4j
@Component
public class ChannelUtil {

    @Value("${shell.host}")
    private String host;

    @Value("${shell.username}")
    private String username;

    @Value("${shell.port}")
    private Integer port;

    @Value("${shell.password}")
    private String password;

    @Value("${shell.file.path}")
    private String filePath;

    @Value("${shell.docker.file.path}")
    private String dockerPath;

    @Value("${shell.docker.dockerfilePath}")
    private String dockerfilePath;

    public String executeCommand(String command) {
        log.info("exec cmd:" + command);
        JSch jsch = new JSch();
        Session session = null;
        ChannelExec channelExec = null;
        InputStream is = null;
        BufferedReader reader = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            // 创建会话
            session = jsch.getSession(username, host, port);
            // 设置密码
            session.setPassword(password);
            // 配置
            session.setConfig("StrictHostKeyChecking", "no");
            // 设置超时时间
            session.setTimeout(50000);
            // 连接会话
            session.connect();
            // 创建命令窗口
            channelExec = (ChannelExec) session.openChannel("exec");
            is = channelExec.getInputStream();
            channelExec.setPty(true);
            // 设置命令
            channelExec.setCommand(command);
            // 执行命令
            channelExec.connect();
            // 获取返回结果
            reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (Exception e) {
            log.info("exec cmd error" + command);
        } finally {
            assert session != null;
            session.disconnect();
            assert channelExec != null;
            channelExec.disconnect();
            try {
                assert is != null;
                is.close();
                assert reader != null;
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        log.info(" result:" + stringBuilder.toString());
        return stringBuilder.toString();
    }

    public void uploadFile(MultipartFile file) {
        JSch jsch = new JSch();
        Session session = null;
        ChannelSftp channelSftp = null;
        InputStream is = null;
        try {
            session = jsch.getSession(username, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setTimeout(50000);
            session.connect();
            // 创建ftp客户端
            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();
            is = file.getInputStream();
            String filename = file.getOriginalFilename();
            // 上传文件
            channelSftp.put(is, filePath + filename);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭会话 连接
            assert session != null;
            session.disconnect();
            assert channelSftp != null;
            channelSftp.disconnect();
            try {
                assert is != null;
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String queryContainerIdByName(String containerName) {
        String cmd = "docker ps -aqf \"name=" + containerName + "\"";
        return executeCommand(cmd);
    }
    public void dockerCp(String from, String to) {
        String cmd = "docker cp " + from + " " + to;
        executeCommand(cmd);
    }
    /**
     * 将指定文件上传到Docker容器中。
     * @param containerId Docker容器的ID。
     * @param filename 需要上传的文件名称。
     * 该方法不返回任何值。
     */
    public void dockerUpload(String containerId, String filename) {
        // 拼接文件的本地路径
        String from = filePath + filename;
        // 拼接容器内的目标路径
        String to = containerID + ":" + dockerPath;
        // 执行Docker复制命令，将文件从本地复制到容器中
        dockerCp(from, to);
    }
    /**
     * 从指定的Docker容器中下载文件。
     * @param containerId Docker容器的ID。
     * @param filename 需要下载的文件名称。
     * 此方法通过调用dockerCp函数，将指定容器内的文件复制到本地文件系统。
     */
    public void dockerDownload(String containerId, String filename) {
        // 构造容器内的文件路径
        String from = containerId + ":" + dockerPath + filename;
        // 指定本地保存文件的路径
        String to = filePath;
        // 执行文件复制操作
        dockerCp(from, to);
    }
    public static boolean isPath(String input){
        //如果字符串以“~”开头或者以“/”开头，说明是路径
        return input.startsWith("~") || input.startsWith("/");
    }
    public Boolean pullImage(String version) {
        String cmd = "docker pull " + version;
        String result = executeCommand(cmd);
        if (result.contains("complete")) {
            return true;
        }
        return false;
    }
    public String buildDocker(String version, String path) {
        String cmd = "docker build -t " + version + " " + path;
        String imageID = executeCommand(cmd);
        return imageID;
    }
    public String rmiDocker(String version) {
        String cmd = "docker rmi " + version;
        String imageID = executeCommand(cmd);
        return imageID;
    }

    public String runDocker(Integer port, String dockerName, String version) {
        String cmd = "docker run -d -p " + port + ":80 --expose=5900 "  + " --name=\"" + dockerName + "\" " + version;
        String containerID = executeCommand(cmd);
        return containerID;
    }
    public String runDocker(String config, String dockerName, String version) {
        String cmd = "docker run -d" + config + " --name=\"" + dockerName + "\" " + version;
        String containerID = executeCommand(cmd);
        return containerID;
    }

    public void startDocker(String dockerName) {
        String cmd = "docker start " + dockerName;
        executeCommand(cmd);
    }

    public void restartDocker(String dockerID) {
        String cmd = "docker restart " + dockerID;
        executeCommand(cmd);
    }

    public void stopDocker(String dockerID) {
        String cmd = "docker stop " + dockerID;
        executeCommand(cmd);
    }

    public void rmDocker(String dockerID) {
        String cmd = "docker rm " + dockerID;
        executeCommand(cmd);
    }


}
