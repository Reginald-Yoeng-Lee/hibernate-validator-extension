package io.github.reginald.hv.extension.validators;

import java.util.Arrays;

public class IntEnumValueValidator extends EnumValueValidator<IntEnumValue, Integer> {

    private boolean allowEmpty;

    private int[] range;

    @Override
    public void initialize(IntEnumValue constraintAnnotation) {
        allowEmpty = constraintAnnotation.allowEmpty();
        range = constraintAnnotation.value();
    }

    @Override
    protected boolean allowEmpty() {
        return allowEmpty;
    }

    @Override
    protected Integer[] range() {
        return Arrays.stream(range).boxed().toArray(Integer[]::new);
    }
}
