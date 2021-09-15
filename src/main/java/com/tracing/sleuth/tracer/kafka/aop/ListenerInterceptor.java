package com.tracing.sleuth.tracer.kafka.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tracing.sleuth.tracer.kafka.consumer.ConsumerEvent;
import com.tracing.sleuth.tracer.kafka.consumer.ConsumerInterceptor;
import com.tracing.sleuth.tracer.utils.InterceptorUtil;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.kafka.annotation.KafkaListener;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class ListenerInterceptor<unchecked> implements MethodInterceptor, InitializingBean, ApplicationContextAware {
    private ApplicationContext applicationContext;
    private ObjectMapper objectMapper;
    private List<ConsumerInterceptor> kafkaConsumerInterceptors;

    public ListenerInterceptor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Nullable
    @Override
    public Object invoke(@javax.annotation.Nonnull MethodInvocation methodInvocation) throws Throwable {
        if(isKafkaListener(methodInvocation) && isConsumerRecordArguments(methodInvocation)){
            ConsumerEvent event = toConsumerEvent(getConsumerRecords(methodInvocation));
            try {
                if(InterceptorUtil.fireBeforeConsume(event, kafkaConsumerInterceptors)){
                    return null;
                } else{
                    Object response = methodInvocation.proceed();
                    InterceptorUtil.fireAfterSuccessConsume(event, kafkaConsumerInterceptors);
                    return response;
                }
            } catch (Throwable throwable) {
                InterceptorUtil.fireAfterFailedConsume(event, kafkaConsumerInterceptors, throwable);
                throw throwable;
            }

        }
        else{
            return methodInvocation.proceed();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        kafkaConsumerInterceptors = InterceptorUtil.getKafkaConsumerInterceptors(applicationContext);
    }

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private boolean isKafkaListener(MethodInvocation invocation){
        Method method = invocation.getMethod();
        return AnnotationUtils.findAnnotation(method, KafkaListener.class) != null;
    }

    private boolean isConsumerRecordArguments(MethodInvocation invocation){
        Object[] arguments = invocation.getArguments();
        if(arguments.length == 0){
            return false;
        }
        return Arrays.stream(invocation.getArguments()).anyMatch(o -> o instanceof ConsumerRecord);
    }

    @SuppressWarnings("unchecked")
    private ConsumerRecord<String,String> getConsumerRecords(MethodInvocation methodInvocation){
        return (ConsumerRecord<String,String>)Arrays.stream(methodInvocation.getArguments())
                .filter(o -> o instanceof ConsumerRecord)
                .findFirst()
                .orElse(null);
    }

    private ConsumerEvent toConsumerEvent(ConsumerRecord<String,String> consumerRecord){
        return ConsumerEvent.builder()
                .eventId("KAFKA-SLEUTH")
                .key(consumerRecord.key())
                .partition(consumerRecord.partition())
                .topic(consumerRecord.topic())
                .timestamp(consumerRecord.timestamp())
                .value(consumerRecord.value())
                .build();
    }
}
