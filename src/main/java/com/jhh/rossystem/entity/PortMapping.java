package com.jhh.rossystem.entity;

public class PortMapping {
    private Integer external;
    private Integer internal;

    // 构造函数、getter 和 setter 方法
    public PortMapping() {}

    public Integer getExternal() {
        return external;
    }

    public void setExternal(Integer external) {
        this.external = external;
    }

    public Integer getInternal() {
        return internal;
    }

    public void setInternal(Integer internal) {
        this.internal = internal;
    }

}