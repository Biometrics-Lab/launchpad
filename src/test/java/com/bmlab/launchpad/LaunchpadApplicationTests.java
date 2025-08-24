package com.bmlab.launchpad;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;


@Import(TestcontainersConfiguration.class)
@SpringBootTest
class LaunchpadApplicationTests {

	@Test
	void contextLoads() {
		// This test will pass if the application context loads successfully
		// No additional assertions are needed for this test
		// It serves as a basic smoke test to ensure that the application can start up without issues
		// If there are any configuration errors or missing beans, this test will fail
	}

}
