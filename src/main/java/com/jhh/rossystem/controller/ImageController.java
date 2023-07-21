package com.jhh.rossystem.controller;

import com.jhh.rossystem.controller.bao.RequestObject;
import com.jhh.rossystem.entity.Image;
import com.jhh.rossystem.service.ImageService;
import com.jhh.rossystem.utils.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("api/image")
public class ImageController {

    @Resource
    private ImageService imageService;


    @PostMapping(value = "/list")
    public Result<List<Image>> pageList(@RequestBody RequestObject requestObject) {
        return imageService.pageList(requestObject.getQuerySearch(),requestObject.getValue(), requestObject.getPage(), requestObject.getLimit());
    }

    @PostMapping(value = "/info")
    public Result<Image> selectOne(@RequestBody RequestObject requestObject) {
        return imageService.selectOne(requestObject.getId());
    }

    @PostMapping("/add")
    public Result add(@Valid @RequestBody Image image) {
        return imageService.add(image);
    }

    @PostMapping(value = "/delete")
    public Result delete(@RequestBody RequestObject requestObject) {
        return imageService.delete(requestObject.getId());
    }

    @PostMapping("/edit")
    public Result edit(@Valid @RequestBody Image image) {
        return imageService.edit(image);
    }
}
