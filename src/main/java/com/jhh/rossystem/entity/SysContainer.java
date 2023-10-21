package com.jhh.rossystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

@Data
@TableName("sys_container")
public class SysContainer implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;

    @NotBlank(message = "请输入名字")
    @TableField("name")
    private String name;

    @TableField("user_id")
    private Integer userId;

    @TableField(exist = false)
    private String username;

    @TableField(exist = false)
    private String nickName;

    private Integer status;

    @TableField("container_id")
    private String containerId;

    @TableField(exist = false)
    private String containerName;

    @TableField("creat_time")
    private String createTime;

    @TableField("version_id")
    private Integer versionId;

    @TableField(exist = false)
    private String version;

    @TableField(exist = false)
    private List<Integer> ports;
}
