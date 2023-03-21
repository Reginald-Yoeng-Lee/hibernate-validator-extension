package io.github.reginald.hv.extension.validators;

/**
 * Verifier that treats the non-null value as valid value.
 */
public class NonNullFieldVerifier implements FieldVerifier {

    /**
     * {@inheritDoc}
     *
     * @param validatingTarget The object being validated.
     * @param field The name of the field to be validated.
     * @param value The value of the field.
     * @return {@code true} if the value is NOT null.
     */
    @Override
    public boolean verify(Object validatingTarget, String field, Object value) {
        return value != null;
    }
}
