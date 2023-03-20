package io.github.reginald.hv.extension;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

public interface PojoFieldAccessHelper {

    default Object accessField(Object pojo, String field) throws NoSuchFieldException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
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
            value = getFieldValue(pojo, rootField);
        }
        if (subField.isEmpty()) {
            return value;
        }
        return accessField(value, subField);
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
