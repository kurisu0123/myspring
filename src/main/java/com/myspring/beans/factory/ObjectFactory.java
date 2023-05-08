package com.myspring.beans.factory;

public interface ObjectFactory<T> {
    T getObject() throws RuntimeException;
}
