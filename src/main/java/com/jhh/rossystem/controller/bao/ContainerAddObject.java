package com.jhh.rossystem.controller.bao;

import lombok.Data;

@Data
public class ContainerAddObject {
    private String name;
    private Integer userid;
    private Integer versionid;
    private Integer port;
    private String createTime;
}
