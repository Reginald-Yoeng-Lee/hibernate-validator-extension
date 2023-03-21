package io.github.reginald.hv.extension.validators;

/**
 * Verifier that treats non-null and (if the value is a string) non-empty string as valid value.
 */
public class NonEmptyStringFieldVerifier extends NonNullFieldVerifier {

    /**
     * {@inheritDoc}
     *
     * @param validatingTarget The object being validated.
     * @param field The name of the field to be validated.
     * @param value The value of the field.
     * @return {@code true} if the value is NOT null or empty string.
     */
    @Override
    public boolean verify(Object validatingTarget, String field, Object value) {
        if (!super.verify(validatingTarget, field, value)) {
            return false;
        }
        if (value instanceof CharSequence) {
            return !((CharSequence) value).isEmpty();
        }
        return true;
    }
}
