package eu.oberon.oss.tools.resource.bundle.helper.util;

/**
 * Defines methods for enums that enable localization and exception handling through a resource bundle provider. This interface is designed to be implemented by
 * enums used with an {@link EnumProvider}. Implementing enums should provide functionality for formatting and retrieving localized messages, as well as
 * creating and logging exceptions or messages.
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public interface ResourceBundleUtilityEnum {
    /**
     * Retrieves the property name associated with the enum constant.
     *
     * @return the property name as a string, typically used for localization or resource bundle lookups
     *
     * @since 1.0.0
     */
    String getPropertyName();

    /**
     * Retrieves a formatted message based on the provided parameter values.
     * <p>
     * The method typically uses a resource bundle and formatting logic to produce the localized message.
     *
     * @param values an array of objects to be included in the formatted message. These values may be placeholders in the message template.
     *
     * @return the formatted message as a string
     *
     * @since 1.0.0
     */
    String getMessage(Object... values);

    /**
     * Creates an exception of type {@code T} the specified type using the provided exception class and optional parameter values.
     * <p>
     * The exception's message is typically derived from the localized messages associated with the implementing enum.
     *
     * @param <T>            the type of the exception to be created, which must extend {@link Exception}
     * @param exceptionClass the class of the exception to be created
     * @param values         the array of objects to be included in the formatted message for the exception
     *
     * @return an instance of the specified exception type with a localized message constructed using the given values
     *
     * @since 1.0.0
     */
    <T extends Exception> T getException(Class<T> exceptionClass, Object... values);

    /**
     * Creates an exception of type {@code T}, with an optional cause and parameterized message values.
     * <p>
     * The exception's message is constructed using the implementing enum's localized message logic, formatted with the passed values.
     *
     * @param <T>            the type of the exception to be created, which must extend {@link Exception}
     * @param exceptionClass the class of the exception to be created
     * @param cause          the throwable cause to be associated with the exception
     * @param values         an optional array of objects to be included in the formatted message for the exception
     *
     * @return an instance of the specified exception type, initialized with the formatted message and the given cause
     *
     * @since 1.0.0
     */
    <T extends Exception> T getException(Class<T> exceptionClass, Throwable cause, Object... values);

    /**
     * Logs a message constructed using the provided parameter values.
     * <p>
     * The implementation may format these values into a predefined message template or use them as-is.
     *
     * @param values an array of objects to be included in the logged message
     *
     * @since 1.0.0
     */
    void logMessage(Object... values);

    /**
     * Logs a message at a specific log level, constructed using the provided parameter values.
     * <p>
     * The implementation may format the provided values into a predefined message template or use them as-is. This method facilitates logging messages with
     * different levels of granularity or severity.
     *
     * @param <L>    the type representing the log level
     * @param level  the logging level used to categorize the log entry
     * @param values an array of objects to be included in the logged message
     *
     * @since 1.0.0
     */
    <L> void logMessage(L level, Object... values);
}
