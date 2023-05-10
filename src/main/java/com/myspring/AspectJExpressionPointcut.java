package com.myspring;

import com.myspring.aop.ClassFilter;
import com.myspring.aop.MethodMatcher;
import com.myspring.aop.Pointcut;
import com.myspring.beans.factory.BeanFactory;
import com.myspring.beans.factory.BeanFactoryAware;

public class AspectJExpressionPointcut implements Pointcut ,ClassFilter, BeanFactoryAware {
    private Class<?> pointcutDeclarationScope;
    private String expression;

    private BeanFactory beanFactory;
    public AspectJExpressionPointcut(Class<?> pointcutDeclarationScope) {
        this.pointcutDeclarationScope = pointcutDeclarationScope;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public ClassFilter getClassFilter() {
        return this;
    }

    @Override
    public MethodMatcher getMethodMatcher() {
        return null;
    }

    @Override
    public boolean matches(Class<?> clazz) {
        System.out.println("111:"+this.getExpression());
        return false;
    }


}
