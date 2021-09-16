# sleuth-tracer-interceptor
An interceptor for Kafka producer/consumer to orchestrate tracing with Spring Cloud Sleuth. Span (and its congregrated Baggage) are carried out as a map alongside Kafka payload.
It is currently able to export Span, but the traces could not get shown up in Zipkin UI
