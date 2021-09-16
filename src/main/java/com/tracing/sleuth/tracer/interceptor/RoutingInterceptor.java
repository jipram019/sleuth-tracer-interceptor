package com.tracing.sleuth.tracer.interceptor;

import com.tracing.sleuth.tracer.kafka.producer.ProducerEvent;
import com.tracing.sleuth.tracer.kafka.producer.ProducerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

@Slf4j
public class RoutingInterceptor implements ProducerInterceptor {
    public RoutingInterceptor(){
    }

    @Override
    public void beforeSend(ProducerEvent event){
        PropertyDescriptor descriptor = BeanUtils.getPropertyDescriptor(event.getValue().getClass(), "routingId");
        if(descriptor != null){
            setKeyFromRoutingProperty(event, descriptor);
        }
    }

    private void setKeyFromRoutingProperty(ProducerEvent event, PropertyDescriptor descriptor){
        Method method = descriptor.getReadMethod();
        if(method != null){
            try {
                String routingId = (String) method.invoke(event.getValue());
                if(routingId == null){
                    routingId = UUID.randomUUID().toString();
                }
                event.setKey(routingId);
                log.debug("Inject routingId: {} to message", routingId);
            } catch (IllegalAccessException | InvocationTargetException e) {
                log.warn("Error while injecting routingId", e);
            }
        }
    }
}
