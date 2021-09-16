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
public class IdentityInterceptor implements ProducerInterceptor {
    public IdentityInterceptor(){
    }

    @Override
    public void beforeSend(ProducerEvent event){
        PropertyDescriptor descriptor = BeanUtils.getPropertyDescriptor(event.getValue().getClass(), "eventId");
        if(descriptor != null && !isIdentityExists(event.getValue(), descriptor)){
            log.debug("eventId is not exist");
        }
        writeEventId(event.getValue(), descriptor);
    }

    private void writeEventId(Object message, PropertyDescriptor descriptor){
        Method method = descriptor.getWriteMethod();
        if(method != null){
            String eventId = UUID.randomUUID().toString();
            try {
                method.invoke(message, eventId);
                log.debug("Inject eventId: {} into message", eventId);
            } catch (IllegalAccessException | InvocationTargetException e) {
                log.warn("Error while writing eventId", e);
            }
        }
    }

    private boolean isIdentityExists(Object message, PropertyDescriptor descriptor){
        Method method = descriptor.getReadMethod();
        if(method != null){
            try {
                return method.invoke(message) != null;
            } catch (IllegalAccessException | InvocationTargetException e) {
                log.error("Cannot retrieve eventId", e);
            }
        }
        return false;
    }

}
