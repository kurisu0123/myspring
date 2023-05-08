package com.myspring.aop.annotation;

import com.myspring.aop.Advisor;
import com.myspring.beans.factory.BeanFactory;
import com.myspring.beans.factory.BeanFactoryAware;
import com.myspring.beans.factory.SmartInstantiationAwareBeanPostProcessor;
import com.sun.istack.internal.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AnnotationAwareAspectJAutoProxyCreator implements SmartInstantiationAwareBeanPostProcessor, BeanFactoryAware {

    private BeanFactory beanFactory;

    private volatile String[] cachedAdvisorBeanNames;

    private volatile List<String> aspectBeanNames;

    private ReflectiveAspectJAdvisorFactory aspectJAdvisorFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @NotNull
    protected BeanFactory getBeanFactory(){
        return this.beanFactory;
    }

    protected List<Advisor> findEligibleAdvisors(Class<?> beanClass, String beanName) {
        if (aspectJAdvisorFactory == null){
            aspectJAdvisorFactory = new ReflectiveAspectJAdvisorFactory(beanFactory);
        }
        List<Advisor> candidateAdvisors = this.findCandidateAdvisors();
//        List<Advisor> eligibleAdvisors = this.findAdvisorsThatCanApply(candidateAdvisors, beanClass, beanName);
        return candidateAdvisors;
    }

    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) {
        findEligibleAdvisors(beanClass, beanName);
        return null;
    }

    private List<Advisor> findCandidateAdvisors(){
        List<Advisor> advisors = this.findAdvisorBeans();
        advisors.addAll(buildAspectJAdvisors());
        return advisors;
    }

    public List<Advisor> findAdvisorBeans() {
        List<Advisor> advisors = new ArrayList<>();
        String[] advisorNames = this.cachedAdvisorBeanNames;
        if (advisorNames == null) {
            advisorNames = beanNamesForTypeIncludingAncestors(this.beanFactory, Advisor.class);
            this.cachedAdvisorBeanNames = advisorNames;
        }
        if (advisorNames.length == 0){
            return new ArrayList<>();
        }else {
            for (String advisorName : advisorNames){
                if (beanFactory.isCurrentlyInCreation(advisorName)){
                    System.out.println("正在创建"+advisorName);
                }else {
                    advisors.add(beanFactory.getBean(advisorName,Advisor.class));
                }
            }
        }
        return advisors;
    }

    private List<Advisor> buildAspectJAdvisors(){
        List<Advisor> advisors = new ArrayList<>();
        List<String> aspectNames = this.aspectBeanNames;
        if (aspectNames == null){
            String[] beanNames = beanNamesForTypeIncludingAncestors(beanFactory,Object.class);
            for (String beanName : beanNames){
                Class<?> aClass = beanFactory.getType(beanName);
                if (aClass != null && aClass.isAnnotationPresent(Aspect.class)){
                    List<Advisor> classAdvisors = aspectJAdvisorFactory.getAdvisors(aClass,beanName);
                    advisors.addAll(classAdvisors);
                }
            }
        }
        return advisors;
    }

    private String[] beanNamesForTypeIncludingAncestors(BeanFactory beanFactory,Class<?> type){
        List<String> resolvedBeanNames = beanFactory.getBeanNamesForType(type);
        return resolvedBeanNames.size()>0? resolvedBeanNames.toArray(new String[resolvedBeanNames.size()]) : new String[0];
    }


}
