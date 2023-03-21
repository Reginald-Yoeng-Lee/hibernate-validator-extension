package io.github.reginald.hv.extension.validators;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;

public abstract class BasicValidatorTests {

    protected static Validator validator;

    @BeforeAll
    public static void initValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }
}
