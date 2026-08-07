package com.testpilot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the TestPilot AI Spring Boot application.
 *
 * @SpringBootApplication enables:
 * - @Configuration: marks this as a config class
 * - @EnableAutoConfiguration: auto-configures Spring beans
 * - @ComponentScan: scans the package for controllers, services, etc.
 */
@SpringBootApplication
public class TestPilotApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestPilotApplication.class, args);
    }
}
