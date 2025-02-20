package es.daw.demo;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

	public static void main(String[] args) throws IOException {
		//DataBaseInitializer dataBaseInitializer = new DataBaseInitializer();
		//dataBaseInitializer.initializeDatabase();
		SpringApplication.run(Application.class, args);
	}

}
