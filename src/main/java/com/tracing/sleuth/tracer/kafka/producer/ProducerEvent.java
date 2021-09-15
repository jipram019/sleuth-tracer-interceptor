package com.tracing.sleuth.tracer.kafka.producer;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProducerEvent {
    public String topic;

    public Integer partition;

    public String key;

    public Object value;

    public Long timestamp;
}
