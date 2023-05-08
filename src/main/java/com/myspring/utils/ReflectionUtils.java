package com.myspring.utils;

import com.sun.istack.internal.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Create by wuhao
 * Date: 2023/4/26
 * Time: 22:13
 */
public class ReflectionUtils {
    public static List<Method> doWithMethods(Class<?> clazz, @Nullable MethodFilter mf) {
        List<Method> methods = new ArrayList<>();
        Method[] classMethods = clazz.getMethods();
        for (Method method : classMethods){
            if (mf == null || mf.matches(method)){
                methods.add(method);
            }
        }
        return methods;
    }

    public interface MethodFilter {
        boolean matches(Method method);

        default MethodFilter and(MethodFilter next) {
            return (method) -> {
                return this.matches(method) && next.matches(method);
            };
        }
    }

    public static void makeAccessible(Method method) {
        if ((!Modifier.isPublic(method.getModifiers()) || !Modifier.isPublic(method.getDeclaringClass().getModifiers())) && !method.isAccessible()) {
            method.setAccessible(true);
        }

    }


}
