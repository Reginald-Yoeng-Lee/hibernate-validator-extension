package io.github.reginald.hv.extension;

public class NonNullFieldVerifier implements FieldVerifier {

    @Override
    public boolean verify(String field, Object value) {
        return value != null;
    }
}
