package com.jhh.rossystem.interceptor;

import com.jhh.rossystem.entity.SysUser;
import org.springframework.web.servlet.HandlerInterceptor;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginInterceptor implements HandlerInterceptor {

    @Resource
    private HttpSession session;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Object o = session.getAttribute("user");
        //如果session中获得的对象o不是SysUser类型的，则去if里面，里面是处理登录失败的逻辑
        if (!(o instanceof SysUser)) {
            response.sendRedirect("login.html");
            return false;
        }
        return true;
    }


}
