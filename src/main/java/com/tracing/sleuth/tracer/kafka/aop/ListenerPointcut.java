package com.tracing.sleuth.tracer.kafka.aop;

import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.kafka.annotation.KafkaListener;

import java.lang.reflect.Method;

public class ListenerPointcut extends StaticMethodMatcherPointcut {
    @Override
    public boolean matches(Method method, Class<?> aClass) {
        return AnnotationUtils.findAnnotation(method, KafkaListener.class) != null;
    }
}
