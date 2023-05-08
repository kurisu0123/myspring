package com.myspring.aop.aspectj;

import com.myspring.AspectJExpressionPointcut;
import com.myspring.aop.Advice;
import com.myspring.aop.Advisor;

import java.lang.reflect.Method;
import java.util.List;

public interface AspectJAdvisorFactory {
    boolean isAspect(Class<?> clazz);
    List<Advisor> getAdvisors(Class<?> clazz, String aspectName);

    Advice getAdvice(Method aspectJAdviceMethod, AspectJExpressionPointcut pointcut, Class<?> aspectClass, String aspectName);
}
