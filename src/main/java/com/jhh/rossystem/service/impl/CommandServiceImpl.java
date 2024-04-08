package com.jhh.rossystem.service.impl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import com.jhh.rossystem.service.CommandService;
import com.jhh.rossystem.utils.Result;
import com.jhh.rossystem.utils.ChannelUtil;

@Service
public class CommandServiceImpl implements CommandService{

    @Resource
    private ChannelUtil channelUtil;
    
    @Async
    @Override
    public Result<String> queryCommand(String command){
        // 执行命令并返回结果
        String res = channelUtil.executeCommand(command);
        return Result.ok("成功",res);
    }
}
