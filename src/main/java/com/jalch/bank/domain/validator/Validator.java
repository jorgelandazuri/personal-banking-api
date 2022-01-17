package com.jalch.bank.domain.validator;

public interface Validator<I> {
    ValidationResult validate(I input);
}
