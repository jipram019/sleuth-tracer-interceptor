package com.tracing.sleuth.tracer.kafka.producer;

import org.springframework.core.Ordered;

public interface ProducerInterceptor extends Ordered {
    default void beforeSend(ProducerEvent event){
    }
    default int getOrder(){
        return 0;
    }
}
