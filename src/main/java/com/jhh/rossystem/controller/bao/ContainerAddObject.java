package com.jhh.rossystem.controller.bao;

import lombok.Data;

@Data
public class ContainerAddObject {
    private String name;
    private Integer userid;
    private Integer versionid;
    private Param params;

    private String containerName;

    private String createTime;

    private String containerId;

}
