package com.jhh.rossystem.controller;

import com.jhh.rossystem.controller.bao.ContainerAddObject;
import com.jhh.rossystem.controller.bao.RequestObject;
import com.jhh.rossystem.entity.SysContainer;
import com.jhh.rossystem.entity.SysUser;
import com.jhh.rossystem.service.SysContainerService;
import com.jhh.rossystem.utils.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/container")
public class ContainerController {

    @Resource
    private SysContainerService sysContainerService;

    @Resource
    private HttpSession session;

    @PostMapping("/list")
    public Result<List<SysContainer>> pageList(@RequestBody RequestObject request){
       return sysContainerService.pageList(request.getQuerySearch(), request.getValue(), request.getPage(), request.getLimit());
    }

    @PostMapping("/delete")
    public Result delete(@RequestBody RequestObject request) {
        return sysContainerService.delete(request.getId());
    }

    @PostMapping("/add")
    public  Result add(@RequestBody ContainerAddObject containerAddObject)
    {
        SysUser user = (SysUser) session.getAttribute("user");
        if (user.getRole().equals(2)) {
            containerAddObject.setUserid(user.getId());
        }
        return sysContainerService.add(containerAddObject);
    }

    @PostMapping("/start")
    public Result start(@RequestBody Map<String,Object> requestData){
        return sysContainerService.start((int)requestData.get("id"));
    }


    @PostMapping("/stop")
    public Result stop(@RequestBody Map<String,Object> requestData){
        return sysContainerService.stop((int)requestData.get("id"));
    }

    @PostMapping("/upload")
    public Result upload(@RequestBody Map<String,Object> requestData){
        return null;
    }

    @PostMapping("/download")
    public Result download(@RequestBody Map<String,Object> requestData){
        return null;
    }


}
