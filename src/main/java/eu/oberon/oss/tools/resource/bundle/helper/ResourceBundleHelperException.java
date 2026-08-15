package eu.oberon.oss.tools.resource.bundle.helper;

/**
 * Exception class for ResourceBundleHelper.
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public class ResourceBundleHelperException extends RuntimeException {
    /**
     * Constructs a new ResourceBundleHelperException with the specified message and cause.
     *
     * @param message The detail message.
     * @param cause   The cause of the exception.
     *
     * @since 1.0.0
     */
    public ResourceBundleHelperException(String message, Exception cause) {
        super(message, cause);
    }
}
