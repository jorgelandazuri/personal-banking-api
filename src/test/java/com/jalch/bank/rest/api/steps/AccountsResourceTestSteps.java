package com.jalch.bank.rest.api.steps;

import io.cucumber.datatable.DataTable;
import io.cucumber.java8.En;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class AccountsResourceTestSteps extends APITestSteps implements En {

    private static final String BASE_URL = "http://localhost:8080/api/v1/";

    public AccountsResourceTestSteps() {
        AtomicReference<Response> createAccountResponse = new AtomicReference<>();
        AtomicReference<Map<String, Object>> request = new AtomicReference<>();

        Given("^An account needs to be created with:$",
                (t) -> {
                    Map<String, Object> inputMap = ((DataTable) t).asMap(String.class, Object.class);
                    Map<String, Object> requestMap = new HashMap<>();
                    requestMap.put("documentId", inputMap.get("documentId"));
                    requestMap.put("nameAndSurname", inputMap.get("nameAndSurname"));
                    requestMap.put("initialDeposit", new BigDecimal((String) inputMap.get("initialDeposit")));
                    request.set(requestMap);
                });

        When("^the create account request is executed$", () -> {
            String requestUrl = BASE_URL + "accounts/personal";
            createAccountResponse.set(
                    given()
                        .baseUri(requestUrl)
                        .accept("application/json")
                        .contentType(ContentType.JSON)
                        .body(request)
                        .log()
                        .all()
                    .when().post());
        });
        Then("^the account is created successfully$", () -> {
            createAccountResponse.get().then().statusCode(201);
            createAccountResponse.get().then().assertThat().body("message", equalTo("Account created!"));
        });
        And("^the balance for the account (\\d+) is \"([^\"]*)\"$", (Integer accountId , String amount) -> {
            Response response = given()
                    .baseUri(BASE_URL + "accounts/personal/"+ accountId+ "/balance")
                    .accept("application/json")
                    .contentType(ContentType.JSON)
                    .body(request)
                    .log()
                    .all()
                    .when().get();
            response.prettyPrint();
            response.then().statusCode(200);
            response.then().assertThat().body("balance", equalTo(Float.valueOf(amount)));
            response.then().assertThat().body("accountId", equalTo(accountId));
        });

        Then("^the account is not created due to invalid initial deposit$", () -> {
            createAccountResponse.get().then().statusCode(400);
            createAccountResponse.get().then().assertThat().body("initialDeposit", equalTo("must be greater than or equal to 0"));
        });

        Then("^the account is not created due to invalid document id$", () -> {
            createAccountResponse.get().then().statusCode(400);
            createAccountResponse.get().then().assertThat().body("documentId", equalTo("must not be blank"));
        });

        Then("^the account is not created due to invalid name and surname$", () -> {
            createAccountResponse.get().then().statusCode(400);
            createAccountResponse.get().then().assertThat().body("nameAndSurname", equalTo("must not be blank"));
        });

        And("^the balance for the account (\\d+) does not exist$", (Integer accountId) -> {
            Response response = given()
                    .baseUri(BASE_URL + "accounts/personal/"+ accountId+ "/balance")
                    .accept("application/json")
                    .contentType(ContentType.JSON)
                    .body(request)
                    .log()
                    .all()
                    .when().get();
            response.then().statusCode(404);
        });
    }
}
