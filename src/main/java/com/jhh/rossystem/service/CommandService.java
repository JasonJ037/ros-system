package com.jhh.rossystem.service;
import org.springframework.scheduling.annotation.Async;

import com.jhh.rossystem.utils.Result;


public interface CommandService {

    @Async
    Result<String> queryCommand(String command);    
}
