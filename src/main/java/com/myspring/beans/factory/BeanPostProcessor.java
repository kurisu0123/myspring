package com.myspring.beans.factory;

import com.sun.istack.internal.Nullable;

public interface BeanPostProcessor {
    @Nullable
    default Object postProcessBeforeInitialization(Object bean, String beanName) throws RuntimeException {
        return bean;
    }

    @Nullable
    default Object postProcessAfterInitialization(Object bean, String beanName) throws RuntimeException {
        return bean;
    }
}
