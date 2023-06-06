package com.jhh.rossystem.controller;

import com.jhh.rossystem.controller.bao.RequestObject;
import com.jhh.rossystem.entity.SysUser;
import com.jhh.rossystem.service.SysUserService;
import com.jhh.rossystem.utils.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Resource
    private SysUserService sysUserService;

    @Resource
    private HttpSession session;

    //查看用户列表
    @PostMapping("/list")
    public Result<List<SysUser>> pagelist(@RequestBody RequestObject request){
        System.out.println("我接收到的东西"+request);
        return sysUserService.pageList(request.getQuerySearch(),request.getValue(), request.getPage(), request.getLimit());
    }

    //查看某用户信息
    @PostMapping("/info")
    public Result<SysUser> info(@RequestBody RequestObject request){
        return sysUserService.selectOne(request.getId());
    }

    //新增用户
    @PostMapping("/add")
    public Result add(@Valid @RequestBody SysUser user){
        return sysUserService.register(user);
    }

    //删除用户
    @PostMapping("/delete")
    public Result delete(@RequestBody RequestObject request) {
        return sysUserService.delete(request.getId());
    }

    //编辑用户
    @PostMapping("/edit")
    public Result edit(@Valid @RequestBody SysUser user){
        return sysUserService.edit(user);
    }

    @PostMapping("/login")
    public Result login(@Valid @RequestBody SysUser user) {
        return sysUserService.login(user);
    }






}
