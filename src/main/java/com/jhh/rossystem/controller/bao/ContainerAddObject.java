package com.jhh.rossystem.controller.bao;

import lombok.Data;

import java.util.List;

@Data
public class ContainerAddObject {
    private String name;
    private Integer userid;
    private Integer imageid;
    private Integer versionid;
    private List<Integer> ports;
    private String extraConfig;
}
