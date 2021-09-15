package com.tracing.sleuth.tracer.sleuth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tracing.sleuth.tracer.kafka.consumer.ConsumerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.sleuth.autoconfig.SleuthProperties;

@Slf4j
public class SleuthSpanConsumerInterceptor implements ConsumerInterceptor {
    private Tracer tracer;
    private ObjectMapper objectMapper;
    private SleuthProperties sleuthProperties;

    public SleuthSpanConsumerInterceptor (Tracer tracer, ObjectMapper objectMapper, SleuthProperties sleuthProperties){
        this.tracer = tracer;
        this.objectMapper = objectMapper;
        this.sleuthProperties = sleuthProperties;
    }
}
