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

    public void dockerCp(String containerId, String filename) {
        String cmd = "docker cp " + filePath + filename + " " + containerId + ":" + dockerPath;
        executeCommand(cmd);
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
        String cmd = "docker run --privileged -d -p " + port + ":8080 --expose=5900 "  + " --name=\"" + dockerName + "\" " + version;
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
