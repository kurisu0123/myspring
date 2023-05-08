package com.myspring.aop.aspectj;

/**
 * Create by wuhao
 * Date: 2023/5/8
 * Time: 23:29
 */
public interface ProceedingJoinPoint extends JoinPoint {

    Object proceed() throws Throwable;

    Object proceed(Object[] var1) throws Throwable;
}
