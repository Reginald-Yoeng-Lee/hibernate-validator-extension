package io.github.reginald.hv.extension.validators;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

/**
 * Accessor for accessing any field of any object or map, including nested fields.
 * <br>
 * <i>This implementation using reflection for accessing the field values of the target object. So make sure to
 * {@code open} the class of the target pojo when using JPMS.</i>
 */
public class PojoFieldAccessor implements FieldAccessor {

    /**
     * Accesses the field of the very target pojo, including nested fields.
     * <br>
     * e.g. Suppose we have a record like
     * <pre>
     *     {@code
     *     record Pojo(String a, NestedPojo inner) {
     *         record NestedPojo(String b) {}
     *     }
     *     }
     * </pre>
     * The field {@code b} of the inner record {@code NestedPojo} could be referenced as {@code inner.b}. That is, if we
     * want to indicate that at least one of the string {@code a} or the string {@code b} contains a valid value, we could
     * use this annotation like
     * <pre>
     *     &#064;Or(fields = {"a", "inner.b"})
     *     record Pojo (String a, NestedPojo inner) {...}
     * </pre>
     * This works for both nested POJO and Map.
     *
     * @param pojo  The target object contains the field requires to be accessing. Could be a pojo or a map.
     * @param field The field to be accessing.
     * @return {@inheritDoc}
     * @throws AccessFieldException {@inheritDoc}
     */
    public FieldTuple access(Object pojo, String field) throws AccessFieldException {
        return new FieldTuple(field, accessValue(pojo, field));
    }

    private Object accessValue(Object pojo, String field) throws AccessFieldException {
        var firstSeparator = field.indexOf(".");
        var rootField = firstSeparator >= 0 ? field.substring(0, firstSeparator) : field;
        var subField = firstSeparator >= 0 ? field.substring(firstSeparator + 1) : "";
        if (rootField.isEmpty()) {
            throw new IllegalArgumentException("Empty field name is NOT allowed.");
        }
        Object value;
        if (pojo instanceof Map) {
            value = ((Map<?, ?>) pojo).get(rootField);
        } else {
            try {
                value = getFieldValue(pojo, rootField);
            } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException |
                     InvocationTargetException e) {
                throw new AccessFieldException("Try to access field " + rootField + " failed.", e);
            }
        }
        if (subField.isEmpty()) {
            return value;
        }
        return accessValue(value, subField);
    }

    private Object getFieldValue(Object pojo, String field) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        var clazz = pojo.getClass();
        var f = clazz.getDeclaredField(field);
        if (Modifier.isPublic(f.getModifiers())) {
            return f.get(pojo);
        }

        var fieldWithFirstLetterCapitalized = field.substring(0, 1).toUpperCase() + field.substring(1);
        var possibleGetter = new ArrayList<String>();
        possibleGetter.add(field);
        possibleGetter.add("get" + fieldWithFirstLetterCapitalized);
        if (Boolean.class.isAssignableFrom(f.getType())) {
            possibleGetter.add("is" + fieldWithFirstLetterCapitalized);
        }
        var getter = possibleGetter.stream()
                .map(name -> {
                    try {
                        return clazz.getMethod(name);
                    } catch (NoSuchMethodException e) {
                        return null;
                    }
                })
                .filter(method -> Optional.ofNullable(method).map(m -> Modifier.isPublic(m.getModifiers())).orElse(false))
                .findAny()
                .orElseThrow(() -> new NoSuchMethodException("No getter for field " + field + " found."));
        return getter.invoke(pojo);
    }
}
