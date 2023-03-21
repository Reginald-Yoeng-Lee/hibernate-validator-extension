package io.github.reginald.hv.extension.validators.internal;

import io.github.reginald.hv.extension.validators.FieldAccessor;
import io.github.reginald.hv.extension.validators.FieldVerifier;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

/**
 * Base validator for validating fields' values within the target object.
 * <br>
 * According to the validation annotation, accesses the values of the designated fields by leveraging the {@link FieldAccessor}
 * and validates them by leveraging the {@link FieldVerifier}.
 *
 * @param <A> The specific annotation.
 */
public abstract class CrossingFieldsValidator<A extends Annotation> implements ConstraintValidator<A, Object> {

    /**
     * Provides the fields gonna be validated.
     *
     * @return The field's names.
     */
    protected abstract String[] fields();

    /**
     * The {@link FieldAccessor} for accessing the values of the validating fields.
     *
     * @return The accessor.
     */
    protected abstract Class<? extends FieldAccessor> accessor();

    /**
     * The {@link FieldVerifier} for verifying the values of the validating fields.
     *
     * @return The verifier.
     */
    protected abstract Class<? extends FieldVerifier> verifier();

    @Override
    public final boolean isValid(Object value, ConstraintValidatorContext context) {
        final var fields = fields();
        FieldAccessor accessor;
        try {
            accessor = accessor().getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalArgumentException("FieldAccessor " + accessor().getName() + " can NOT be initialized with no argument.", e);
        }
        final var fieldTuples = new FieldAccessor.FieldTuple[fields.length];
        var exceptionThrown = false;
        for (var i = 0; i < fields.length; i++) {
            try {
                fieldTuples[i] = accessor.access(value, fields[i]);
            } catch (FieldAccessor.AccessFieldException e) {
                exceptionThrown = true;
                buildValidatorContextOnAccessFieldException(fields[i], context, e.getCause());
            }
        }
        if (exceptionThrown) {
            return false;
        }

        return isValid(initializeFieldVerifier(verifier()), value, fieldTuples, context);
    }

    /**
     * Provides the constraint violation when {@link FieldAccessor.AccessFieldException}
     * occurs.
     *
     * @param field The field couldn't be accessed.
     * @param context context in which the constraint is evaluated
     * @param cause The specific exception causing the failure of the accessing.
     */
    protected void buildValidatorContextOnAccessFieldException(String field, ConstraintValidatorContext context, Throwable cause) {
        if (cause instanceof NoSuchFieldException) {
            context.buildConstraintViolationWithTemplate(String.format("Field [%s] doesn't exist.", field))
                    .addConstraintViolation();
        } else if (cause instanceof InvocationTargetException) {
            context.buildConstraintViolationWithTemplate(String.format("Access field [%s] with error: %s", field, cause.getMessage()))
                    .addConstraintViolation();
        } else if (cause instanceof IllegalAccessException) {
            context.buildConstraintViolationWithTemplate(String.format("Can NOT access field [%s]", field))
                    .addConstraintViolation();
        } else if (cause instanceof NoSuchMethodException) {
            context.buildConstraintViolationWithTemplate(String.format("Field [%s] getter method doesn't exist.", field))
                    .addConstraintViolation();
        } else {
            context.buildConstraintViolationWithTemplate(String.format("Access field [%s] with unknown error: %s", field, Optional.ofNullable(cause).map(Throwable::getMessage).orElse("(unknown)")));
        }
    }

    /**
     * Validates whether the values of the fields are valid.
     *
     * @param verifier The {@link FieldVerifier} using for validation.
     * @param validatingTarget The target object gonna be validated.
     * @param fields The fields gonna be validated.
     * @param context Context in which the constraint is evaluated
     * @return The validation result.
     */
    protected abstract boolean isValid(FieldVerifier verifier, Object validatingTarget, FieldAccessor.FieldTuple[] fields, ConstraintValidatorContext context);

    private FieldVerifier initializeFieldVerifier(Class<? extends FieldVerifier> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalArgumentException("FieldVerifier " + clazz.getName() + "can NOT be initialized with no argument.", e);
        }
    }

}
