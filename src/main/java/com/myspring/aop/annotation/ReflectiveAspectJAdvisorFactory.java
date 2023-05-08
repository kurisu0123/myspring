package com.myspring.aop.annotation;

import com.myspring.AspectJExpressionPointcut;
import com.myspring.aop.Advice;
import com.myspring.aop.Advisor;
import com.myspring.aop.aspectj.AbstractAspectJAdvice;
import com.myspring.aop.aspectj.AspectJAdvisorFactory;
import com.myspring.aop.aspectj.AspectJMethodBeforeAdvice;
import com.myspring.aop.aspectj.InstantiationModelAwarePointcutAdvisorImpl;
import com.myspring.beans.factory.BeanFactory;
import com.myspring.utils.AnnotationUtils;
import com.myspring.utils.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

public class ReflectiveAspectJAdvisorFactory implements AspectJAdvisorFactory {

    private final BeanFactory beanFactory;

    private static final Class<?>[] ASPECTJ_ANNOTATION_CLASSES = new Class[]{Pointcut.class, Around.class, Before.class, After.class, AfterReturning.class, AfterThrowing.class};

    public static final ReflectionUtils.MethodFilter USER_DECLARED_METHODS = (method) -> {
        return !method.isBridge() && !method.isSynthetic() && method.getDeclaringClass() != Object.class;
    };

    private static final ReflectionUtils.MethodFilter adviceMethodFilter;

    static {
        adviceMethodFilter = USER_DECLARED_METHODS.and((method) ->{
            if (!method.isAnnotationPresent(Pointcut.class)){
                return true;
            }
            return false;
        });
    }

    public ReflectiveAspectJAdvisorFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
    @Override
    public boolean isAspect(Class<?> clazz) {
        return clazz.isAnnotationPresent(Aspect.class);
    }

    @Override
    public List<Advisor> getAdvisors(Class<?> clazz, String aspectName) {
        List<Advisor> advisors = new ArrayList<>();
        List<Method> advisorMethods = getAdvisorMethods(clazz);
        Iterator<Method> iterator = advisorMethods.iterator();
        while (iterator.hasNext()){
            Advisor advisor = getAdvisor(iterator.next(),clazz,aspectName);
            if (advisor != null){
                advisors.add(advisor);
            }
        }
        return advisors;
    }

    private List<Method> getAdvisorMethods(Class<?> aspectClass) {
        List<Method> methods = ReflectionUtils.doWithMethods(aspectClass, adviceMethodFilter);
        return methods;
    }

    private Advisor getAdvisor(Method candidateAdviceMethod, Class<?> candidateAspectClass, String aspectName){
        AspectJExpressionPointcut expressionPointcut = getPointcut(candidateAdviceMethod,candidateAspectClass);
        return expressionPointcut == null? null:new InstantiationModelAwarePointcutAdvisorImpl(expressionPointcut,candidateAdviceMethod,this,aspectName,candidateAspectClass);
    }

    public Advice getAdvice(Method candidateAdviceMethod, AspectJExpressionPointcut pointcut, Class<?> aspectClass, String aspectName){
        AspectJAnnotation<?> aspectJAnnotation = findAspectJAnnotationOnMethod(candidateAdviceMethod);
        if (aspectJAnnotation == null){
            return null;
        }
        Object springAdvice;
        switch (aspectJAnnotation.annotationType){
            case AtPointcut:
                return null;
            case AtAfter:
                springAdvice = null;
                break;
            case AtBefore:
                springAdvice = new AspectJMethodBeforeAdvice(candidateAdviceMethod,pointcut,aspectClass);
                break;
            case AtAround:
                springAdvice = null;
                break;
            case AtAfterReturning:
                springAdvice = null;
                break;
            case AtAfterThrowing:
                springAdvice = null;
                break;
            default:
                throw new RuntimeException();

        }
        ((AbstractAspectJAdvice)springAdvice).setAspectName(aspectName);
        ((AbstractAspectJAdvice)springAdvice).calculateArgumentBindings();
        return (Advice)springAdvice;
    }

    private AspectJExpressionPointcut getPointcut(Method candidateAdviceMethod, Class<?> candidateAspectClass){
        AspectJAnnotation<?> aspectJAnnotation = findAspectJAnnotationOnMethod(candidateAdviceMethod);
        AspectJExpressionPointcut aspectJExpressionPointcut = new AspectJExpressionPointcut(candidateAspectClass);
        if (aspectJAnnotation == null){
            return null;
        }else {
            aspectJExpressionPointcut.setExpression(aspectJAnnotation.getPointcutExpression());
            if (this.beanFactory != null){
                aspectJExpressionPointcut.setBeanFactory(this.beanFactory);
            }
        }
        return aspectJExpressionPointcut;
    }

    protected static AspectJAnnotation<?> findAspectJAnnotationOnMethod(Method method) {
        for (Class<?> clazz : ASPECTJ_ANNOTATION_CLASSES) {
            AspectJAnnotation<?> foundAnnotation = findAnnotation((Class<Annotation>) clazz,method);
            if (foundAnnotation != null){
                return foundAnnotation;
            }
        }
        return null;
    }

    private static <A extends Annotation> AspectJAnnotation<A> findAnnotation(Class<A> toLookFor, Method method){
        A annotation = AnnotationUtils.findAnnotation(method,toLookFor);
        return annotation == null? null:new AspectJAnnotation<>(annotation);
    }

    protected static class AspectJAnnotation<A extends Annotation>{
        private A annotation;
        private final AspectJAnnotationType annotationType;
        private static Map<Class<?>,AspectJAnnotationType> annotationTypeMap = new HashMap<>(8);

        private final String pointcutExpression;

        public AspectJAnnotation(A annotation) {
            this.annotation = annotation;
            this.annotationType = this.determineAnnotationType(annotation);
            this.pointcutExpression = this.resolveExpression(annotation);
        }

        public String getPointcutExpression() {
            return pointcutExpression;
        }

        private String resolveExpression(A annotation) {
            Object value = AnnotationUtils.getValue(annotation, "value");
            if (value instanceof String) {
                String str = (String)value;
                if (!str.isEmpty()) {
                    return str;
                }
            }
            throw new IllegalStateException("Failed to resolve expression: " + annotation);
        }

        private AspectJAnnotationType determineAnnotationType(A annotation){
            AspectJAnnotationType aspectJAnnotationType = annotationTypeMap.get(annotation.annotationType());
            if (aspectJAnnotationType != null){
                return aspectJAnnotationType;
            }
            throw new RuntimeException("Unknown annotation type: " + annotation);
        }

        static {
            annotationTypeMap.put(Pointcut.class, AspectJAnnotationType.AtPointcut);
            annotationTypeMap.put(Around.class, AspectJAnnotationType.AtAround);
            annotationTypeMap.put(Before.class, AspectJAnnotationType.AtBefore);
            annotationTypeMap.put(After.class, AspectJAnnotationType.AtAfter);
            annotationTypeMap.put(AfterReturning.class, AspectJAnnotationType.AtAfterReturning);
            annotationTypeMap.put(AfterThrowing.class, AspectJAnnotationType.AtAfterThrowing);
        }
        protected static enum AspectJAnnotationType {
            AtPointcut,
            AtAround,
            AtBefore,
            AtAfter,
            AtAfterReturning,
            AtAfterThrowing;

            private AspectJAnnotationType() {
            }
        }
    }



}
