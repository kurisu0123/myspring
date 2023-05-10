package com.wuhao;

import com.myspring.core.MyApplicationContext;
import com.myspring.core.annotation.ComponentScan;
import com.wuhao.service.TestService;
import com.wuhao.service.UserService;

/**
 * 自定义配置类*/
@ComponentScan
public class MySpringApplication {
    public static void main(String[] args) {
        MyApplicationContext context = new MyApplicationContext(MySpringApplication.class);
//        UserService userService = (UserService) context.getBean("userService");
//        TestService testService = (TestService) context.getBean("testService");
    }
}
