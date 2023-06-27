package com.jhh.rossystem.service;

import com.jhh.rossystem.controller.bao.ContainerAddObject;
import com.jhh.rossystem.entity.SysContainer;
import com.jhh.rossystem.utils.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

public interface SysContainerService {
    Result add(ContainerAddObject containerAddObject);

    Result<List<SysContainer>> pageList(String querySearch, String value, Integer page, Integer limit);

    Result delete(Integer id);

    Result start(Integer id);

    Result stop(Integer id);

    Result uploadFile(MultipartFile file, Integer id);

    Result downloadFile(String path, Integer id);

    void handleDockerContainerId();
}
