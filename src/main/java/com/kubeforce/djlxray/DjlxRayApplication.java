package com.kubeforce.djlxray;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class DjlxRayApplication {

    public static void main(String[] args) {
        SpringApplication.run(DjlxRayApplication.class, args);

    }
    @Bean
    public XRAYFunction xrayFunction() {
        return new XRAYFunction();
    }
    }


