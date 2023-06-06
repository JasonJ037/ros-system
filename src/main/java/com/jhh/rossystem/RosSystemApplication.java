package com.jhh.rossystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.jhh.rossystem.controller","com.jhh.rossystem.service","com.jhh.rossystem.config","com.jhh.rossystem.entity","com.jhh.rossystem.exception","com.jhh.rossystem.interceptor","com.jhh.rossystem.mapper","com.jhh.rossystem.utils"})
public class RosSystemApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        //String port = "6080";
        SpringApplication.run(RosSystemApplication.class, args);
        //String cmd = "ssh -p 20023 root@183.237.172.243 -f -N -L 3400:127.0.0.1:"+ port;
        //CmdUtil.execute(cmd);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        // 注意这里要指向原先用main方法执行的Application启动类
        return builder.sources(RosSystemApplication.class);
    }
}

