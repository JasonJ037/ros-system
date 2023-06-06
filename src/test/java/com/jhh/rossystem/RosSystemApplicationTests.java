package com.jhh.rossystem;

import com.jhh.rossystem.entity.SysUser;
import com.jhh.rossystem.mapper.SysUserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RosSystemApplicationTests {

    @Autowired
    private SysUserMapper sysUserMapper;


    @Test
    void contextLoads() {
        SysUser sysUser=sysUserMapper.selectById(1);
        System.out.println(sysUser);


    }

}
