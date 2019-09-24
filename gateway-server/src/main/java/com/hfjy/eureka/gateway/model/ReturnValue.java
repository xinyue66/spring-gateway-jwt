package com.hfjy.eureka.gateway.model;

import lombok.Data;

@Data
public class ReturnValue<T> {
    private int code;
    private String message = "";
    private T data;

    public ReturnValue(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
