package com.tracing.sleuth.tracer.kafka.consumer;

import org.springframework.core.Ordered;

public interface ConsumerInterceptor extends Ordered {
    default boolean beforeConsume(ConsumerEvent event){
        return false;
    }

    default void afterSuccessConsume(ConsumerEvent event){
        // DO NOTHING
    }

    default void afterFailedConsume(ConsumerEvent event, Throwable throwable){
        // DO NOTHING
    }

    default int getOrder(){
        return 0;
    }
}
