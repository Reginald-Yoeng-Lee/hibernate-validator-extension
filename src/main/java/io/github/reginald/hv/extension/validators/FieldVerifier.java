package io.github.reginald.hv.extension.validators;

/**
 * Verifier for determining whether the field value is valid.
 * <br>
 * <b>
 *     Notice: All the implementations should provide a constructor with no arguments.
 * </b>
 */
public interface FieldVerifier {

    /**
     * Determines whether the field's value of the target object is valid.
     *
     * @param validatingTarget The object being validated.
     * @param field The name of the field to be validated.
     * @param value The value of the field.
     * @return The validation result. {@code true} for valid.
     */
    boolean verify(Object validatingTarget, String field, Object value);
}
