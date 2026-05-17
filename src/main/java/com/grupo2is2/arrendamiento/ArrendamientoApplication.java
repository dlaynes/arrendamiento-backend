package com.grupo2is2.arrendamiento;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ArrendamientoApplication {

	public static void main(String[] args) {
		// Load .env file before Spring Boot starts so env vars are available
		// for property placeholders in application.properties
		Dotenv dotenv = Dotenv.configure()
				.ignoreIfMissing()
				.load();
		dotenv.entries().forEach(entry -> {
			if (System.getProperty(entry.getKey()) == null) {
				System.setProperty(entry.getKey(), entry.getValue());
			}
		});

		SpringApplication.run(ArrendamientoApplication.class, args);
	}

}
