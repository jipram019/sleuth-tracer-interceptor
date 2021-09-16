package com.tracing.sleuth.tracer.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tracing.sleuth.tracer.kafka.aop.ListenerAdvisor;
import com.tracing.sleuth.tracer.kafka.aop.ListenerInterceptor;
import com.tracing.sleuth.tracer.kafka.aop.ListenerPointcut;
import com.tracing.sleuth.tracer.kafka.producer.KafkaProducer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;

@Configuration
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

    @Bean
    public ConsumerFactory<?,?> kafkaConsumerFactory(@Autowired KafkaProperties kafkaProperties){
        Map<?,?> properties = kafkaProperties.buildConsumerProperties();
        return new DefaultKafkaConsumerFactory(properties);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Integer> containerFactory(@Autowired KafkaProperties kafkaProperties
                                                                                           ) {
        ConcurrentKafkaListenerContainerFactory<String, Integer> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory(kafkaProperties));
        return factory;
    }

    @Bean
    public ConsumerFactory<String,Integer> consumerFactory(@Autowired KafkaProperties kafkaProperties){
        Map<String,Object> properties = kafkaProperties.buildConsumerProperties();
        properties.put(org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "tracer-app");
        properties.put(ConsumerConfig.CLIENT_ID_CONFIG, "tracer-app");
        return new DefaultKafkaConsumerFactory<>(properties);
    }
}
