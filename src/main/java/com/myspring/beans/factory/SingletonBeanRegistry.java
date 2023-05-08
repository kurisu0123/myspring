package com.myspring.beans.factory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Create by wuhao
 * Date: 2023/4/13
 * Time: 22:38
 */
public class SingletonBeanRegistry {

    private final Map<String,Object> singletonObjects = new ConcurrentHashMap<>(256); // 一级缓存
    private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap<>(16); // 三级缓存
    private final Map<String, Object> earlySingletonObjects = new ConcurrentHashMap<>(16); // 二级缓存

    private final Set<String> singletonsCurrentlyInCreation = Collections.newSetFromMap(new ConcurrentHashMap(16)); // 保存正在创建中的单例对象
    private final Set<String> inCreationCheckExclusions = Collections.newSetFromMap(new ConcurrentHashMap(16));

    public Object getSingleton(String beanName) {
        return this.getSingleton(beanName, true);
    }

    /**判断bean 是否在创建中*/
    public boolean isSingletonCurrentlyInCreation(String beanName) {
        return this.singletonsCurrentlyInCreation.contains(beanName);
    }

    /**创建单例bean前，判断该bean是否正在创建，不在则保存beanName，存在则抛出异常*/
    protected void beforeSingletonCreation(String beanName) {
        if (!this.inCreationCheckExclusions.contains(beanName) && !this.singletonsCurrentlyInCreation.add(beanName)) {
            throw new RuntimeException(beanName + "正在创建中");
        }
    }

    /**创建单例bean后，将该bean从正在创建的集合中删除*/
    protected void afterSingletonCreation(String beanName) {
        if (!this.inCreationCheckExclusions.contains(beanName) && !this.singletonsCurrentlyInCreation.remove(beanName)) {
            throw new IllegalStateException(beanName + "当前不在创建");
        }
    }

    protected Object getSingleton(String beanName, boolean allowEarlyReference){
        Object singletonObject = this.singletonObjects.get(beanName);
        if (singletonObject == null){
            singletonObject = this.earlySingletonObjects.get(beanName);
            if (singletonObject == null && allowEarlyReference){
                ObjectFactory<?> factory = this.singletonFactories.get(beanName);
                if (factory != null){
                    singletonObject = factory.getObject();
                    this.earlySingletonObjects.put(beanName,singletonObject);
                    this.singletonFactories.remove(beanName);
                }
            }
        }
        return singletonObject;
    }

    /**向一级缓存中添加单例对象，同时删除二、三级缓存*/
    protected void addSingleton(String beanName, Object singletonObject) {
        synchronized(this.singletonObjects) {
            this.singletonObjects.put(beanName, singletonObject);
            this.earlySingletonObjects.remove(beanName);
            this.singletonFactories.remove(beanName);
        }
    }

    /**向三级缓存中添加匿名工厂类*/
    protected void addSingletonFactory(String beanName, ObjectFactory<?> singletonFactory) {
        synchronized(this.singletonObjects) {
            if (!this.singletonObjects.containsKey(beanName)) {
                this.singletonFactories.put(beanName, singletonFactory);
                this.earlySingletonObjects.remove(beanName);
            }
        }
    }

    protected boolean isActuallyInCreation(String beanName) {
        return this.isSingletonCurrentlyInCreation(beanName);
    }

    public boolean isCurrentlyInCreation(String beanName) {
        return !this.inCreationCheckExclusions.contains(beanName) && this.isActuallyInCreation(beanName);
    }
}
