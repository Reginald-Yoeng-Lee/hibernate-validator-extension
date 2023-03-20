package io.github.reginald.hv.extension;

public class StringEnumValueValidator extends EnumValueValidator<StringEnumValue, String> {

    private boolean allowEmpty;

    private String[] range;

    @Override
    public void initialize(StringEnumValue constraintAnnotation) {
        allowEmpty = constraintAnnotation.allowEmpty();
        range = constraintAnnotation.value();
    }

    @Override
    protected boolean allowEmpty() {
        return allowEmpty;
    }

    @Override
    protected String[] range() {
        return range;
    }
}
