package com.jalch.bank.rest;

import com.jalch.bank.domain.model.Transfers;
import com.jalch.bank.domain.service.transfer.PersonalAccountTransfersService;
import com.jalch.bank.rest.request.TransferRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/transactions")
@Validated
public class TransactionResource {

    private final PersonalAccountTransfersService accountTransfersService;

    @Autowired
    public TransactionResource(PersonalAccountTransfersService accountTransfersService) {
        this.accountTransfersService = accountTransfersService;
    }

    @PostMapping(path= "/transfers")
    public ResponseEntity makeTransfer(@Valid @RequestBody TransferRequest transferRequest){
        try {
            return this.accountTransfersService.make(transferRequest);
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "An error has occurred making the transfer"));
        }
    }

    @GetMapping(path = "/{accountId}/transfers")
    public ResponseEntity<Transfers> transfers(@PathVariable long accountId) {
        Transfers transfers = this.accountTransfersService.getAll(accountId);
        return transfers.getTransfers().isPresent() ?
                ResponseEntity.status(OK).body(transfers)
                : ResponseEntity.status(NOT_FOUND).build();
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
