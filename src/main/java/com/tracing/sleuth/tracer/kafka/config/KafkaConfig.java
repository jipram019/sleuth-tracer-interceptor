package com.tracing.sleuth.tracer.kafka.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tracing.sleuth.tracer.kafka.aop.ListenerAdvisor;
import com.tracing.sleuth.tracer.kafka.aop.ListenerInterceptor;
import com.tracing.sleuth.tracer.kafka.aop.ListenerPointcut;
import com.tracing.sleuth.tracer.kafka.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@EnableAutoConfiguration
@EnableKafka
@ConditionalOnClass({ObjectMapper.class, KafkaTemplate.class})
public class KafkaConfig {

    @Bean
    public ListenerPointcut listenerPointcut(){
        return new ListenerPointcut();
    }

    @Bean
    public ListenerInterceptor listenerInterceptor(@Autowired ObjectMapper objectMapper){
        return new ListenerInterceptor(objectMapper);
    }

    @Bean
    public ListenerAdvisor listenerAdvisor(ListenerPointcut pointcut, ListenerInterceptor interceptor){
        return new ListenerAdvisor(pointcut,interceptor);
    }

    @Bean
    public KafkaProducer kafkaProducer(@Autowired ObjectMapper objectMapper,
                                       @Autowired KafkaTemplate kafkaTemplate){
        return new KafkaProducer(objectMapper,kafkaTemplate);
    }
}
