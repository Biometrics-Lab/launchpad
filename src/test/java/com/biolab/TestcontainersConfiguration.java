package com.biolab;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

//	@Bean
//	@ServiceConnection
//	KafkaContainer kafkaContainer() {
//		return new KafkaContainer(DockerImageName.parse("apache/kafka-native:latest"));
//	}

	@Bean
	@ServiceConnection
	PostgreSQLContainer<?> postgresContainer() {
		return new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));
	}

	@Bean
	LocalStackContainer localStackContainer() {
		return new LocalStackContainer(DockerImageName.parse("localstack/localstack:3.4"))
				.withServices(S3);
	}

	@Bean
	DynamicPropertyRegistrar localStackProperties(LocalStackContainer localStack) {
		return registry -> {
			registry.add("aws.s3.endpoint", () -> localStack.getEndpointOverride(S3).toString());
			registry.add("aws.s3.region", localStack::getRegion);
			registry.add("aws.s3.access-key", localStack::getAccessKey);
			registry.add("aws.s3.secret-key", localStack::getSecretKey);
		};
	}
}