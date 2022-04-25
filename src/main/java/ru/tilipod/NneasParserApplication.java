package ru.tilipod;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class NneasParserApplication {

    public static void main(String[] args) {
            SpringApplication.run(NneasParserApplication.class, args);
        }
}
