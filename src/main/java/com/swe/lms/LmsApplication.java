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
		System.setProperty("DB_URL", dotenv.get("DB_URL"));
		System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
		System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
//		System.setProperty("EMAIL_PASSWORD", dotenv.get("EMAIL_PASSWORD"));
//		System.setProperty("EMAIL_USERNAME", dotenv.get("EMAIL_USERNAME"));
//		System.setProperty("EMAIL_PASSWORD", dotenv.get("EMAIL_PASSWORD"));
//		System.out.println("EMAIL_USERNAME: " + System.getProperty("EMAIL_USERNAME"));
//		System.out.println("EMAIL_PASSWORD: " + System.getProperty("EMAIL_PASSWORD"));
		SpringApplication.run(LmsApplication.class, args);
	}

}
