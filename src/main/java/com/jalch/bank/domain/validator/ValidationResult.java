package com.jalch.bank.domain.validator;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class ValidationResult {

    public enum Result {
        VALID, INVALID
    }
    private Result result;
    private Map<String, String> validationMessages;

}
