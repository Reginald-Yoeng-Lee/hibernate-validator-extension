package io.github.reginald.hv.extension;

import io.github.reginald.hv.extension.internal.CrossingFieldsValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.stream.IntStream;

public class OrValidator extends CrossingFieldsValidator<Or> {

    private String[] fields;

    private Class<? extends FieldAccessor> accessorClass;

    private FieldVerifier fieldVerifier;

    @Override
    public void initialize(Or constraintAnnotation) {
        fields = constraintAnnotation.fields();
        accessorClass = constraintAnnotation.accessor();
        fieldVerifier = initializeFieldVerifier(constraintAnnotation.fieldVerifier());
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
    protected boolean isValid(Object[] fieldValues, ConstraintValidatorContext context) {
        return IntStream.range(0, fieldValues.length).anyMatch(i -> fieldVerifier.verify(fields[i], fieldValues[i]));
    }
}
