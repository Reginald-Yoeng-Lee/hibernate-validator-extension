package io.github.reginald.hv.extension.internal;

import io.github.reginald.hv.extension.FieldAccessor;
import io.github.reginald.hv.extension.FieldVerifier;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public abstract class CrossingFieldsValidator<A extends Annotation> implements ConstraintValidator<A, Object> {

    protected abstract String[] fields();

    protected abstract Class<? extends FieldAccessor> accessor();

    @Override
    public final boolean isValid(Object value, ConstraintValidatorContext context) {
        var fields = fields();
        FieldAccessor accessor;
        try {
            accessor = accessor().getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalArgumentException("FieldAccessor " + accessor().getName() + " can NOT be initialized with no argument.", e);
        }
        var fieldValues = new Object[fields.length];
        var exceptionThrown = false;
        for (var i = 0; i < fields.length; i++) {
            try {
                fieldValues[i] = accessor.access(value, fields[i]);
            } catch (FieldAccessor.AccessFieldException e) {
                exceptionThrown = true;
                buildValidatorContextOnAccessFieldException(fields[i], context, e.getCause());
            }
        }
        if (exceptionThrown) {
            return false;
        }

        return isValid(fieldValues, context);
    }

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

    protected abstract boolean isValid(Object[] fieldValues, ConstraintValidatorContext context);

    protected final FieldVerifier initializeFieldVerifier(Class<? extends FieldVerifier> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalArgumentException("FieldVerifier " + clazz.getName() + "can NOT be initialized with no argument.", e);
        }
    }
}
