package com.jalch.bank.rest.api.steps;

import io.cucumber.datatable.DataTable;
import io.cucumber.java8.En;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static io.restassured.RestAssured.given;
import static java.lang.Integer.valueOf;
import static org.hamcrest.CoreMatchers.equalTo;

public class TransferResourceTestSteps extends APITestSteps implements En {

    private static final String BASE_URL = "http://localhost:8080/api/v1/";

    public TransferResourceTestSteps() {
        AtomicReference<Response> transferResponse = new AtomicReference<>();
        AtomicReference<Map<String, Object>> request = new AtomicReference<>();

        Given("^A transfer needs to be made with:$",
                (t) -> {
                    Map<String, Object> inputMap = ((DataTable) t).asMap(String.class, Object.class);
                    Map<String, Object> requestMap = new HashMap<>();
                    requestMap.put("sourceAccountId", valueOf((String)inputMap.get("sourceAccountId")));
                    requestMap.put("destinationAccountId", valueOf((String)inputMap.get("destinationAccountId")));
                    requestMap.put("amount", new BigDecimal((String) inputMap.get("amount")));
                    request.set(requestMap);
                });

        When("^the transfer request is executed$", () -> {
            String requestUrl = BASE_URL + "transactions/transfers";
            transferResponse.set(
                    given()
                        .baseUri(requestUrl)
                        .accept("application/json")
                        .contentType(ContentType.JSON)
                        .body(request)
                        .log()
                        .all()
                    .when().post());
        });

        Then("^the transfer is successful$", () -> {
            transferResponse.get().then().statusCode(HttpStatus.CREATED.value());
            transferResponse.get().then().assertThat().body("message", equalTo("Transfer completed."));
        });

        Then("^the transfer is unsuccessful with not existing destination account message$", () -> {
            transferResponse.get().then().statusCode(HttpStatus.BAD_REQUEST.value());
            transferResponse.get().then().assertThat().body("destinationAccountId",
                    equalTo("destination account does not exist"));
        });

        Then("^the transfer is unsuccessful with not existent account or enough balance$", () -> {
            transferResponse.get().then().statusCode(HttpStatus.BAD_REQUEST.value());
            transferResponse.get().then().assertThat().body("accountBalance",
                    equalTo("source account does not exist or has not enough balance"));
        });

        And("^the transfers list for account (\\d+) has (\\d+) transactions$",  (Integer accountId , Integer transfers) -> {
            Response response = given()
                    .baseUri(BASE_URL + "transactions/"+ accountId+ "/transfers")
                    .accept("application/json")
                    .contentType(ContentType.JSON)
                    .body(request)
                    .log()
                    .all()
                    .when().get();
            response.then().statusCode(HttpStatus.OK.value());
            response.then().assertThat().body("transfers.size()", equalTo(transfers));
        });

        And("^the transfers list for account (\\d+) does not exists$", (Integer accountId) -> {
            Response response = given()
                    .baseUri(BASE_URL + "transactions/"+ accountId+ "/transfers")
                    .accept("application/json")
                    .contentType(ContentType.JSON)
                    .body(request)
                    .log()
                    .all()
                    .when().get();
            response.then().statusCode(HttpStatus.NOT_FOUND.value());
        });

    }
}
