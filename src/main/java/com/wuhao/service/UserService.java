package com.wuhao.service;

import com.myspring.core.annotation.Autowired;
import com.myspring.core.annotation.Component;

@Component
public class UserService {

//    @Autowired
    public TestService testService;

    private String name = "wuhao";
    public String getUserName(){
        System.out.println("name : "+name);
        return this.name;
    }
}
