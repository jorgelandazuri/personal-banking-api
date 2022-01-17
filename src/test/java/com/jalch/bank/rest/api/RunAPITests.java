package com.jalch.bank.rest.api;

import com.jalch.bank.Application;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;

@RunWith(Cucumber.class)
@CucumberContextConfiguration
@SpringBootTest(classes = Application.class)
@CucumberOptions(
        glue = {"com.jalch.bank.rest.api.steps"},
        features = {"src/test/resources/bdd"}
)
public class RunAPITests {}
