package com.jhh.rossystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

@Data
public class ContainerPort implements Serializable {
    private Integer id;

    @TableId(type= IdType.AUTO)
    private Integer port;


}
