package com.example.cormacarena_client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "org.example.modelo")
public class CormacarenaClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(CormacarenaClientApplication.class, args);
	}

}
