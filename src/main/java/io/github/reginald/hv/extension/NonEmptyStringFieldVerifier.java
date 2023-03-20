package io.github.reginald.hv.extension;

public class NonEmptyStringFieldVerifier extends NonNullFieldVerifier {

    @Override
    public boolean verify(String field, Object value) {
        if (!super.verify(field, value)) {
            return false;
        }
        if (value instanceof CharSequence) {
            return !((CharSequence) value).isEmpty();
        }
        return true;
    }
}
