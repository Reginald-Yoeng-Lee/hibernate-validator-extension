package io.github.reginald.hv.extension.validators;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AndValidatorTests extends BasicValidatorTests {

    @And(fields = {"a", "b", "c"})
    private record SimplePojo(String a, String b, String c) {
    }

    @Test
    public void testSimplePojo_ExactlyOneFieldExists() {
        var pojo = new SimplePojo("a", "", "");
        var violations = validator.validate(pojo).stream().map(ConstraintViolation::getMessage).toList();
        Assertions.assertEquals(1, violations.size());
        Assertions.assertTrue(violations.contains("All or none of fields [a, b, c] should be set."));
    }

    @Test
    public void testSimplePojo_TwoFieldsExist() {
        var pojo = new SimplePojo("a", "b", "");
        var violations = validator.validate(pojo).stream().map(ConstraintViolation::getMessage).toList();
        Assertions.assertEquals(1, violations.size());
        Assertions.assertTrue(violations.contains("All or none of fields [a, b, c] should be set."));
    }

    @Test
    public void testSimplePojo_NoneFieldsExist() {
        var pojo = new SimplePojo("", "", "");
        var validations = validator.validate(pojo);
        Assertions.assertTrue(validations.isEmpty());
    }

    @Test
    public void testSimplePojo_AllFieldsExist() {
        var pojo = new SimplePojo("a", "b", "c");
        var validations = validator.validate(pojo);
        Assertions.assertTrue(validations.isEmpty());
    }
}
