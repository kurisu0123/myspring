package com.myspring;

import com.myspring.aop.Pointcut;
import com.myspring.beans.factory.BeanFactory;
import com.myspring.beans.factory.BeanFactoryAware;

public class AspectJExpressionPointcut implements Pointcut , BeanFactoryAware {
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
}
