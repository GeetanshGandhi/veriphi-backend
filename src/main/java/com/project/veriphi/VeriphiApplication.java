package com.project.veriphi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class VeriphiApplication {

	public static void main(String[] args) {
		SpringApplication.run(VeriphiApplication.class, args);
	}

}
