package com.myspring.aop.aspectj;

import com.myspring.AspectJExpressionPointcut;
import com.myspring.aop.Advice;

import java.lang.reflect.Method;

/**
 * Create by wuhao
 * Date: 2023/5/5
 * Time: 22:09
 */
public abstract class AbstractAspectJAdvice implements Advice, AspectJPrecedenceInformation {
    private final Class<?> declaringClass;
    private final String methodName;
    private final Class<?>[] parameterTypes;
    private final Class<?> aspectClass;
    protected transient Method aspectJAdviceMethod;
    private final AspectJExpressionPointcut pointcut;
    private String aspectName = "";
    public AbstractAspectJAdvice(Method aspectJAdviceMethod, AspectJExpressionPointcut pointcut, Class<?> aspectClass) {
        this.declaringClass = aspectJAdviceMethod.getDeclaringClass();
        this.methodName = aspectJAdviceMethod.getName();
        this.parameterTypes = aspectJAdviceMethod.getParameterTypes();
        this.aspectJAdviceMethod = aspectJAdviceMethod;
        this.pointcut = pointcut;
        this.aspectClass = aspectClass;
    }
    public final Method getAspectJAdviceMethod() {
        return this.aspectJAdviceMethod;
    }

    public final AspectJExpressionPointcut getPointcut() {
        return this.pointcut;
    }

    public String getAspectName() {
        return this.aspectName;
    }
}
