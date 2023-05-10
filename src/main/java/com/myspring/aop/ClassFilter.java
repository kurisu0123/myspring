package com.myspring.aop;

/**
 * Create by wuhao
 * Date: 2023/5/10
 * Time: 23:07
 */
public interface ClassFilter {
    boolean matches(Class<?> clazz);
}
