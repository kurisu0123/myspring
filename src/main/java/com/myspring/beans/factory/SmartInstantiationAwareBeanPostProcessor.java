package com.myspring.beans.factory;

public interface SmartInstantiationAwareBeanPostProcessor extends InstantiationAwareBeanPostProcessor{
    default Object getEarlyBeanReference(Object bean, String beanName) throws RuntimeException {
        return bean;
    }
}
