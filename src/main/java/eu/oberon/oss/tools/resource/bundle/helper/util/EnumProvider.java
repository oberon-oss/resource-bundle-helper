package eu.oberon.oss.tools.resource.bundle.helper.util;

/**
 * Defines methods for enums that enable localization and exception creation through a resource bundle provider.
 * <p>
 * This interface is designed to be implemented by enums used with an {@link EnumProvider}. Implementing enums should provide functionality for formatting and
 * retrieving localized messages, as well as creating and logging exceptions or messages.
 *
 * @param <L> Describes the type of class that represents the log level
 * @param <E> Describes the type of enum that implements {@link ResourceBundleUtilityEnum}
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public interface EnumProvider<L, E extends Enum<E> & ResourceBundleUtilityEnum> {

    /**
     * Retrieves a formatted message associated with the specified enum constant and values.
     * <p>
     * This method uses a resource bundle to fetch and format the message.
     *
     * @param enumConstant the enum constant for which the localized message is to be retrieved
     * @param values       the array of objects to be included in the formatted message
     *
     * @return the formatted message as a string
     *
     * @since 1.0.0
     */
    String getMessage(E enumConstant, Object... values);

    /**
     * Logs a message associated with the specified enum constant and optional parameter values.
     * <p>
     * The message is typically retrieved from a resource bundle based on the provided enum constant and then formatted with the given values.
     *
     * @param enumConstant the enum constant representing the message to be logged
     * @param values       the array of objects to be included in the formatted message
     *
     * @since 1.0.0
     */
    void logMessage(E enumConstant, Object... values);

    /**
     * Logs a message associated with the specified enum constant, log level, and optional parameter values.
     * <p>
     * The message is typically retrieved from a resource bundle based on the provided enum constant and then formatted with the given values.
     *
     * @param enumConstant the enum constant representing the message to be logged
     * @param level        the log level to categorize the log entry
     * @param values       the array of objects to be included in the formatted message
     *
     * @since 1.0.0
     */
    void logMessage(E enumConstant, L level, Object... values);

    /**
     * Creates an exception of type {@code <T>}, associated with the provided enum constant and optional parameter values.
     * <p>
     * The exception's message is typically retrieved from a resource bundle, formatted using the given values, and passed to the specified exception class.
     *
     * @param <T>            the type of exception to be created, which must extend {@link Exception}
     * @param enumConstant   the enum constant representing the localized message for the exception
     * @param exceptionClass the class of the exception to be created
     * @param values         the array of objects to be included in the formatted message
     *
     * @return an instance of the specified exception type with the localized message
     *
     * @since 1.0.0
     */
    <T extends Exception> T getException(E enumConstant, Class<T> exceptionClass, Object... values);

    /**
     * Creates an exception of type {@code <T>}, associated with the provided enum constant, optional parameter values, and a cause.
     * <p>
     * The exception's message is typically retrieved from a resource bundle, formatted using the given values, and passed to the specified exception class
     * along with the cause.
     *
     * @param <T>            the type of exception to be created, which must extend {@link Exception}
     * @param enumConstant   the enum constant representing the localized message for the exception
     * @param exceptionClass the class of the exception to be created
     * @param cause          the throwable cause to be associated with the exception
     * @param values         the array of objects to be included in the formatted message for the exception
     *
     * @return an instance of the specified exception class, initialized with the formatted message and the given cause
     *
     * @since 1.0.0
     */
    <T extends Exception> T getException(E enumConstant, Class<T> exceptionClass, Throwable cause, Object... values);

}
