package com.jhh.rossystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class SysContainer implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;

    @NotBlank(message = "请输入名字")
    private String name;

    private Integer userId;

    @TableField(exist = false)
    private String username;

    @TableField(exist = false)
    private String nickName;

    private Integer status;

    private String containerId;

    private String containerName;

    private String createTime;

    private Integer versionId;

    @TableField(exist = false)
    private String version;
}
