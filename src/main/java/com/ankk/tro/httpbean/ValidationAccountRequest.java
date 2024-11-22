package com.ankk.tro.httpbean;

import lombok.Data;

@Data
public class ValidationAccountRequest {
    private long iduser;
    private String code;
}
