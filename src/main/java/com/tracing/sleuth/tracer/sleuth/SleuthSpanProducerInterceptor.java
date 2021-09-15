package com.tracing.sleuth.tracer.sleuth;

import com.tracing.sleuth.tracer.kafka.producer.KafkaProducerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.sleuth.Tracer;

@Slf4j
public class SleuthSpanProducerInterceptor implements KafkaProducerInterceptor {
    private Tracer tracer;
    public SleuthSpanProducerInterceptor(Tracer tracer) {
        this.tracer = tracer;
    }
}
