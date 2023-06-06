package com.jhh.rossystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

@Data
public class ContainerVersion implements Serializable {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String version;

    private String createTime;

    private String content;

}
