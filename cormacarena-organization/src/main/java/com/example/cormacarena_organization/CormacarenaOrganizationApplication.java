package com.example.cormacarena_organization;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "org.example.modelo")

public class CormacarenaOrganizationApplication {

	public static void main(String[] args) {
		SpringApplication.run(CormacarenaOrganizationApplication.class, args);
	}

}
