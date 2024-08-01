package com.enzo.dga.util;

import com.enzo.dga.governance.assessor.Assessor;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringIOCProvider implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 通过名字从容器中获取Bean对象
     */
    public <T> T getBean(String beanName, Class<T> clsType) {
        T bean = applicationContext.getBean(beanName, clsType);
        return bean;
    }
}
