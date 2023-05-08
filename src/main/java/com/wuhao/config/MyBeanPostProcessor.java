package com.wuhao.config;

import com.myspring.beans.factory.InstantiationAwareBeanPostProcessor;
import com.myspring.core.annotation.Component;
import com.sun.istack.internal.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Create by wuhao
 * Date: 2023/4/19
 * Time: 21:40
 */
@Component
public class MyBeanPostProcessor implements InstantiationAwareBeanPostProcessor {

    @Nullable
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws RuntimeException {
        System.out.println("qqeqwe");
        return bean;
    }

    @Nullable
    public Object postProcessAfterInitialization(Object bean, String beanName) throws RuntimeException {
        System.out.println("12313");
        return bean;
    }
}
