package com.myspring.aop.aspectj;

/**
 * Create by wuhao
 * Date: 2023/5/8
 * Time: 23:16
 */
public interface Signature {
    String toString();

    String toShortString();

    String toLongString();

    String getName();

    int getModifiers();

    Class getDeclaringType();

    String getDeclaringTypeName();
}

