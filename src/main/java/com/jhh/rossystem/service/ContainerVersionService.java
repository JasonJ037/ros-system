package com.jhh.rossystem.service;

import com.jhh.rossystem.entity.ContainerVersion;
import com.jhh.rossystem.utils.Result;

import java.util.List;

public interface ContainerVersionService {
    Result add(ContainerVersion containerVersion);

    Result edit(ContainerVersion containerVersion);

    Result<List<ContainerVersion>> pageList(String version, Integer page, Integer limit);

    Result delete(Integer id);

    Result<List<ContainerVersion>> list();

    Result<ContainerVersion> selectOne(Integer id);
}
