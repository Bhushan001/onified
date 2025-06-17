package com.onified.ai.appConfig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ApplicationConfigServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApplicationConfigServiceApplication.class, args);
	}

}
