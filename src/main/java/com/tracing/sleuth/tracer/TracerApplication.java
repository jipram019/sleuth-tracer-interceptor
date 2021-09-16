package com.tracing.sleuth.tracer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tracing.sleuth.tracer.kafka.producer.KafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

@SpringBootApplication
public class TracerApplication {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(TracerApplication.class, args);
	/*	ConfigurableApplicationContext applicationContext = SpringApplication.run(TracerApplication.class);
		KafkaProducer producer = applicationContext.getBean(KafkaProducer.class);

		String filePath = "D:/Works/test.json";
		String content = Files.lines(Paths.get(filePath)).collect(Collectors.joining(System.lineSeparator()));
		content = content.replace('\u00A0',' ');

		JsonNode node = new ObjectMapper().readTree(content);

		producer.send("trace.local.topic", "Test", null, "1", 1234567L);*/
	}

}
