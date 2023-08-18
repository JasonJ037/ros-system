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
import com.jhh.rossystem.mapper.ContainerPortMapper;
import com.jhh.rossystem.mapper.ImageMapper;
import com.jhh.rossystem.mapper.SysContainerMapper;
import com.jhh.rossystem.mapper.SysUserMapper;
import com.jhh.rossystem.service.DockerService;
import com.jhh.rossystem.service.SysContainerService;
import com.jhh.rossystem.utils.ChannelUtil;
import com.jhh.rossystem.utils.DockerUtil;
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

        SysContainer sysContainer = sysContainerMapper.selectOne(queryWrapper);

        Long count = sysContainerMapper.selectCount(queryWrapper);
        //容器数量限制
        if (4 <= count) {
            return Result.fail("每个用户最多创建4个容器！");
        }

        //取出端口号
        List<Integer> ports = containerAddObject.getPorts();
        //判断用户输入的端口号是否有重复
        Set<Integer> uniquePorts = new HashSet<>();
        Set<Integer> duplicatePorts = new HashSet<>();

        for (Integer port : ports) {
            if (!uniquePorts.add(port)) {
                duplicatePorts.add(port);
            }
        }

        if (!duplicatePorts.isEmpty()) {
            return Result.fail("输入端口号重复");
        }

        //检查容器端口是否已经存在
        for(Integer port : ports){
            QueryWrapper<ContainerPort> queryWrapper1=new QueryWrapper<>();
            queryWrapper1.eq("port", port);
            Long count1 = containerPortMapper.selectCount(queryWrapper1);

            //端口占用检测
            if(port < 1024){
                return Result.fail("不允许使用该范围端口!");
            }
            if (count1!=0) {
                return Result.fail("端口已被占用！");
            }
        }

        //如果容器不存在，先创建一个容器
        if(sysContainer==null)
        {
            SysContainer sysContainer1 = new SysContainer();
            sysContainer1.setName(containerAddObject.getName());
            sysContainer1.setUserId(containerAddObject.getUserid());
            sysContainer1.setStatus(0);
            //生成的容器id这里写死了，后期需要改
            String containerid="46567asd";
            sysContainer1.setContainerId(containerid);
            sysContainer1.setVersionId(containerAddObject.getVersionid());

            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
            sysContainer1.setCreateTime(sdf.format(new Date()));

            int i1 = sysContainerMapper.insert(sysContainer1);
            if(i1==0) return Result.fail("失败");



            for(Integer port: ports){
                ContainerPort containerPort = new ContainerPort();
                containerPort.setContainerId(containerid);
                containerPort.setPort(port);

                int i2 = containerPortMapper.insert(containerPort);

                if(i2==0) return Result.fail("失败");
            }

        }else {
            //容器存在，先取出容器的id
            String containerid = sysContainer.getContainerId();

            for(Integer port: ports){
                ContainerPort containerPort = new ContainerPort();
                containerPort.setContainerId(containerid);
                containerPort.setPort(port);

                int i2 = containerPortMapper.insert(containerPort);

                if(i2==0) return Result.fail("失败");
            }
        }

        return Result.ok(0,"");

    }

    @Override
    public Result<List<SysContainer>> pageList(String querySearch, String value, Integer page, Integer limit) {
        IPage<SysContainer> iPage = new Page<>(page, limit);
        QueryWrapper<SysContainer> queryWrapper = new QueryWrapper<>();
        //queryWrapper.eq(null != querySearch, "user_id", querySearch);
        // 查询条件
        if ("name".equals(querySearch)) {
            queryWrapper.like("name", value);
        } else if ("status".equals(querySearch)) {
            queryWrapper.eq("status", value);
        } else if ("version_id".equals(querySearch)) {
            queryWrapper.eq("version_id", value);
        } else if("user_id".equals(querySearch)){
            queryWrapper.eq("user_id",value);
        }
        queryWrapper.orderByAsc("id");
        iPage = sysContainerMapper.selectPage(iPage,queryWrapper);
        List<SysContainer> list = iPage.getRecords();
        if (list.isEmpty()) {
            System.out.println("fail123");
            return Result.fail("查询为空");
        }
        List<Integer> userIds = list.stream().map(SysContainer::getUserId).collect(Collectors.toList());
        List<Integer> versionIds = list.stream().map(SysContainer::getVersionId).collect(Collectors.toList());
        List<SysUser> userList = sysUserMapper.selectBatchIds(userIds);
        List<Image> versionList = imageMapper.selectBatchIds(versionIds);
        //把查出来的容器的关联对象user的信息填全
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
        }
        return Result.page(Math.toIntExact(iPage.getTotal()), list);
    }

    @Override
    public Result delete(Integer id) {
        int i = 0;
        i = sysContainerMapper.deleteById(id);
        if (i == 0) {
            return Result.fail("删除失败！");
        }
        return Result.ok("");
    }

    @Override
    public Result start(Integer id) {
        SysContainer container = sysContainerMapper.selectById(id);
        if (null == container) {
            return Result.fail("容器不存在！");
        }
        //channelUtil.startDocker(container.getContainerId());
        sysContainerMapper.updateStatus(1, id);
        return Result.ok();
    }

    @Override
    public Result stop(Integer id) {

        SysContainer container = sysContainerMapper.selectById(id);
        if (null == container) {
            return Result.fail("容器不存在！");
        }
//        channelUtil.stopDocker(container.getContainerId());
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
//        channelUtil.uploadFile(file);
//        channelUtil.dockerCp(containerId, filename);
        return Result.ok();
    }

    public Result downloadFile(String path,Integer id) {
        SysContainer container = sysContainerMapper.selectById(id);
        if (null == container) {
            return Result.fail("容器数据异常！");
        }
        String containerId = container.getContainerId();
        if (StringUtils.isBlank(containerId)) {
            return Result.fail("容器数据异常！");
        }
        //utils相关download方法
        return Result.ok();
    }

    @Override
    public void handleDockerContainerId() {

    }
}
