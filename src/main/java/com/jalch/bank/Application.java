package com.jalch.bank;

import com.jalch.bank.domain.service.data.TestDataInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@SpringBootApplication
@Configuration
public class Application {

    @Autowired
    private TestDataInitializer testDataInitializer;

    @PostConstruct
    public void init() {
        testDataInitializer.init();
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
