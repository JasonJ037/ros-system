package com.jhh.rossystem.controller.bao;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Data
public class RequestObject {
    private String querySearch;
    private String value;
    private Integer page;
    private Integer limit;

    private Integer id;
    private String cmd;
    private MultipartFile file;

}
