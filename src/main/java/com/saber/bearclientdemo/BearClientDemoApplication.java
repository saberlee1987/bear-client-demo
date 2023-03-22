package com.saber.bearclientdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class BearClientDemoApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(BearClientDemoApplication.class, args);
        RestTemplate restTemplate = context.getBean(RestTemplate.class);
        ResponseEntity<String> responseEntity = restTemplate.exchange("http://api.springframework.guru/api/v1/beer", HttpMethod.GET, null, String.class);
        String body = responseEntity.getBody();
        System.out.println("body ====> "+body);
    }
}