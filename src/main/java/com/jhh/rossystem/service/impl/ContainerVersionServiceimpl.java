package com.jhh.rossystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jhh.rossystem.entity.ContainerVersion;
import com.jhh.rossystem.mapper.ContainerVersionMapper;
import com.jhh.rossystem.service.ContainerVersionService;
import com.jhh.rossystem.utils.Result;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Service
public class ContainerVersionServiceimpl implements ContainerVersionService {
    @Resource
    private ContainerVersionMapper containerVersionMapper;

    // 测试提交
    @Override
    public Result add(ContainerVersion containerVersion) {
        //创建时间
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        containerVersion.setCreateTime(sdf.format(new Date()));
        int i = containerVersionMapper.insert(containerVersion);
        if (i != 1) {
            return Result.fail();
        }
        return Result.ok();
    }

    @Override
    public Result edit(ContainerVersion containerVersion) {
        int i = containerVersionMapper.updateById(containerVersion);
        if (i != 1) {
            return Result.fail();
        }
        return Result.ok();
    }

    @Override
    public Result<List<ContainerVersion>> pageList(String version, Integer page, Integer limit) {
        IPage<ContainerVersion> iPage = new Page<>(page, limit);
        QueryWrapper<ContainerVersion> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(version), "version", version);
        queryWrapper.orderByDesc("id");
        iPage = containerVersionMapper.selectPage(iPage, queryWrapper);
        List<ContainerVersion> list = iPage.getRecords();
        return Result.page(Math.toIntExact(iPage.getTotal()), list);
    }

    @Override
    public Result delete(Integer id) {
        int i = containerVersionMapper.deleteById(id);
        if (i == 0) {
            return Result.fail("删除失败！");
        }
        return Result.ok();
    }

    @Override
    public Result<List<ContainerVersion>> list() {
        List<ContainerVersion> versionList = containerVersionMapper.selectList(null);
        for (ContainerVersion version: versionList ) {
            version.setVersion(version.getVersion() + "-" + version.getContent());
        }
        return Result.ok(versionList);
    }

    @Override
    public Result<ContainerVersion> selectOne(Integer id) {
        return Result.ok(containerVersionMapper.selectById(id));
    }
}
