package com.jhh.rossystem.service.impl;

import com.jhh.rossystem.entity.ContainerVersion;
import com.jhh.rossystem.mapper.ContainerVersionMapper;
import com.jhh.rossystem.service.DockerService;
import com.jhh.rossystem.utils.ChannelUtil;
import com.jhh.rossystem.mapper.ContainerVersionMapper;
import com.jhh.rossystem.entity.ContainerVersion;
import com.jhh.rossystem.service.DockerService;
import com.jhh.rossystem.utils.ChannelUtil;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class DockerServiceImpl implements DockerService {

    @Resource
    private ChannelUtil channelUtil;

    @Resource
    private ContainerVersionMapper containerVersionMapper;

    @Async
    @Override
    public String runDocker(Integer port, String name, Integer versionId) {
        ContainerVersion version = containerVersionMapper.selectById(versionId);
        String dockerID = channelUtil.runDocker(port, name, version.getVersion());
        return dockerID;
    }

}
