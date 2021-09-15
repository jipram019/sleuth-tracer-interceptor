package com.tracing.sleuth.tracer.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.OrderComparator;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class KafkaProducer implements InitializingBean, ApplicationContextAware {
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String,String> kafkaTemplate;
    private ApplicationContext applicationContext;
    private List<KafkaProducerInterceptor> kafkaProducerInterceptors;

    public KafkaProducer(ObjectMapper objectMapper, KafkaTemplate<String, String> kafkaTemplate) {
        this.objectMapper = objectMapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        kafkaProducerInterceptors = getKafkaProducerInterceptors(applicationContext);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public ListenableFuture<SendResult<String,String>> send(String topic, Object message, Integer partition, String key, Long timestamp){
        ProducerEvent event = ProducerEvent.builder()
                .key(key)
                .partition(partition)
                .topic(topic)
                .value(message)
                .timestamp(timestamp)
                .build();

        fireBeforeSend(event);

        try {
            String json = objectMapper.writeValueAsString(event.getValue());
            return kafkaTemplate.send(new ProducerRecord<String,String>(
                    event.getTopic(), event.getPartition(), event.getTimestamp(), event.getKey(), json)
                    );

        } catch (JsonProcessingException e) {
            SettableListenableFuture<SendResult<String, String>> future = new SettableListenableFuture<>();
            future.setException(e);
            return  future;
        }
    }

    private List<KafkaProducerInterceptor> getKafkaProducerInterceptors(ApplicationContext applicationContext){
        List<KafkaProducerInterceptor> interceptors = null;
        Map<String, KafkaProducerInterceptor> beans = applicationContext.getBeansOfType(KafkaProducerInterceptor.class);
        if(beans != null && !beans.isEmpty()){
            interceptors = new ArrayList<>(beans.values());
        }

        OrderComparator.sort(interceptors);
        return interceptors;
    }

    private void fireBeforeSend(ProducerEvent event){
        for(KafkaProducerInterceptor interceptor : kafkaProducerInterceptors){
            try {
                interceptor.beforeSend(event);
            } catch (Throwable throwable) {
               log.error("Error while invoking interceptor", throwable);
            }
        }
    }
}
