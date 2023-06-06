package com.jhh.rossystem.controller.bao;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class RequestObject {
    private String querySearch;
    private String value;
    private Integer page;
    private Integer limit;
    private Integer id;

}
