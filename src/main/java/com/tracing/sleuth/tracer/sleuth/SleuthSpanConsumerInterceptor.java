package com.tracing.sleuth.tracer.sleuth;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tracing.sleuth.tracer.kafka.consumer.ConsumerEvent;
import com.tracing.sleuth.tracer.kafka.consumer.ConsumerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.sleuth.autoconfig.SleuthProperties;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

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

    private Map<String, String > getSpan(String json) throws IOException {
        return Optional.ofNullable(objectMapper.readTree(json))
                .map(value -> value.get("span"))
                .map(value -> objectMapper.treeAsTokens(value))
                .map(this::getMap)
                .orElse(null);
    }

    private Map<String, String > getMap(JsonParser parser) {
        try {
            return objectMapper.readValue(parser, new TypeReference<Map<String, String>>() {
            });
        } catch (IOException e) {
            log.error("Failed to get map from span attribute", e);
            return Collections.emptyMap();
        }
    }

    @Override
    public boolean beforeConsume(ConsumerEvent event){
        try {
            Map<String, String> span = getSpan(event.getValue());
            if(span != null && !span.isEmpty()){
                if(sleuthProperties.isSupportsJoin()){
                    SleuthHelper.joinSpan(tracer, span);
                    log.info("Join trace span {}", span);
                } else{
                    SleuthHelper.continueSpan(tracer, span);
                    log.info("Continue trace span {}", span);
                }
            } else{
                String name = "kafka:consumer" + ":" + event.getTopic();
                tracer.close(tracer.getCurrentSpan());
                tracer.createSpan(name);
                log.info("Sleuth span is not available, create new one");
            }
        } catch (Throwable e) {
           log.error("Failed to continue span", e);
        }

        return false;
    }

}
