package com.jhh.rossystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jhh.rossystem.controller.bao.ContainerAddObject;
import com.jhh.rossystem.entity.ContainerPort;
import com.jhh.rossystem.entity.Image;
import com.jhh.rossystem.entity.SysContainer;
import com.jhh.rossystem.entity.SysUser;
import com.jhh.rossystem.entity.PortMapping;
import com.jhh.rossystem.mapper.ContainerPortMapper;
import com.jhh.rossystem.mapper.ImageMapper;
import com.jhh.rossystem.mapper.SysContainerMapper;
import com.jhh.rossystem.mapper.SysUserMapper;
import com.jhh.rossystem.service.DockerService;
import com.jhh.rossystem.service.SysContainerService;
import com.jhh.rossystem.utils.ChannelUtil;
import com.jhh.rossystem.utils.Result;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SysContainerServiceimpl implements SysContainerService {

    @Resource
    private SysContainerMapper sysContainerMapper;

    @Resource
    private ContainerPortMapper containerPortMapper;

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private ChannelUtil channelUtil;

    @Resource
    private DockerService dockerService;

    @Resource
    private ImageMapper imageMapper;

    @Override
    public Result add(ContainerAddObject containerAddObject) {
        QueryWrapper<SysContainer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", containerAddObject.getUserid());

        // SysContainer sysContainer = sysContainerMapper.selectOne(queryWrapper);

        Long count = sysContainerMapper.selectCount(queryWrapper);
        // 容器数量限制
        if (4 <= count) {
            return Result.fail("每个用户最多创建4个容器！");
        }

        // ports现在是PortMapping对象的列表
        // 取出端口号
        List<PortMapping> portMappings = containerAddObject.getPorts();
        Set<Integer> uniqueExternalPorts = new HashSet<>();
        Set<Integer> uniqueInternalPorts = new HashSet<>();
        Set<Integer> duplicatePorts = new HashSet<>();

        // 检查重复的外部端口和内部端口
        for (PortMapping portMapping : portMappings) {
            if (!uniqueExternalPorts.add(portMapping.getExternal())) {
                duplicatePorts.add(portMapping.getExternal());
            }
            if (!uniqueInternalPorts.add(portMapping.getInternal())) {
                duplicatePorts.add(portMapping.getInternal());
            }
        }

        if (!duplicatePorts.isEmpty()) {
            return Result.fail("输入端口号重复");
        }

        // 检查容器端口是否已经存在
        for (PortMapping portMapping : portMappings) {
            QueryWrapper<ContainerPort> queryWrapper1 = new QueryWrapper<>();
            Integer externalPort = portMapping.getExternal();
            queryWrapper1.eq("port", externalPort);
            Long count1 = containerPortMapper.selectCount(queryWrapper1);

            // 端口占用检测
            if (externalPort < 1024) {
                return Result.fail("不允许使用该范围端口!");
            }
            if (count1 != 0) {
                return Result.fail("端口已被占用！");
            }
        }
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        SysContainer sysContainer = new SysContainer();
        sysContainer.setName(containerAddObject.getName());
        sysContainer.setUserId(containerAddObject.getUserid());
        sysContainer.setStatus(0);
        // 生成的容器id这里写死了，后期需要改

        // int imageid = containerAddObject.getImageid();
        Image im = imageMapper.selectById(containerAddObject.getImageid());
        // 拿到extraConfig，即额外配置中的内容
        String config = containerAddObject.getExtraConfig();
        for(PortMapping portMapping : portMappings){
            config += " -p " + portMapping.getExternal() + ":" + portMapping.getInternal() + " ";
        }
        // runDocker函数已经将docker命令拼接好，把config填进去就好了
        String containerId = channelUtil.runDocker(config, sysContainer.getName(), im.getVersion());
        ; // 生成容器ID的逻辑需要根据实际需求修改
        containerId = containerId.substring(0, 12);
        // System.out.println(dockerService.runDocker(8014,"wdd",0));
        // String dockerID = dockerService.runDocker(sysContainer.getPort(),
        // sysContainer.getContainerName(), sysContainer.getVersionId()); // 异步启动容器
        // sysContainer.setContainerId(dockerID.substring(0,12));
        // System.out.println(dockerID);
        sysContainer.setContainerId(containerId);
        // sysContainer.setVersionId(containerAddObject.getVersionid());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        sysContainer.setCreateTime(sdf.format(new Date()));

        int insertResult = sysContainerMapper.insert(sysContainer);

        if (insertResult == 0) {
            channelUtil.rmDocker(sysContainer.getName());
            return Result.fail("容器创建失败");
        }

        // 为容器添加端口
        for (Integer port : uniqueExternalPorts) {
            ContainerPort containerPort = new ContainerPort();
            containerPort.setContainerId(containerId);
            containerPort.setPort(port);

            int portInsertResult = containerPortMapper.insert(containerPort);

            if (portInsertResult == 0) {
                return Result.fail("端口创建失败");
            }
        }
        return Result.ok(0, "添加成功");
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    }

    @Override
    public Result<List<SysContainer>> pageList(String querySearch, String value, Integer page, Integer limit) {
        IPage<SysContainer> iPage;

        if (page != null && limit != null) {
            iPage = new Page<>(page, limit);
        } else {
            // 如果 page 或 limit 为 null，创建一个不进行分页的 IPage 对象
            iPage = new Page<>();
        }

        QueryWrapper<SysContainer> queryWrapper = new QueryWrapper<>();
        // queryWrapper.eq(null != querySearch, "user_id", querySearch);
        // 查询条件
        if ("name".equals(querySearch)) {
            queryWrapper.like("name", value);
        } else if ("status".equals(querySearch)) {
            queryWrapper.eq("status", value);
        } else if ("version_id".equals(querySearch)) {
            queryWrapper.eq("version_id", value);
        } else if ("user_id".equals(querySearch)) {
            queryWrapper.eq("user_id", value);
        }
        queryWrapper.orderByAsc("id");
        iPage = sysContainerMapper.selectPage(iPage, queryWrapper);
        List<SysContainer> list = iPage.getRecords();
        if (list.isEmpty()) {
            return Result.fail("查询为空");
        }
        List<Integer> userIds = list.stream().map(SysContainer::getUserId).collect(Collectors.toList());
        List<Integer> versionIds = list.stream().map(SysContainer::getVersionId).collect(Collectors.toList());
        List<SysUser> userList = sysUserMapper.selectBatchIds(userIds);
        List<Image> versionList = imageMapper.selectBatchIds(versionIds);

        // 查询containerID对应的port列表，并存储在Map中
        Map<String, List<Integer>> containerPortsMap = new HashMap<>();
        List<String> containerIds = list.stream().map(SysContainer::getContainerId).collect(Collectors.toList());
        for (String containerId : containerIds) {
            // 获取containerId对应的containerport对象
            QueryWrapper<ContainerPort> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("container_id", containerId);
            List<ContainerPort> containerPorts = containerPortMapper.selectList(queryWrapper1);
            // 根据containerport对象获取port的list
            List<Integer> portList = containerPorts.stream()
                    .map(ContainerPort::getPort) // 使用map方法提取port属性
                    .collect(Collectors.toList());
            containerPortsMap.put(String.valueOf(containerId), portList);
        }
        // 把查出来的容器的关联对象user的信息填全
        for (SysContainer container : list) {
            for (SysUser user : userList) {
                if (user.getId().equals(container.getUserId())) {
                    container.setNickName(user.getNickName());
                    container.setUsername(user.getUsername());
                    break;
                }
            }
            for (Image version : versionList) {
                if (version.getId().equals(container.getVersionId())) {
                    container.setVersion(version.getVersion());
                    break;
                }
            }
            // 获取containerID对应的port列表
            List<Integer> portList = containerPortsMap.get(container.getContainerId());
            // 将port列表赋给SysContainer对象的port属性
            container.setPorts(portList);
        }
        return Result.page(Math.toIntExact(iPage.getTotal()), list);
    }

    @Override
    public Result delete(Integer id) {

        SysContainer container = sysContainerMapper.selectById(id);
        int i = 0;
        i = sysContainerMapper.deleteById(id);
        if (i == 0) {
            return Result.fail("删除失败！");
        }
        channelUtil.rmDocker(container.getName());
        return Result.ok("");
    }

    @Override
    public Result start(Integer id) {
        SysContainer container = sysContainerMapper.selectById(id);
        if (null == container) {
            return Result.fail("容器不存在！");
        }
        channelUtil.startDocker(container.getName());
        sysContainerMapper.updateStatus(1, id);
        return Result.ok();
    }

    @Override
    public Result stop(Integer id) {

        SysContainer container = sysContainerMapper.selectById(id);
        if (null == container) {
            return Result.fail("容器不存在！");
        }
        channelUtil.stopDocker(container.getName());
        sysContainerMapper.updateStatus(2, id);
        return Result.ok();
    }

    @Override
    public Result uploadFile(MultipartFile file, Integer id) {
        SysContainer container = sysContainerMapper.selectById(id);
        if (null == container) {
            return Result.fail("容器数据异常！");
        }
        String containerId = container.getContainerId();
        if (StringUtils.isBlank(containerId)) {
            return Result.fail("容器数据异常！");
        }
        String filename = file.getOriginalFilename();
        channelUtil.uploadFile(file);
        channelUtil.dockerUpload(containerId, filename);
        return Result.ok();
    }

    @Override
    public Result downloadFile(String path, Integer id) {
        SysContainer container = sysContainerMapper.selectById(id);
        if (null == container) {
            return Result.fail("容器数据异常！");
        }
        String containerId = container.getContainerId();
        if (StringUtils.isBlank(containerId)) {
            return Result.fail("容器数据异常！");
        }
        // utils相关download方法
        return Result.ok();
    }

    @Override
    public void handleDockerContainerId() {

    }
}
