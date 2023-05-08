package com.myspring.beans.factory;

import com.sun.istack.internal.Nullable;

public interface InstantiationAwareBeanPostProcessor extends BeanPostProcessor{
    @Nullable
    default Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws RuntimeException {
        return null;
    }

    default boolean postProcessAfterInstantiation(Object bean, String beanName) throws RuntimeException {
        return true;
    }
}
