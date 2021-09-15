package com.tracing.sleuth.tracer.kafka.consumer;

public interface KafkaConsumerInterceptor {
    default void beforeConsume(ConsumerEvent event){
        // DO NOTHING
    }

    default void afterSuccessConsume(ConsumerEvent event){
        // DO NOTHING
    }

    default void afterFailedConsume(ConsumerEvent event){
        // DO NOTHING
    }
}
