package io.github.reginald.hv.extension;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class OrValidator implements ConstraintValidator<Or, Object>, PojoFieldAccessHelper {

    private String[] fields;

    private boolean allowEmptyString;

    @Override
    public void initialize(Or constraintAnnotation) {
        fields = constraintAnnotation.fields();
        allowEmptyString = constraintAnnotation.allowEmptyString();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        var fieldValues = new Object[fields.length];
        var exceptionThrown = false;
        for (var i = 0; i < fields.length; i++) {
            try {
                fieldValues[i] = accessField(value, fields[i]);
            } catch (NoSuchFieldException e) {
                exceptionThrown = true;
                context.buildConstraintViolationWithTemplate(String.format("Field [%s] doesn't exist.", fields[i]))
                        .addConstraintViolation();
            } catch (InvocationTargetException e) {
                exceptionThrown = true;
                context.buildConstraintViolationWithTemplate(String.format("Access field [%s] with error: %s", fields[i], e.getMessage()))
                        .addConstraintViolation();
            } catch (IllegalAccessException e) {
                exceptionThrown = true;
                context.buildConstraintViolationWithTemplate(String.format("Can NOT access field [%s]", fields[i]))
                        .addConstraintViolation();
            } catch (NoSuchMethodException e) {
                exceptionThrown = true;
                context.buildConstraintViolationWithTemplate(String.format("Field [%s] getter method doesn't exist.", fields[i]))
                        .addConstraintViolation();
            }
        }
        if (exceptionThrown) {
            return false;
        }

        return Arrays.stream(fieldValues).anyMatch(v -> {
            if (v == null) {
                return false;
            }
            return !(v instanceof CharSequence) || allowEmptyString || ((CharSequence) v).length() != 0;
        });
    }
}
