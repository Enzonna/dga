package com.enzo.dga;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DgaApplication {

    public static void main(String[] args) {
        SpringApplication.run(DgaApplication.class, args);
    }

}
