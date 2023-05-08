package com.myspring.core;

import com.myspring.aop.annotation.AnnotationAwareAspectJAutoProxyCreator;
import com.myspring.beans.BeanDefinition;
import com.myspring.beans.factory.BeanFactory;
import com.myspring.beans.factory.BeanPostProcessor;
import com.myspring.core.annotation.Component;
import com.myspring.core.annotation.ComponentScan;
import com.myspring.core.annotation.Scope;
import com.myspring.utils.StringUtils;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

/**
 * spring核心容器*/
public class MyApplicationContext {

    private Class<?> config;

    private String[] basePackages; // 要扫描的基础包

    private final BeanFactory beanFactory = new BeanFactory();

    public MyApplicationContext(Class<?> config) {
        this.config = config;
        //解析配置类，获取基础的扫描包，默认为配置类所在的包
        doGetBasePackages(config);
        // 注解扫描
        annotationScan();
        registerBeanPostProcessors();
        beanFactory.process();

    }

    public Object getBean(String beanName){
        return beanFactory.getBean(beanName);
    }


    private void registerBeanPostProcessors() {
        registerDefaultBeanPostProcessors();
        ConcurrentHashMap<String,BeanDefinition> beanDefinitionMap = beanFactory.geBeanDefinitionMap(); // 注册自定义的beanPostProcessors
        beanDefinitionMap.entrySet().forEach(item ->{
            if (BeanPostProcessor.class.isAssignableFrom(item.getValue().getClazz())){
                beanFactory.addBeanPostProcessor((BeanPostProcessor) beanFactory.getBean(item.getKey(),item.getValue().getClazz()));
            }
        });

    }


    // 注册默认的beanPostProcessors
    private void registerDefaultBeanPostProcessors(){
        BeanDefinition beanDefinition = new BeanDefinition();
        beanDefinition.setScope("singleton");
        beanDefinition.setClazz(AnnotationAwareAspectJAutoProxyCreator.class);
        beanDefinition.beforeInstantiationResolved = false;
        beanFactory.registerBeanDefinition("internalAutoProxyCreator",beanDefinition);
        beanFactory.addBeanPostProcessor(beanFactory.getBean("internalAutoProxyCreator",AnnotationAwareAspectJAutoProxyCreator.class));
    }
    private void annotationScan(){
        ClassLoader classLoader = MyApplicationContext.class.getClassLoader();
        for (String basePackage : this.basePackages){
            String basePackagePath = basePackage.replace(".","/");
            URL resource = classLoader.getResource(basePackagePath);
            File file = new File(resource.getFile());
            doAnnotationScan(file,classLoader,basePackage,false);
        }
    }

    private void doAnnotationScan(File file, ClassLoader classLoader, String packagePath, boolean concat){
        String filePath = packagePath;
        if (concat){
            filePath = packagePath.concat(".").concat(file.getName());
        }
        if (file.isDirectory()){
            File[] files = file.listFiles();
            String finalFilePath = filePath;
            Arrays.stream(files).forEach(item ->{
                doAnnotationScan(item,classLoader, finalFilePath,true);
            });
        }else {
            String clazz = filePath.substring(0, filePath.lastIndexOf("."));
            try {
                Class<?> aClass = classLoader.loadClass(clazz);
                if (aClass.isAnnotationPresent(Component.class)){
                    registerBeanDefinition(aClass);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取包扫描路径*/
    private void doGetBasePackages(Class config){
        ComponentScan componentScan = (ComponentScan) config.getDeclaredAnnotation(ComponentScan.class);
        this.basePackages = componentScan.value(); // 获取扫描路径
        if (this.basePackages.length == 0){
            this.basePackages = new String[1];
            this.basePackages[0] = config.getPackage().getName(); // 默认扫描路径为注解作用的配置类所在的包
        }
    }

    /**处理@Component注解，注册beanDefinition*/
    private void registerBeanDefinition(Class clazz){
        Component componentAnnotation = (Component) clazz.getDeclaredAnnotation(Component.class);
        String componentName = componentAnnotation.value();
        if ("".equals(componentName)){
            componentName = StringUtils.uncapitalize(clazz.getSimpleName());
        }
        BeanDefinition beanDefinition = new BeanDefinition();
        if (clazz.isAnnotationPresent(Scope.class)){
            Scope scope = (Scope) clazz.getDeclaredAnnotation(Scope.class);
            beanDefinition.setScope(scope.value());
        }else {
            beanDefinition.setScope("singleton");
        }
        if (BeanPostProcessor.class.isAssignableFrom(clazz)){
            beanDefinition.beforeInstantiationResolved = false; // 如果是beanPostProcessor 则创建bean时不需要进行后置处理
        }else {
            beanDefinition.beforeInstantiationResolved = true;
        }
        beanDefinition.setClazz(clazz);
        beanFactory.registerBeanDefinition(componentName,beanDefinition);
    }

}
