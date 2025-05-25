package com.shiftmanagement.app_core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@SpringBootApplication
@EnableReactiveMongoRepositories(basePackages = "com.shiftmanagement.app_core.repository")
public class AppCoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppCoreApplication.class, args);
	}

}
