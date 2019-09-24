package com.hfjy.eureka.gateway.model;

import lombok.Data;

@Data
public class UserInfo {
    private Integer userId;
    private String userName;

    public UserInfo(Integer userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }
}
