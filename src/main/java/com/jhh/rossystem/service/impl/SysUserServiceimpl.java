package com.jhh.rossystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jhh.rossystem.entity.SysUser;
import com.jhh.rossystem.mapper.SysContainerMapper;
import com.jhh.rossystem.mapper.SysUserMapper;
import com.jhh.rossystem.service.SysUserService;
import com.jhh.rossystem.utils.Base64Util;
import com.jhh.rossystem.utils.Result;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

@Service
public class SysUserServiceimpl implements SysUserService {

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private SysContainerMapper sysContainerMapper;

    @Resource
    private HttpSession session;

    @Override
    public Result register(SysUser sysUser) {
        SysUser user = queryByUserName(sysUser.getUsername());
        if (null != user) {
            return Result.fail("用户名已经存在！");
        }
        // 密码与确认密码比较
        if(StringUtils.isNotBlank(sysUser.getPassword()) && StringUtils.isNotBlank(sysUser.getPasswordCf())){
            if(!sysUser.getPassword().equals(sysUser.getPasswordCf())){
                return Result.fail("两次密码不一致,请重试！");
            }
        }
        if (StringUtils.isBlank(sysUser.getPassword())) {
            sysUser.setPassword("123456");
        } else{
            sysUser.setPassword(sysUser.getPassword());
        }

        if (StringUtils.isBlank(sysUser.getNickName())) {
            sysUser.setNickName("用户" + sysUser.getUsername());
        } else {
            sysUser.setNickName((sysUser.getNickName()));
        }
        //密码加密
        String base64encodedString = Base64Util.encrypt(sysUser.getPassword());
        sysUser.setPassword(base64encodedString);
        //注册时间
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        sysUser.setRegisterTime(sdf.format(new Date()));
        int i = sysUserMapper.insert(sysUser);
        if (i == 0) {
            return Result.fail("注册失败！");
        }
        return Result.ok(sysUser.getId());
    }

    @Override
    public Result login(SysUser sysUser) {
        SysUser user = queryByUserName(sysUser.getUsername());
        if (null == user) {
            return Result.fail("用户名不存在！");
        }

        String password = Base64Util.dEncrypt(user.getPassword());

        if (!password.equals(sysUser.getPassword())) {
            return Result.fail("密码错误！");
        }
        session.setAttribute("user", user);
        return Result.ok();
    }

    @Override
    public Result<List<SysUser>> pageList(String querySearch, String value, Integer page, Integer limit) {
        IPage<SysUser> iPage = new Page<>(page, limit);
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role", 2);
        // 查询条件
        if ("username".equals(querySearch)) {
            queryWrapper.like("username", value);
        } else if ("nick_name".equals(querySearch)) {
            queryWrapper.like("nick_name", value);
        } else if ("role".equals(querySearch)) {
            queryWrapper.eq("role", value);
        }
        queryWrapper.orderByAsc("id");
        iPage = sysUserMapper.selectPage(iPage, queryWrapper);
        List<SysUser> list = iPage.getRecords();
        if (!list.isEmpty()) {
            List<Integer> userIds = list.stream().map(SysUser::getId).collect(Collectors.toList());
            List<SysUser> users = sysContainerMapper.selectCountByUserIds(userIds);
            for (SysUser sysUser : list) {
                for (SysUser user : users) {
                    if (sysUser.getId().equals(user.getId())) {
                        sysUser.setContainerCount(user.getContainerCount());
                        break;
                    }
                }
            }
        }
        return Result.page(list.size(),list);
    }

    @Override
    public Result delete(Integer id) {
        int i = sysUserMapper.deleteById(id);
        if (i == 0) {
            return Result.fail("删除失败！");
        }
        return Result.ok();
    }

    @Override
    public Result edit(SysUser sysUser) {
        String password = Base64Util.encrypt(sysUser.getPassword());
        sysUser.setPassword(password);
        int i = sysUserMapper.updateById(sysUser);
        if (i == 0) {
            return Result.fail("修改失败！");
        }
        return Result.ok();
    }

    @Override
    public Result<SysUser> selectOne(Integer id) {
        SysUser sysUser = sysUserMapper.selectById(id);
        String password = Base64Util.dEncrypt(sysUser.getPassword());
        sysUser.setPassword(password);
        return Result.ok(sysUser);
    }

    @Override
    public Result<List<SysUser>> selectList() {
        return null;
    }

    private SysUser queryByUserName(String username) {
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return sysUserMapper.selectOne(queryWrapper);
    }

}
