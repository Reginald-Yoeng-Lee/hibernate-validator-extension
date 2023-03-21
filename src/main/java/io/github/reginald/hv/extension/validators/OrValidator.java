package io.github.reginald.hv.extension.validators;

import io.github.reginald.hv.extension.validators.internal.CrossingFieldsValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class OrValidator extends CrossingFieldsValidator<Or> {

    private String[] fields;

    private Class<? extends FieldAccessor> accessorClass;

    private Class<? extends FieldVerifier> verifierClass;

    @Override
    public void initialize(Or constraintAnnotation) {
        fields = constraintAnnotation.fields();
        accessorClass = constraintAnnotation.accessor();
        verifierClass = constraintAnnotation.fieldVerifier();
    }

    @Override
    protected String[] fields() {
        return fields;
    }

    @Override
    protected Class<? extends FieldAccessor> accessor() {
        return accessorClass;
    }

    @Override
    protected Class<? extends FieldVerifier> verifier() {
        return verifierClass;
    }

    @Override
    protected boolean isValid(FieldVerifier verifier, Object validatingTarget, FieldAccessor.FieldTuple[] fields, ConstraintValidatorContext context) {
        return Arrays.stream(fields).anyMatch(field -> verifier.verify(validatingTarget, field.field(), field.value()));
    }
}
