package com.jhh.rossystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jhh.rossystem.controller.bao.UserUpdateRequest;
import com.jhh.rossystem.entity.SysUser;
import com.jhh.rossystem.mapper.SysContainerMapper;
import com.jhh.rossystem.mapper.SysUserMapper;
import com.jhh.rossystem.service.SysUserService;
import com.jhh.rossystem.utils.Base64Util;
import com.jhh.rossystem.utils.Result;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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
//        if(StringUtils.isNotBlank(sysUser.getPassword()) && StringUtils.isNotBlank(sysUser.getPasswordCf())){
//            if(!sysUser.getPassword().equals(sysUser.getPasswordCf())){
//                return Result.fail("两次密码不一致,请重试！");
//            }
//        }
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
        return Result.ok(0, "成功",user);
    }

    @Override
    public Result<List<SysUser>> pageList(String querySearch, String value, Integer page, Integer limit) {
        IPage<SysUser> iPage;

        if (page != null && limit != null) {
            iPage = new Page<>(page, limit);
        } else {
            // 如果 page 或 limit 为 null，创建一个不进行分页的 IPage 对象
            iPage = new Page<>();
        }
    
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role", 1);
        // 查询条件
        if ("username".equals(querySearch)) {
            queryWrapper.like("username", value);
        } else if ("nick_name".equals(querySearch)) {
            queryWrapper.like("nick_name", value);
        } //else if ("role".equals(querySearch)) {
        //    queryWrapper.eq("role", value);
        //}
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
    public Result edit(UserUpdateRequest request) {
        SysUser loggedInUser = (SysUser) session.getAttribute("user");

        if (loggedInUser != null) {
            // 进行权限判断
            if (isAdminUser(loggedInUser)) {
                // 管理员权限，允许编辑用户信息
                SysUser sysUser = sysUserMapper.selectById(request.getUser_id());
                if (sysUser==null){
                    return Result.fail("用户不存在");
                }
                //编辑用户信息
                updateUserFields(request, sysUser);

                int i = sysUserMapper.updateById(sysUser);
                if(i==0){
                    return Result.fail("用户信息编辑失败！");
                }

                return Result.ok("用户信息编辑成功！");

            } else {
                //用户权限，允许用户修改密码
                String s = Base64Util.dEncrypt(loggedInUser.getPassword());
                if(!request.getOldpwd().equals(s))
                {
                    return Result.fail("旧密码错误");
                }else if(request.getOldpwd().equals(request.getNewpwd())){
                    return Result.fail("新密码不能与旧密码相同");
                }

                String password = Base64Util.encrypt(request.getNewpwd());
                loggedInUser.setPassword(password);
                int i = sysUserMapper.updateById(loggedInUser);
                if (i == 0) {
                    return Result.fail("修改失败！");
                }
                return Result.ok();
            }
        } else {
            return Result.fail("用户未登录或Session已过期，请重新登录！");
        }
    }


    @Override
    public Result<SysUser> selectOne(Integer id) {
        SysUser sysUser = sysUserMapper.selectById(id);
        if(sysUser==null){
            return Result.fail("查询失败，没有该用户");
        }
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

    private boolean isAdminUser(SysUser user) {
        return user.getRole()==1;
    }

    private void updateUserFields(UserUpdateRequest request, SysUser user) {
        if (request.getUsername() != null) {
            user.setUsername(request.getUsername());
        }
        if (request.getNickName() != null) {
            user.setNickName(request.getNickName());
        }
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
        if (request.getNewpwd() != null) {
            String password = Base64Util.encrypt(request.getNewpwd());
            user.setPassword(password);
        }
    }

}
