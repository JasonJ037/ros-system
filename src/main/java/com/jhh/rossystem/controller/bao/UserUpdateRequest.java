package com.jhh.rossystem.controller.bao;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private String newpwd;
    private String oldpwd;
    private Integer user_id;

    private String username;
    private String nickName;
    private Integer role;
}
