package com.jhh.rossystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

@Data
public class Image implements Serializable {

    @TableId(type = IdType.AUTO)
    private Integer id;
    @TableField("version")
    private String version;

    @TableField("create_time")
    private String createTime;

    @TableField("content")
    private String content;

}
