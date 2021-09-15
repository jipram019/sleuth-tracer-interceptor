package com.tracing.sleuth.tracer.kafka.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tracing.sleuth.tracer.sleuth.SleuthSpanConsumerInterceptor;
import com.tracing.sleuth.tracer.sleuth.SleuthSpanProducerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.sleuth.autoconfig.brave.SleuthProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(Tracer.class)
@AutoConfigureAfter(KafkaConfig.class)
public class SleuthConfig {
    @Bean
    public SleuthSpanProducerInterceptor sleuthSpanProducerInterceptor(@Autowired Tracer tracer) {
        return new SleuthSpanProducerInterceptor(tracer);
    }

    @Bean
    public SleuthSpanConsumerInterceptor sleuthSpanConsumerInterceptor(@Autowired Tracer tracer,
                                                                       @Autowired ObjectMapper objectMapper,
                                                                       @Autowired SleuthProperties sleuthProperties){
        return new SleuthSpanConsumerInterceptor(tracer, objectMapper, sleuthProperties);
    }
}
