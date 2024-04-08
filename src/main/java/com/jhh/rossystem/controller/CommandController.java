package com.jhh.rossystem.controller;
import javax.annotation.Resource;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.jhh.rossystem.utils.Result;
import com.jhh.rossystem.controller.bao.RequestObject;
import com.jhh.rossystem.service.CommandService;


@RestController
@CrossOrigin
@RequestMapping("/api/cmd")
public class CommandController {
    
    @Resource
    private CommandService commandService;
    
    // 查询指令
    @RequestMapping("/query")
    public Result queryCommand(@RequestBody RequestObject request) {
        return commandService.queryCommand(request.getCmd());
    }   
}
