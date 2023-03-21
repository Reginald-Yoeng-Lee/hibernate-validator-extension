package io.github.reginald.hv.extension.validators;

import java.util.Arrays;

public class LongEnumValueValidator extends EnumValueValidator<LongEnumValue, Long> {

    private boolean allowEmpty;
    private long[] range;

    @Override
    public void initialize(LongEnumValue constraintAnnotation) {
        allowEmpty = constraintAnnotation.allowEmpty();
        range = constraintAnnotation.value();
    }

    @Override
    protected boolean allowEmpty() {
        return allowEmpty;
    }

    @Override
    protected Long[] range() {
        return Arrays.stream(range).boxed().toArray(Long[]::new);
    }
}
