package com.jhh.rossystem.controller.bao;

import lombok.Data;

import java.util.List;

import com.jhh.rossystem.entity.PortMapping;

@Data
public class ContainerAddObject {
    private String name;
    private Integer userid;
    private Integer imageid;
    private List<PortMapping> ports;
    private String extraConfig;
}
