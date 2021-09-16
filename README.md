# sleuth-tracer-interceptor
An interceptor for Kafka producer/consumer to orchestrate tracing with Spring Cloud Sleuth. Span (and its congregrated Baggage) are carried out as a map alongside Kafka payload.
It currently is able to export Span, but the traces are not shown in Zipkin UI
