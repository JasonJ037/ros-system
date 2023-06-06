package com.jhh.rossystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jhh.rossystem.controller.bao.ContainerAddObject;
import com.jhh.rossystem.entity.ContainerPort;
import com.jhh.rossystem.entity.ContainerVersion;
import com.jhh.rossystem.entity.SysContainer;
import com.jhh.rossystem.entity.SysUser;
import com.jhh.rossystem.mapper.ContainerPortMapper;
import com.jhh.rossystem.mapper.ContainerVersionMapper;
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
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
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
    private ContainerVersionMapper containerVersionMapper;
    @Override
    public Result add(ContainerAddObject containerAddObject) {
        QueryWrapper<SysContainer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", containerAddObject.getUserid());
        Integer count = Math.toIntExact(sysContainerMapper.selectCount(queryWrapper));
        //容器数量限制
        if (4 <= count) {
            return Result.fail("每个用户最多创建4个容器！");
        }

        QueryWrapper<ContainerPort> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("port",containerAddObject.getParams().getArr()[0]);

//        queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("port", sysContainer.getPort());
        count = Math.toIntExact(containerPortMapper.selectCount(queryWrapper1));
        //端口占用检测
        if(containerAddObject.getParams().getArr()[0] < 1024){
            return Result.fail("不允许使用该范围端口!");
        }
        if (!count.equals(0)) {
            return Result.fail("端口已被占用！");
        }

        containerAddObject.setContainerName(DockerUtil.generateName());
        //创建时间
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        containerAddObject.setCreateTime(sdf.format(new Date()));
        String dockerID = dockerService.runDocker(containerAddObject.getParams().getArr()[0], containerAddObject.getContainerName(), containerAddObject.getVersionid()); // 异步启动容器
        containerAddObject.setContainerId(dockerID.substring(0,12));

        SysContainer sysContainer=new SysContainer();
        sysContainer.setContainerId(containerAddObject.getContainerId());
        sysContainer.setContainerName(containerAddObject.getContainerName());
        sysContainer.setUserId(containerAddObject.getUserid());
        sysContainer.setVersionId(containerAddObject.getVersionid());
        sysContainer.setCreateTime(containerAddObject.getCreateTime());

        int i = sysContainerMapper.insert(sysContainer);
        if (i == 0) {
            return Result.fail("创建失败！");
        }
        return Result.ok("创建成功！"+dockerID);
    }

    @Override
    public Result<List<SysContainer>> pageList(String querySearch, String value, Integer page, Integer limit) {
        IPage<SysContainer> iPage = new Page<>(page, limit);
        QueryWrapper<SysContainer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(null != querySearch, "user_id", querySearch);
        // 查询条件
        if ("name".equals(querySearch)) {
            queryWrapper.like("name", value);
        } else if ("status".equals(querySearch)) {
            queryWrapper.eq("status", value);
        } else if ("version".equals(querySearch)) {
            queryWrapper.eq("version", value);
        } else if ("port".equals(querySearch)) {
            queryWrapper.eq("port", value);
        }
        queryWrapper.orderByAsc("id");
        iPage = sysContainerMapper.selectPage(iPage, queryWrapper);
        List<SysContainer> list = iPage.getRecords();
        if (list.isEmpty()) {
            return Result.page(Math.toIntExact(iPage.getTotal()), list);
        }
        List<Integer> userIds = list.stream().map(SysContainer::getUserId).collect(Collectors.toList());
        List<Integer> versionIds = list.stream().map(SysContainer::getVersionId).collect(Collectors.toList());
        List<SysUser> userList = sysUserMapper.selectBatchIds(userIds);
        List<ContainerVersion> versionList = containerVersionMapper.selectBatchIds(versionIds);
        for (SysContainer container : list) {
            for (SysUser user : userList) {
                if (user.getId().equals(container.getUserId())) {
                    container.setNickName(user.getNickName());
                    container.setUsername(user.getUsername());
                    break;
                }
            }
            for (ContainerVersion version : versionList) {
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
        channelUtil.startDocker(container.getContainerId());
        sysContainerMapper.updateStatus(1, id);
        return Result.ok();
    }

    @Override
    public Result stop(Integer id) {

        SysContainer container = sysContainerMapper.selectById(id);
        if (null == container) {
            return Result.fail("容器不存在！");
        }
        channelUtil.stopDocker(container.getContainerId());
        sysContainerMapper.updateStatus(2, id);
        return Result.ok();
    }

    @Override
    public Result uploadFile(MultipartFile file, Integer id) {
        return null;
    }

    @Override
    public void handleDockerContainerId() {

    }
}
