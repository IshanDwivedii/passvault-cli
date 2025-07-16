package com.ishan.passvault;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PassVaultApplication {

    public static void main(String[] args) {
        System.setProperty("spring.main.web-application-type", "NONE");
        SpringApplication.run(PassVaultApplication.class, args);
    }

}
