package io.github.reginald.hv.extension;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class OrValidatorTests {

    private static Validator validator;

    @BeforeAll
    public static void initValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Or(fields = {"a", "b"})
    private record SimplePojo(
            String a,
            String b
    ) {
    }

    @Test
    public void testSimplePojo_noneExists() {
        var pojo = new SimplePojo(null, null);
        var validations = validator.validate(pojo);
        Assertions.assertEquals(1, validations.size());
    }

    @Test
    public void testSimplePojo_oneOfExists() {
        var pojo = new SimplePojo("a", null);
        var violations = validator.validate(pojo);
        Assertions.assertTrue(violations.isEmpty());
    }

    private static final class SimplePojoList {

        @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
        private final List<@Or(fields = {"a", "b"}) SimplePojo> list = new ArrayList<>();

        public SimplePojoList addPojo(SimplePojo pojo) {
            list.add(pojo);
            return this;
        }
    }

    @Test
    public void testSimplePojoList_oneViolated() {
        var list = new SimplePojoList()
                .addPojo(new SimplePojo("a", "b"))
                .addPojo(new SimplePojo(null, ""))
                .addPojo(new SimplePojo("a", ""));
        var violations = validator.validate(list).stream().map(ConstraintViolation::getMessage).toList();
        Assertions.assertEquals(1, violations.size());
        Assertions.assertTrue(violations.contains("At least one of the fields [a, b] required."));
    }

    @Test
    public void testSimplePojoList_allViolated() {
        var list = new SimplePojoList()
                .addPojo(new SimplePojo("", null))
                .addPojo(new SimplePojo("", ""))
                .addPojo(new SimplePojo(null, ""));
        var violations = validator.validate(list).stream().map(ConstraintViolation::getMessage).toList();
        Assertions.assertEquals(3, violations.size());
        Assertions.assertTrue(violations.contains("At least one of the fields [a, b] required."));
    }

    @SuppressWarnings({"FieldCanBeLocal", "unused", "ClassCanBeRecord"})
    @Or(fields = {"a", "b"})
    private static final class SimplePrivatePojo {

        private final String a;
        private final String b;

        public SimplePrivatePojo(String a, String b) {
            this.a = a;
            this.b = b;
        }
    }

    @Test
    public void testSimplePrivatePojo_nonAccessible() {
        var pojo = new SimplePrivatePojo("a", "b");
        var validations = validator.validate(pojo).stream().map(ConstraintViolation::getMessage).toList();
        Assertions.assertEquals(3, validations.size());
        Assertions.assertTrue(validations.contains("Field [a] getter method doesn't exist."));
        Assertions.assertTrue(validations.contains("Field [b] getter method doesn't exist."));
        Assertions.assertTrue(validations.contains("At least one of the fields [a, b] required."));
    }

    @Or(fields = {"a", "n.b"})
    private record NestedPojo(
            String a,
            Nested n
    ) {

        public record Nested(
                String b,
                String c
        ) {
        }
    }

    @Test
    public void testNestedPojo_nestedFieldExists() {
        var pojo = new NestedPojo(null, new NestedPojo.Nested("b", "c"));
        var validations = validator.validate(pojo);
        Assertions.assertTrue(validations.isEmpty());
    }

    @Test
    public void testNestedPojo_nestedFieldNonExists() {
        var pojo = new NestedPojo(null, new NestedPojo.Nested(null, "c"));
        var validations = validator.validate(pojo).stream().map(ConstraintViolation::getMessage).toList();
        Assertions.assertEquals(1, validations.size());
        Assertions.assertTrue(validations.contains("At least one of the fields [a, n.b] required."));
    }

    @Or(fields = {"a", "map.b"})
    private record NestedMapPojo(
            String a,
            Map<String, String> map
    ) {
    }

    @Test
    public void testNestedMapPojo_nestedKeyOfMapExists() {
        var map = Collections.singletonMap("b", "b");
        var pojo = new NestedMapPojo(null, map);
        var validations = validator.validate(pojo);
        Assertions.assertTrue(validations.isEmpty());
    }

    @Test
    public void testNestedMapPojo_nestedKeyOfMapNonExists() {
        var map = Collections.singletonMap("c", "c");
        var pojo = new NestedMapPojo(null, map);
        var validations = validator.validate(pojo).stream().map(ConstraintViolation::getMessage).toList();
        Assertions.assertEquals(1, validations.size());
        Assertions.assertTrue(validations.contains("At least one of the fields [a, map.b] required."));
    }

    @Test
    public void testNestedMapPojo_nestedKeyOfMapEmpty() {
        var map = Collections.singletonMap("b", "");
        var pojo = new NestedMapPojo(null, map);
        var validations = validator.validate(pojo).stream().map(ConstraintViolation::getMessage).toList();
        Assertions.assertEquals(1, validations.size());
        Assertions.assertTrue(validations.contains("At least one of the fields [a, map.b] required."));
    }

    @Or(fields = {"a", "b"}, allowEmptyString = true)
    private record EmptyStringPojo(
            String a,
            String b
    ) {
    }

    @Test
    public void testEmptyStringPojo_emptyStringExists() {
        var pojo = new EmptyStringPojo(null, "");
        var validations = validator.validate(pojo);
        Assertions.assertTrue(validations.isEmpty());
    }

    @Or(fields = {"a", "b"})
    @Or(fields = {"c", "d"})
    private record MultiConstraintPojo(
            String a,
            String b,
            String c,
            String d
    ) {
    }

    @Test
    public void testMultiConstraintPojo_oneViolated() {
        var pojo = new MultiConstraintPojo("a", null, null, null);
        var validations = validator.validate(pojo).stream().map(ConstraintViolation::getMessage).toList();
        Assertions.assertEquals(1, validations.size());
        Assertions.assertTrue(validations.contains("At least one of the fields [c, d] required."));
    }

    @Test
    public void testMultiConstraintPojo_allViolated() {
        var pojo = new MultiConstraintPojo(null, null, null, null);
        var validations = validator.validate(pojo).stream().map(ConstraintViolation::getMessage).toList();
        Assertions.assertEquals(2, validations.size());
        Assertions.assertTrue(validations.contains("At least one of the fields [a, b] required."));
        Assertions.assertTrue(validations.contains("At least one of the fields [c, d] required."));
    }
}
