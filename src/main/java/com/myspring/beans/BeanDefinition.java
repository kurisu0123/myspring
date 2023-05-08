package com.myspring.beans;

public class BeanDefinition {

    private Class<?> clazz;
    private String scope;

    public boolean beforeInstantiationResolved;

    public BeanDefinition() {
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public boolean isSingleton() {
        return "singleton".equals(this.scope) || "".equals(this.scope);
    }

    public boolean isPrototype() {
        return "prototype".equals(this.scope);
    }
}
