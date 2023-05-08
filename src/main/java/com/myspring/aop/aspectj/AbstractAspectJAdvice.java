package com.myspring.aop.aspectj;

import com.myspring.AspectJExpressionPointcut;
import com.myspring.aop.Advice;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Map;

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
    private String aspectName;

    private int joinPointArgumentIndex = -1;
    private int joinPointStaticPartArgumentIndex = -1;
    private Map<String, Integer> argumentBindings;
    private boolean argumentsIntrospected = false;
    public AbstractAspectJAdvice(@NotNull Method aspectJAdviceMethod, AspectJExpressionPointcut pointcut, Class<?> aspectClass) {
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
    public void setAspectName(String aspectName){
        this.aspectName =aspectName;
    }

    public final void calculateArgumentBindings(){
        if (!this.argumentsIntrospected && this.parameterTypes.length!=0){
            int argumentsLength = this.parameterTypes.length;
            if (maybeBindJoinPoint(parameterTypes[0]) || maybeBindProceedingJoinPoint(parameterTypes[0]) || maybeBindJoinPointStaticPart(parameterTypes[0])){
                argumentsLength--;
            }
            if (argumentsLength>0){
                bindArgumentsByName(argumentsLength);
            }
            argumentsIntrospected = true;
        }
    }

    private void bindArgumentsByName(int argumentsLength) {
    }


    protected boolean supportsProceedingJoinPoint() {
        return false;
    }

    private boolean maybeBindJoinPoint(Class<?> parameterType) {
        if (JoinPoint.class == parameterType){
            this.joinPointArgumentIndex = 0;
            return true;
        }else {
            return false;
        }
    }

    private boolean maybeBindProceedingJoinPoint(Class<?> parameterType) {
        if (ProceedingJoinPoint.class == parameterType){
            if (!this.supportsProceedingJoinPoint()) {
                throw new IllegalArgumentException("ProceedingJoinPoint is only supported for around advice");
            } else {
                this.joinPointArgumentIndex = 0;
                return true;
            }
        }else {
            return false;
        }
    }

    private boolean maybeBindJoinPointStaticPart(Class<?> parameterType) {
        if (JoinPoint.StaticPart.class == parameterType) {
            this.joinPointStaticPartArgumentIndex = 0;
            return true;
        } else {
            return false;
        }
    }


}
