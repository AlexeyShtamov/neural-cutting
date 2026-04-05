package ru.shtamov.neural_cutting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class NeuralCuttingApplication {

	public static void main(String[] args) {
		SpringApplication.run(NeuralCuttingApplication.class, args);
	}

}
