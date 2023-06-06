package com.jhh.rossystem.service;

import com.jhh.rossystem.entity.SysUser;
import com.jhh.rossystem.utils.Result;

import java.util.List;

public interface SysUserService {
    Result register(SysUser sysUser);

    Result login(SysUser sysUser);

    Result<List<SysUser>> pageList(String querySearch, String value, Integer page, Integer limit);

    Result delete(Integer id);

    Result edit(SysUser sysUser);

    Result<SysUser> selectOne(Integer id);

    Result<List<SysUser>> selectList();
}
