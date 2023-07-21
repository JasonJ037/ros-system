package com.jhh.rossystem.service;

import com.jhh.rossystem.entity.Image;
import com.jhh.rossystem.utils.Result;

import java.util.List;

public interface ImageService {
    Result add(Image image);

    Result edit(Image image);

    Result<List<Image>> pageList(String querySearch,String value, Integer page, Integer limit);

    Result delete(Integer id);

    Result<List<Image>> list();

    Result<Image> selectOne(Integer id);
}
