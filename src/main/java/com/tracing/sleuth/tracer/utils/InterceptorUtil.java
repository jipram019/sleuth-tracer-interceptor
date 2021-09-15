package com.tracing.sleuth.tracer.utils;

import com.tracing.sleuth.tracer.kafka.consumer.ConsumerEvent;
import com.tracing.sleuth.tracer.kafka.consumer.ConsumerInterceptor;
import com.tracing.sleuth.tracer.kafka.producer.ProducerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.core.OrderComparator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class InterceptorUtil {
    public static boolean fireBeforeConsume(ConsumerEvent event, List<ConsumerInterceptor> interceptors){
        for(ConsumerInterceptor interceptor : interceptors){
            try {
                if(interceptor.beforeConsume(event)){
                    return true;
                }
            } catch (Throwable throwable) {
                log.error("Error while invoking interceptor", throwable);
            }
        }
        return false;
    }

    public static void fireAfterSuccessConsume(ConsumerEvent event, List<ConsumerInterceptor> interceptors){
        for(ConsumerInterceptor interceptor : interceptors){
            try {
                interceptor.afterSuccessConsume(event);
            } catch (Throwable throwable) {
                log.error("Error while invoking interceptor", throwable);
            }
        }
    }

    public static void fireAfterFailedConsume(ConsumerEvent event, List<ConsumerInterceptor> interceptors, Throwable throwable){
        for(ConsumerInterceptor interceptor : interceptors){
            try {
                interceptor.afterFailedConsume(event, throwable);
            } catch (Throwable e) {
                log.error("Error while invoking interceptor", e);
            }
        }
    }

    public static List<ConsumerInterceptor> getKafkaConsumerInterceptors(ApplicationContext applicationContext){
        List<ConsumerInterceptor> interceptors = null;
        Map<String, ConsumerInterceptor> beans = applicationContext.getBeansOfType(ConsumerInterceptor.class);
        if(beans != null && !beans.isEmpty()){
            interceptors = new ArrayList<>(beans.values());
        }

        OrderComparator.sort(interceptors);
        return interceptors;
    }

    public static List<ProducerInterceptor> getKafkaProducerInterceptors(ApplicationContext applicationContext){
        List<ProducerInterceptor> interceptors = null;
        Map<String, ProducerInterceptor> beans = applicationContext.getBeansOfType(ProducerInterceptor.class);
        if(beans != null && !beans.isEmpty()){
            interceptors = new ArrayList<>(beans.values());
        }

        OrderComparator.sort(interceptors);
        return interceptors;
    }
}
