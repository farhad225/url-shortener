package org.dkb.hash.generation.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HashGenerationApplication {

	public static void main(String[] args) {
		SpringApplication.run(HashGenerationApplication.class, args);
	}

}
