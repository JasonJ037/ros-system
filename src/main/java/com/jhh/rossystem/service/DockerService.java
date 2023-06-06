package com.jhh.rossystem.service;

import org.springframework.scheduling.annotation.Async;

public interface DockerService {

    // 异步方法在相同的类下不生效
    @Async
    String runDocker(Integer port, String name, Integer versionId);

}
