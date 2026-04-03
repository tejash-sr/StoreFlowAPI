package com.grootan.storeflow.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ValidSkuValidatorTest {

    private ValidSkuValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new ValidSkuValidator();
        context = mock(ConstraintValidatorContext.class);
    }

    @Test
    void isValid_withValidSku_returnsTrue() {
        assertTrue(validator.isValid("SKU-123", context));
        assertTrue(validator.isValid("ABC-DEF-GHI", context));
        assertTrue(validator.isValid("12345", context));
    }

    @Test
    void isValid_withInvalidSku_returnsFalse() {
        assertFalse(validator.isValid("sku-123", context)); // lowercase
        assertFalse(validator.isValid("SKU_123", context)); // underscore
        assertFalse(validator.isValid("SKU 123", context)); // space
        assertFalse(validator.isValid("SKU@123", context)); // special char
    }

    @Test
    void isValid_withNullOrEmpty_returnsTrue() {
        assertTrue(validator.isValid(null, context));
        assertTrue(validator.isValid("", context));
        assertTrue(validator.isValid("   ", context));
    }

    @Test
    void isValid_withOnlyHyphens_returnsTrue() {
        assertTrue(validator.isValid("-", context));
        assertTrue(validator.isValid("---", context));
    }

    @Test
    void isValid_withMixedValidAndInvalid_returnsFalse() {
        assertFalse(validator.isValid("SKU-123*", context));
        assertFalse(validator.isValid("SK1-23A_B", context));
    }
}
