package com.tracing.sleuth.tracer.config;

import com.tracing.sleuth.tracer.interceptor.IdentityInterceptor;
import com.tracing.sleuth.tracer.interceptor.RoutingInterceptor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.sleuth.instrument.messaging.TraceSpanMessagingAutoConfiguration;
import org.springframework.cloud.sleuth.instrument.web.TraceHandlerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class InterceptorConfig {
    @Bean
    public IdentityInterceptor identityInterceptor(){
        return new IdentityInterceptor();
    }

    @Bean
    public RoutingInterceptor routingInterceptor(){
        return new RoutingInterceptor();
    }

}

