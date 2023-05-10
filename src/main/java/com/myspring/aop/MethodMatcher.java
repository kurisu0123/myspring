package com.myspring.aop;

import java.lang.reflect.Method;

/**
 * Create by wuhao
 * Date: 2023/5/10
 * Time: 23:00
 */
public interface MethodMatcher {
    boolean matches(Method method, Class<?> targetClass);

    boolean isRuntime();

    boolean matches(Method method, Class<?> targetClass, Object... args);
}
