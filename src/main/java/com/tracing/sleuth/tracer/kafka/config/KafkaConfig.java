package com.tracing.sleuth.tracer.kafka.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tracing.sleuth.tracer.kafka.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@EnableKafka
@ConditionalOnClass({ObjectMapper.class, KafkaTemplate.class})
public class KafkaConfig {

    @Bean
    public KafkaProducer kafkaProducer(@Autowired ObjectMapper objectMapper,
                                       @Autowired KafkaTemplate kafkaTemplate){
        return new KafkaProducer(objectMapper,kafkaTemplate);
    }
}
