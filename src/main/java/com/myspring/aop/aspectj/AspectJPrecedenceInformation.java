package com.myspring.aop.aspectj;

/**
 * Create by wuhao
 * Date: 2023/5/5
 * Time: 22:10
 */
public interface AspectJPrecedenceInformation {
    String getAspectName();

    boolean isBeforeAdvice();

    boolean isAfterAdvice();
}
