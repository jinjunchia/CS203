package com.cs203.cs203system;

import com.cs203.cs203system.utility.SimulationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.stereotype.Component;

@Component
public class SimulationLoader implements CommandLineRunner {

    private final SimulationManager simulationManager;

    @Autowired
    public SimulationLoader(SimulationManager simulationManager) {
        this.simulationManager = simulationManager;
    }

    public static void main(String[] args) {
        SpringApplication.run(SimulationLoader.class, args);
    }

    public void run(String... args) throws Exception {
        // Run the simulation when the application starts
        simulationManager.setupAndRunDummySimulation();
    }
}
