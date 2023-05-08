package com.myspring.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AnnotationUtils {

    public static <A extends Annotation> A findAnnotation(Method method, Class<A> annotationType){
        if (annotationType == null){
            return null;
        }
        return method.getDeclaredAnnotation(annotationType);
    }

    public static Object getValue(Annotation annotation,String attributeName) {
        if (annotation != null && StringUtils.hasText(attributeName)) {
            try {
                Method method = annotation.annotationType().getDeclaredMethod(attributeName);
                ReflectionUtils.makeAccessible(method);
                return method.invoke(annotation);
            } catch (NoSuchMethodException var3) {
                return null;
            } catch (InvocationTargetException var4) {
                throw new IllegalStateException("Could not obtain value for annotation attribute '" + attributeName + "' in " + annotation, var4);
            } catch (Throwable var5) {
                return null;
            }
        } else {
            return null;
        }
    }
}
