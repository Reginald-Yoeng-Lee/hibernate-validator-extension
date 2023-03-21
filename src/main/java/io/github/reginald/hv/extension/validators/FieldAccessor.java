package io.github.reginald.hv.extension.validators;

/**
 * Accessor of any field of any target object.
 * <br>
 * Generally be used by the validation annotations and the related validators. The implementations should provide the proper
 * ways to access the value of the field of the specific object.
 * <br>
 * <b>
 *     Notice: All the implementations should provide a constructor with no arguments.
 * </b>
 */
public interface FieldAccessor {

    /**
     * Accesses the specific field of the target object.
     *
     * @param bean The target object contains the field requires to be accessing.
     * @param field The field to be accessing.
     * @return The value of the field within the {@code bean}
     * @throws AccessFieldException Throws when unable to access the field.
     */
    FieldTuple access(Object bean, String field) throws AccessFieldException;

    /**
     * Wrapper for the actual {@link Exception} causing the failure of accessing the field.
     */
    class AccessFieldException extends Exception {

        public AccessFieldException() {
            super();
        }

        public AccessFieldException(String message) {
            super(message);
        }

        public AccessFieldException(String message, Throwable cause) {
            super(message, cause);
        }

        public AccessFieldException(Throwable cause) {
            super(cause);
        }
    }

    record FieldTuple(String field, Object value) {
    }
}
