package com.myspring.aop.aspectj;

/**
 * Create by wuhao
 * Date: 2023/5/8
 * Time: 23:15
 */
public interface SourceLocation {
    Class getWithinType();

    String getFileName();

    int getLine();

    /** @deprecated */
    int getColumn();
}
