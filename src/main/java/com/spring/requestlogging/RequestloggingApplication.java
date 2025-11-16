package com.spring.requestlogging;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // zamanlanmis gorevleri ekler
public class RequestloggingApplication {

	public static void main(String[] args) {
		SpringApplication.run(RequestloggingApplication.class, args);
	}

}

