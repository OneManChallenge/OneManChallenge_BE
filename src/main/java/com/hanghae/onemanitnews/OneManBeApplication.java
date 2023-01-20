package com.hanghae.onemanitnews;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class OneManBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(OneManBeApplication.class, args);
	}

}
