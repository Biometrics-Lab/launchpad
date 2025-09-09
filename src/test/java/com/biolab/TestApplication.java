package com.biolab;

import org.springframework.boot.SpringApplication;

public class TestApplication {

	public static void main(String[] args) {
		SpringApplication.from(Application::main)
//				.with(TestcontainersConfiguration.class) //comment to use real DB
				.run(args);
	}

}
