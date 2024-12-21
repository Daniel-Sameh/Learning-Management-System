package com.swe.lms;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LmsApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.load();
		System.setProperty("EMAIL_USERNAME", dotenv.get("EMAIL_USERNAME"));
		System.setProperty("EMAIL_PASSWORD", dotenv.get("EMAIL_PASSWORD"));
//		System.out.println("EMAIL_USERNAME: " + System.getProperty("EMAIL_USERNAME"));
//		System.out.println("EMAIL_PASSWORD: " + System.getProperty("EMAIL_PASSWORD"));
		SpringApplication.run(LmsApplication.class, args);
	}

}
