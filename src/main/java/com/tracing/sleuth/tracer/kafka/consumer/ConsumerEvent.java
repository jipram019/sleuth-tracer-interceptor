package com.tracing.sleuth.tracer.kafka.consumer;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConsumerEvent {
    public String eventId;

    public String topic;

    public Integer partition;

    public String key;

    public Object value;

    public Long timestamp;
}
