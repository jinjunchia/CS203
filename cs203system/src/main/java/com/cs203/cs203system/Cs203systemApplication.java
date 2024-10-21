package com.cs203.cs203system;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class Cs203systemApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(Cs203systemApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
    }

}
