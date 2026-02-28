package com.example.demo;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App {

	public static void main(String[] args) {
		// 1. Carga el .env (debe estar en la raíz del proyecto)
		Dotenv dotenv = Dotenv.configure()
				.ignoreIfMissing()
				.load();

		// 2. Inyecta cada variable en el Sistema para que ${VARIABLE} funcione en el YAML
		dotenv.entries().forEach(entry ->
				System.setProperty(entry.getKey(), entry.getValue())
		);

		// 3. Ahora sí, arranca Spring
		SpringApplication.run(App.class, args);
	}

}
