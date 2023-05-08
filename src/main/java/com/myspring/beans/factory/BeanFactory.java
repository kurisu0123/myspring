package com.myspring.beans.factory;

import com.myspring.beans.BeanDefinition;
import com.myspring.core.annotation.Autowired;
import com.myspring.core.annotation.Bean;
import com.sun.istack.internal.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * Create by wuhao
 * Date: 2023/4/13
 * Time: 22:24
 */
public class BeanFactory extends SingletonBeanRegistry{

    private boolean allowCover = true;


    private final ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);

    private final ThreadLocal<Object> prototypesCurrentlyInCreation = new ThreadLocal<>();

    private volatile BeanPostProcessorCache beanPostProcessorCache; // 将beanPostProcessor按照不同的分类存储到缓存中

    private final List<BeanPostProcessor> beanPostProcessors = new BeanPostProcessorCacheAwareList(); // 存储所有的beanPostProcessor

    private static class BeanPostProcessorCache {
        final List<InstantiationAwareBeanPostProcessor> instantiationAware = new ArrayList();
        final List<SmartInstantiationAwareBeanPostProcessor> smartInstantiationAware = new ArrayList();
        BeanPostProcessorCache() {
        }

    }

    public ConcurrentHashMap<String, BeanDefinition> geBeanDefinitionMap(){
        return this.beanDefinitionMap;
    }

    public Object getBean(String beanName){
        return doGetBean(beanName,null);
    }

    public <T> T getBean(String beanName,Class<T> beanClass){
        return doGetBean(beanName,beanClass);
    }

    public Class<?> getType(String beanName){
        Class<?> type = null;
        BeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);
        if (beanDefinition != null){
            type = beanDefinition.getClazz();
        }
        return type;
    }

    public <T> T doGetBean(String beanName,Class<T> requiredType){
        Object beanInstance;
        if (beanDefinitionMap.containsKey(beanName)){
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);

            if (beanDefinition.getScope().equals("singleton")){
                Object singletonObject = this.getSingleton(beanName);
                if (singletonObject != null){
                    if (isSingletonCurrentlyInCreation(beanName)){
//                        System.out.println("返回了一个半成品bean: "+beanName);
                    }else {
//                        System.out.println("返回了一个成品bean: "+beanName);
                    }
                }else {
                    singletonObject = createBean(beanName,beanDefinition);
                    this.addSingleton(beanName,singletonObject);
                }
                beanInstance =  singletonObject;
            }else {
                beanInstance =  createBean(beanName,beanDefinition);
            }
            return adaptBeanInstance(beanName,beanInstance,requiredType);
        }else {
            throw new NullPointerException();
        }
    }

    <T> T adaptBeanInstance(String name, Object bean, @Nullable Class<?> requiredType) {
        if (requiredType != null && !requiredType.isInstance(bean)){
            return (T)bean;
        }else {
            return (T)bean;
        }
    }

    public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        this.beanPostProcessors.remove(beanPostProcessor);
        this.beanPostProcessors.add(beanPostProcessor);
    }

    public void addBeanPostProcessors(Collection<? extends BeanPostProcessor> beanPostProcessors) {
        this.beanPostProcessors.removeAll(beanPostProcessors);
        this.beanPostProcessors.addAll(beanPostProcessors);
    }

    public void registerBeanDefinition(String beanName ,BeanDefinition beanDefinition){
        BeanDefinition existingDefinition = this.beanDefinitionMap.get(beanName);
        if (existingDefinition != null && !allowCover){
            throw new RuntimeException("bean已存在且不允许覆盖");
        }
        beanDefinitionMap.put(beanName,beanDefinition);
    }

    public Object createBean(String beanName,BeanDefinition beanDefinition){
        beforeCreateBean(beanName,beanDefinition);
        Object beanInstance;
        beanInstance = resolveBeforeInstantiation(beanName,beanDefinition);
        if (beanInstance != null){
            return beanInstance;
        }
        beanInstance = doCreateBean(beanName,beanDefinition);
        return beanInstance;
    }

    protected void beforeCreateBean(String beanName,BeanDefinition beanDefinition){
        if (beanDefinition.isSingleton()){
            beforeSingletonCreation(beanName);
        }else {
        }
    }

    protected void afterCreateBean(String beanName,BeanDefinition beanDefinition){
        if (beanDefinition.isSingleton()){
            afterSingletonCreation(beanName);
        }else {
        }
    }



    protected boolean hasInstantiationAwareBeanPostProcessors() {
        return !this.getBeanPostProcessorCache().instantiationAware.isEmpty();
    }

    private BeanPostProcessorCache getBeanPostProcessorCache(){
        BeanPostProcessorCache beanPostProcessorCache = this.beanPostProcessorCache;
        if (beanPostProcessorCache == null){
            beanPostProcessorCache = new BeanPostProcessorCache();
            Iterator<BeanPostProcessor> iterator = this.beanPostProcessors.iterator();
            while (iterator.hasNext()){
                BeanPostProcessor item = iterator.next();
                if (item instanceof InstantiationAwareBeanPostProcessor){
                    beanPostProcessorCache.instantiationAware.add((InstantiationAwareBeanPostProcessor) item);
                    if (item instanceof SmartInstantiationAwareBeanPostProcessor){
                        beanPostProcessorCache.smartInstantiationAware.add((SmartInstantiationAwareBeanPostProcessor) item);
                    }
                }
            }
        }
        this.beanPostProcessorCache = beanPostProcessorCache;
        return beanPostProcessorCache;
    }

    private Object doCreateBean(String beanName,BeanDefinition beanDefinition){
        try {
            Object beanInstance = createInstance(beanDefinition); // 实例化
            if (beanDefinition.isSingleton()){
                this.addSingletonFactory(beanName, ()->{
                    return this.getEarlyBeanReference(beanName,beanDefinition,beanInstance); // 对bean实例化后初始化前做增强,aop的实现
                });
            }
            populateBean(beanInstance,beanDefinition);
            invokeAwareMethods(beanName,beanInstance);
            return beanInstance;
        } catch (Throwable e) {
            e.printStackTrace();
            throw new RuntimeException();
        } finally {
            afterCreateBean(beanName,beanDefinition);
        }
    }

    public void destroyBean(String beanName){
    }


    public void process(){
        for (String beanName : beanDefinitionMap.keySet()){
            createBean(beanName,beanDefinitionMap.get(beanName));
        }

    }

    protected Object getEarlyBeanReference(String beanName, BeanDefinition beanDefinition, Object bean){
        Object exposedObject = bean;
        if (hasInstantiationAwareBeanPostProcessors()){
            SmartInstantiationAwareBeanPostProcessor bp;
            for (Iterator iterator = this.beanPostProcessorCache.smartInstantiationAware.iterator();iterator.hasNext();exposedObject = bp.getEarlyBeanReference(bean,beanName)){
                bp = (SmartInstantiationAwareBeanPostProcessor) iterator.next();
            }
        }
        return exposedObject;
    }

    private Object createInstance(BeanDefinition beanDefinition) throws IllegalAccessException, InstantiationException {
        Class<?> clazz = beanDefinition.getClazz();
        return clazz.newInstance();
    }

    private Object resolveBeforeInstantiation(String beanName, BeanDefinition beanDefinition){
        Object bean =null;
        if (!Boolean.FALSE.equals(beanDefinition.beforeInstantiationResolved) && hasInstantiationAwareBeanPostProcessors()){
            bean = applyBeanPostProcessorsBeforeInstantiation(beanDefinition.getClazz(),beanName);
            if (bean != null){
                bean = applyBeanPostProcessorsAfterInitialization(bean,beanName);
            }
        }
        return bean;
    }

    private Object applyBeanPostProcessorsBeforeInstantiation(Class<?> beanClass, String beanName) throws RuntimeException {
        Object  result = null;
        Iterator iterator = this.beanPostProcessorCache.instantiationAware.iterator();
        while (result == null){
            if (!iterator.hasNext()) return null;
            InstantiationAwareBeanPostProcessor instantiationAwareBeanPostProcessor = (InstantiationAwareBeanPostProcessor) iterator.next();
            result = instantiationAwareBeanPostProcessor.postProcessBeforeInstantiation(beanClass,beanName);
        }
        return result;
    }
    public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName) throws RuntimeException {
        Object result = existingBean;

        Object current;
        for(Iterator iterator = this.getBeanPostProcessors().iterator(); iterator.hasNext(); result = current) {
            BeanPostProcessor processor = (BeanPostProcessor)iterator.next();
            current = processor.postProcessAfterInitialization(result, beanName);
            if (current == null) {
                return result;
            }
        }
        return result;
    }
    protected Object populateBean(Object bean, BeanDefinition beanDefinition) throws InvocationTargetException, IllegalAccessException {
        Class<?> clazz = beanDefinition.getClazz();
        Method[] methods = clazz.getMethods();
        Field[] fields = clazz.getFields();
        for (Method method : methods) {
            if (method.isAnnotationPresent(Autowired.class)) {
                String paramName = method.getParameters()[0].getName();
                method.invoke(bean, getBean(paramName));
            }
        }
        for (Field field : fields) {
            if (field.isAnnotationPresent(Autowired.class)) {
                field.setAccessible(true);
                field.set(bean, getBean(field.getName()));
            }
        }
        return bean;
    }

    private class BeanPostProcessorCacheAwareList extends CopyOnWriteArrayList<BeanPostProcessor> {
        private BeanPostProcessorCacheAwareList() {
        }

        public boolean add(BeanPostProcessor o) {
            boolean success = super.add(o);
            BeanFactory.this.beanPostProcessorCache = null;
            return success;
        }

        public BeanPostProcessor remove(int index) {
            BeanPostProcessor result = (BeanPostProcessor)super.remove(index);
            BeanFactory.this.beanPostProcessorCache = null;
            return result;
        }

        public boolean remove(Object o) {
            boolean success = super.remove(o);
            if (success) {
                BeanFactory.this.beanPostProcessorCache = null;
            }

            return success;
        }

    }

    public int getBeanPostProcessorCount() {
        return this.beanPostProcessors.size();
    }

    public List<BeanPostProcessor> getBeanPostProcessors() {
        return this.beanPostProcessors;
    }

    public List<String> getBeanNamesForType(Class<?> type){
        List<String> resolvedBeanNames = new ArrayList<>();
        Iterator<Map.Entry<String, BeanDefinition>> iterator = this.beanDefinitionMap.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, BeanDefinition> next = iterator.next();
            if (type.isAssignableFrom(next.getValue().getClazz())){
                resolvedBeanNames.add(next.getKey());
            }
        }
        return resolvedBeanNames;
    }

    private void invokeAwareMethods(String beanName, Object bean) {
        if (bean instanceof Aware) {
            if (bean instanceof BeanFactoryAware) {
                ((BeanFactoryAware)bean).setBeanFactory(this);
            }
        }

    }

}
