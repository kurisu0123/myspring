package com.myspring.aop.aspectj;

import com.myspring.AspectJExpressionPointcut;
import com.myspring.aop.Advice;
import com.myspring.aop.Pointcut;
import com.myspring.aop.PointcutAdvisor;
import com.myspring.aop.aspectj.AspectJAdvisorFactory;

import java.lang.reflect.Method;

/**
 * Create by wuhao
 * Date: 2023/5/5
 * Time: 21:45
 */
public class InstantiationModelAwarePointcutAdvisorImpl implements PointcutAdvisor {

    private static final Advice EMPTY_ADVICE = new Advice() {
    };
    private String aspectName;
    private AspectJAdvisorFactory aspectJAdvisorFactory;
    private AspectJExpressionPointcut declaredPointcut;
    private transient Method aspectJAdviceMethod;
    private final Class<?>[] parameterTypes;
    private final Class<?> declaringClass;

    private final Class<?> aspectClass;
    private final String methodName;

    private Advice instantiatedAdvice;
    public InstantiationModelAwarePointcutAdvisorImpl(AspectJExpressionPointcut aspectJExpressionPointcut, Method aspectJAdviceMethod,AspectJAdvisorFactory aspectJAdvisorFactory, String aspectName, Class<?> aspectClass){
        this.aspectClass = aspectClass;
        this.declaredPointcut = aspectJExpressionPointcut;
        this.aspectJAdviceMethod = aspectJAdviceMethod;
        this.declaringClass = aspectJAdviceMethod.getDeclaringClass();
        this.parameterTypes = aspectJAdviceMethod.getParameterTypes();
        this.methodName = aspectJAdviceMethod.getName();
        this.aspectName = aspectName;
        this.instantiatedAdvice = this.instantiateAdvice(declaredPointcut);
    }

    private Advice instantiateAdvice(AspectJExpressionPointcut pointcut) {
        Advice advice = this.aspectJAdvisorFactory.getAdvice(this.aspectJAdviceMethod, pointcut,this.aspectClass,this.aspectName);
        return advice != null ? advice : EMPTY_ADVICE;
    }

    @Override
    public Advice getAdvice() {
        return null;
    }

    @Override
    public Pointcut getPointcut() {
        return null;
    }
}
