package com.sabinghost19.teamslkghostapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TeamsLkGhostAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(TeamsLkGhostAppApplication.class, args);
    }

}
