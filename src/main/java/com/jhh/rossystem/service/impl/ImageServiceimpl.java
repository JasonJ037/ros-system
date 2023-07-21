package com.jhh.rossystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jhh.rossystem.entity.Image;
import com.jhh.rossystem.mapper.ImageMapper;
import com.jhh.rossystem.service.ImageService;
import com.jhh.rossystem.utils.Result;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Service
public class ImageServiceimpl implements ImageService {
    @Resource
    private ImageMapper imageMapper;

    // 测试提交
    @Override
    public Result add(Image image) {
        //创建时间
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        image.setCreateTime(sdf.format(new Date()));
        int i = imageMapper.insert(image);
        if (i != 1) {
            return Result.fail();
        }
        return Result.ok();
    }

    @Override
    public Result edit(Image image) {
        int i = imageMapper.updateById(image);
        if (i != 1) {
            return Result.fail();
        }
        return Result.ok();
    }

    @Override
    public Result<List<Image>> pageList(String querySearch,String value, Integer page, Integer limit) {
        IPage<Image> iPage = new Page<>(page, limit);
        QueryWrapper<Image> queryWrapper = new QueryWrapper<>();

        if("id".equals(querySearch)){
            queryWrapper.eq("id",value);
        }else if("version".equals(querySearch)){
            queryWrapper.like("version",value);
        }else if("content".equals(querySearch)){
            queryWrapper.like("content",value);
        }

        queryWrapper.orderByAsc("id");
        iPage=imageMapper.selectPage(iPage,queryWrapper);
        List<Image>list=iPage.getRecords();

        if (list.isEmpty()) {
            return Result.page(Math.toIntExact(iPage.getTotal()), list);
        }

        return Result.page(list);

//        queryWrapper.like(StringUtils.isNotBlank(version), "version", version);
//        queryWrapper.orderByDesc("id");
//        iPage = imageMapper.selectPage(iPage, queryWrapper);
//        List<Image> list = iPage.getRecords();
//        return Result.page(Math.toIntExact(iPage.getTotal()), list);
    }

    @Override
    public Result delete(Integer id) {
        int i = imageMapper.deleteById(id);
        if (i == 0) {
            return Result.fail("删除失败！");
        }
        return Result.ok();
    }

    @Override
    public Result<List<Image>> list() {
        List<Image> versionList = imageMapper.selectList(null);
        for (Image version: versionList ) {
            version.setVersion(version.getVersion() + "-" + version.getContent());
        }
        return Result.ok(versionList);
    }

    @Override
    public Result<Image> selectOne(Integer id) {
        return Result.ok(imageMapper.selectById(id));
    }
}
