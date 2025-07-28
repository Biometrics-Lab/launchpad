package com.bmlab.launchpad;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

//	@Bean
//	@ServiceConnection
//	KafkaContainer kafkaContainer() {
//		return new KafkaContainer(DockerImageName.parse("apache/kafka-native:latest"));
//	}


	@Bean
	@ServiceConnection
	PostgreSQLContainer<?> postgresContainer() {
		return new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));
//				.withExposedPorts(5432)
//				.withCreateContainerCmdModifier(cmd -> cmd.withPortBindings(
//						new PortBinding(Ports.Binding.bindPort(5433), new ExposedPort(5432))
//				));
//				.withExposedPorts(5432);
	}


//	@DynamicPropertySource
//	static void configureProperties(DynamicPropertyRegistry registry, PostgreSQLContainer<?> postgres) {
//		registry.add("spring.datasource.url", () -> "jdbc:postgresql://localhost:5433/test");
//		registry.add("spring.datasource.username", postgres::getUsername);
//		registry.add("spring.datasource.password", postgres::getPassword);
//	}


}