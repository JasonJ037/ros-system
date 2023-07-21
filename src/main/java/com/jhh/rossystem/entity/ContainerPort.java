package com.jhh.rossystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

@Data
public class ContainerPort implements Serializable {

    @TableId(type= IdType.AUTO)
    private Integer id;

    private Integer port;

    private String containerId;
}
