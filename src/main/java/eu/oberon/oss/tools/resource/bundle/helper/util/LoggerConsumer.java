package eu.oberon.oss.tools.resource.bundle.helper.util;

/**
 * An interface describing the function required to perform logging.
 * <p>
 * When the functional interfaces {@link #accept(Object, Object, String)} is called, it will provide the actual logger, the required log level and the message
 * to be logged; it is up to the implementor to handle the actual logging.
 *
 * @param <W> the type of logger
 * @param <L> the type of log level
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
@FunctionalInterface
public interface LoggerConsumer<W, L> {
    /**
     * Performs a logging operation using the specified logger instance, log level, and message.
     *
     * @param logger  the logger instance to use for logging
     * @param level   the log level to categorize the log entry
     * @param message the message content to be logged
     *
     * @since 1.0.0
     */
    void accept(W logger, L level, String message);
}
