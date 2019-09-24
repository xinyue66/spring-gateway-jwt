package com.hfjy.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


@RestController
@Slf4j
public class BaseController {

    @Autowired
    private HttpServletRequest request;

    public void getHeaders(){
        log.info("userId:{}",request.getHeader("userId"));
        log.info("userName:{}",request.getHeader("userName"));
    }

}
