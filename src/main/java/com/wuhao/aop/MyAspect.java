package com.wuhao.aop;

import com.myspring.aop.annotation.After;
import com.myspring.aop.annotation.Aspect;
import com.myspring.aop.annotation.Before;
import com.myspring.aop.annotation.Pointcut;
import com.myspring.core.annotation.Component;

/**
 * Create by wuhao
 * Date: 2023/4/26
 * Time: 21:51
 */
@Aspect
@Component
public class MyAspect {

    @Pointcut("aaa")
    public void pointcut() {

    }

    @Before("pointcut()")
    public void before(){

    }
    @After("pointcut()")
    public void after(){

    }

    public void ada(){

    }
}
