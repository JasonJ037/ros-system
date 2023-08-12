package com.jhh.rossystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class SysUser implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

//    @TableField(exist = false)
//    private String passwordCf;

    private String nickName;

    private Integer role;

    @TableField(exist = false)
    private Integer containerCount = 0;

    private String registerTime;

//    @TableField(exist = false)
//    private Integer groupid;
}
