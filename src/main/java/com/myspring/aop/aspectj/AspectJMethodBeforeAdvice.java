package com.myspring.aop.aspectj;

import com.myspring.AspectJExpressionPointcut;

import java.lang.reflect.Method;

/**
 * Create by wuhao
 * Date: 2023/5/5
 * Time: 22:15
 */
public class AspectJMethodBeforeAdvice extends AbstractAspectJAdvice{


    public AspectJMethodBeforeAdvice(Method aspectJAdviceMethod, AspectJExpressionPointcut pointcut, Class<?> aspectClass) {
        super(aspectJAdviceMethod, pointcut,aspectClass);
    }
    public boolean isBeforeAdvice() {
        return true;
    }

    public boolean isAfterAdvice() {
        return false;
    }
}
