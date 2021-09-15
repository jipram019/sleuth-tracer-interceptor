package com.tracing.sleuth.tracer.kafka.aop;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;

public class ListenerAdvisor extends AbstractPointcutAdvisor {
    private ListenerPointcut pointcut;
    private ListenerInterceptor interceptor;

    public ListenerAdvisor(ListenerPointcut pointcut, ListenerInterceptor interceptor){
        this.pointcut = pointcut;
        this.interceptor = interceptor;
    }

    @Override
    public Pointcut getPointcut() {
        return pointcut;
    }

    @Override
    public Advice getAdvice() {
        return interceptor;
    }
}
