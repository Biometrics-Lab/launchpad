package com.bmlab.launchpad;

import org.springframework.boot.SpringApplication;

public class TestLaunchpadApplication {

	public static void main(String[] args) {
		SpringApplication.from(LaunchpadApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
