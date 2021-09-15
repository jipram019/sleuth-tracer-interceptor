package com.tracing.sleuth.tracer.sleuth;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;

import java.util.*;
import java.util.stream.Collectors;

public class SleuthHelper {
    public static void continueSpan(Tracer tracer, Map<String, String> map){
        if(tracer != null && map != null && !map.isEmpty()){
            tracer.continueSpan(fromMap(map));
        }
    }

    public static void joinSpan(Tracer tracer, Map<String, String> map){
        if(tracer != null && map != null && !map.isEmpty()){
           Span parent = fromMap(map);
           if(parent != null){
               tracer.createSpan(parent.getName(),parent);
           }
        }
    }

    public static Span fromMap(Map<String, String> map) {
         return Span.builder()
                 .name(map.get(Span.SPAN_NAME_NAME))
                 .exportable(Boolean.valueOf(map.get(Span.SPAN_EXPORT_NAME)))
                 .traceId(Long.valueOf(map.get(Span.TRACE_ID_NAME)))
                 .processId(map.get(Span.PROCESS_ID_NAME))
                 .spanId(Long.valueOf(map.get(Span.SPAN_ID_NAME)))
                 .parents(getParents(map.get(Span.PARENT_ID_NAME)))
                 // To add baggage
                 .build();
    }

    public static Map<String, String> toMap(Span span){
        if(span==null){
            return Collections.EMPTY_MAP;
        }
        return convertSpanToMap(span);
    }

    private static Map<String, String> convertSpanToMap(Span span){
        Map<String, String> map = new HashMap<>();

        List<String> parents = span.getParents().stream()
                .map(String::valueOf)
                .collect(Collectors.toList());

        map.put(Span.SPAN_ID_NAME, String.valueOf(span.getSpanId()));
        map.put(Span.SPAN_NAME_NAME, span.getName());
        map.put(Span.PROCESS_ID_NAME, span.getProcessId());
        map.put(Span.SPAN_EXPORT_NAME, String.valueOf(span.isExportable()));
        map.put(Span.TRACE_ID_NAME, String.valueOf(span.getTraceId()));
        map.put(Span.PARENT_ID_NAME, String.join(",", parents));

        // Set the baggage
        return map;
    }

    private static List<Long> getParents(String parents){
        if(parents==null || parents.trim().isEmpty()){
            return Collections.EMPTY_LIST;
        }

        return Arrays.stream(parents.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }
}
