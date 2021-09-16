package com.tracing.sleuth.tracer.sleuth;

import com.tracing.sleuth.tracer.kafka.producer.ProducerEvent;
import com.tracing.sleuth.tracer.kafka.producer.ProducerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.core.Ordered;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Map;

@Slf4j
public class SleuthSpanProducerInterceptor implements ProducerInterceptor {
    private Tracer tracer;
    public SleuthSpanProducerInterceptor(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public void beforeSend(ProducerEvent event){
        PropertyDescriptor propertyDescriptor = BeanUtils.getPropertyDescriptor(event.getValue().getClass(), "span");
        if(propertyDescriptor != null){
            Method method = propertyDescriptor.getWriteMethod();
            if(method != null){
                try {
                    if(tracer.getCurrentSpan() == null){
                        String name = "kafka:producer" + ":" + event.getTopic();
                        tracer.createSpan(name);
                        log.debug("Sleuth span is not available, create new one");
                    }

                    Map<String,String> span = SleuthHelper.toMap(tracer.getCurrentSpan());
                    method.invoke(event.getValue(), span);
                    log.debug("Inject trace span {} to message", span);
                } catch (Throwable e) {
                    log.error("Error while writing span information", e);
                }
            }
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
