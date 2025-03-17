package com.douglasmarq.imageservice;

import com.amazonaws.SDKGlobalConfiguration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ImageserviceApplication {

    public static void main(String[] args) {
        System.setProperty(SDKGlobalConfiguration.DISABLE_CERT_CHECKING_SYSTEM_PROPERTY, "true");

        SpringApplication.run(ImageserviceApplication.class, args);
    }
}
