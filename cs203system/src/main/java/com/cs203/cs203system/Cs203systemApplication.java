package com.cs203.cs203system;

import com.cs203.cs203system.utility.SimulationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Cs203systemApplication implements CommandLineRunner {

	@Autowired
	private SimulationManager simulationManager;
	public static void main(String[] args) {
		SpringApplication.run(Cs203systemApplication.class, args);
	}

	public void run(String... args) throws Exception {
		// Run the simulation when the application starts
		simulationManager.setupAndRunDummySimulation();
	}
}
