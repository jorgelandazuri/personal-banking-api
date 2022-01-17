package com.jalch.bank.rest;

import com.jalch.bank.domain.model.AccountAndBalance;
import com.jalch.bank.domain.service.account.PersonalAccountService;
import com.jalch.bank.rest.request.AccountCreationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/accounts/personal")
@Validated
public class AccountResource {

    private final PersonalAccountService accountService;

    @Autowired
    public AccountResource(PersonalAccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity createAccount(@Valid @RequestBody AccountCreationRequest accountCreationRequest) {
        try {
            return accountService.create(accountCreationRequest);
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "An error has occurred creating the account"));
        }
    }

    @GetMapping(value = "/{accountId}/balance", produces = "application/json")
    public ResponseEntity<AccountAndBalance> balance(@PathVariable long accountId) {
        return accountService.getBalance(accountId)
                .map(accountAndBalance -> ResponseEntity.status(OK).body(accountService.getBalance(accountId).get()))
                .orElseGet(() -> ResponseEntity.status(NOT_FOUND).build());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public Map<String, String> handleRequestValidationExceptions(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

}
