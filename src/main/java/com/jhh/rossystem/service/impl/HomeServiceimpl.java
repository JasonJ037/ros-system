package com.jhh.rossystem.service.impl;

import com.jhh.rossystem.entity.HomeData;
import com.jhh.rossystem.mapper.SysContainerMapper;
import com.jhh.rossystem.mapper.SysUserMapper;
import com.jhh.rossystem.service.HomeService;
import com.jhh.rossystem.utils.Result;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class HomeServiceimpl implements HomeService {
    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private SysContainerMapper sysContainerMapper;

    @Override
    public Result<HomeData> getData() {
        HomeData homeData = new HomeData();
        homeData.setUserCount(Math.toIntExact(sysUserMapper.selectCount(null)));
        homeData.setContainerCount(Math.toIntExact(sysContainerMapper.selectCount(null)));
        return Result.ok(homeData);
    }
}
