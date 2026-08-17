package eu.oberon.oss.tools.resource.bundle.helper.util;

import eu.oberon.oss.tools.resource.bundle.helper.ResourceBundleHelper;
import eu.oberon.oss.tools.resource.bundle.helper.ResourceBundleHelperException;
import eu.oberon.oss.tools.resource.bundle.helper.ResourceBundleHelperRegistry;
import org.slf4j.Logger;
import org.slf4j.event.Level;
import org.slf4j.helpers.MessageFormatter;

import java.util.function.BiFunction;

/**
 * Utility class that provides a simplified initialization mechanism for enums implementing {@link ResourceBundleUtilityEnum}.
 * <p>
 * This class acts as a convenience wrapper around {@link DefaultEnumProvider#initForEnum}, providing a streamlined way to initialize enum constants with
 * resource bundle capabilities using SLF4J logging.
 * <p>
 * The initialization process injects an {@link EnumProvider} instance into the target enum class, enabling it to:
 * <ul>
 *   <li>Retrieve localized messages from resource bundles</li>
 *   <li>Log messages using SLF4J loggers</li>
 *   <li>Create exceptions with localized messages</li>
 * </ul>
 * <p>
 * <b>Requirements for the target enum class:</b>
 * <ul>
 *   <li>Must implement {@link ResourceBundleUtilityEnum}</li>
 *   <li>Must declare a public static method: {@code public static <L> void init(EnumProvider<L, E> provider)}</li>
 * </ul>
 * <p>
 * This class uses SLF4J's {@link org.slf4j.event.Level} for log levels and {@link org.slf4j.helpers.MessageFormatter}
 * for message formatting with placeholders.
 *
 * @author TigerLilly64
 * @see ResourceBundleUtilityEnum
 * @see EnumProvider
 * @see DefaultEnumProvider
 * @see ResourceBundleHelper
 * @since 1.0.0
 */
public class InitEnumProvider {

    private InitEnumProvider() {
        // Prevent instantiation - this is a utility class
    }

    public static final LoggerConsumer<Logger, Level> LOGGER_CONSUMER = (logger, level, message) -> {
        if (logger.isEnabledForLevel(level)) {
            logger.atLevel(level).log(message);
        }
    };
    public  static final BiFunction<String, Object[], String> LOG_FORMATTER = (messageFormatString, args) -> {
        if (args == null || args.length == 0) {
            return messageFormatString;
        }
        return MessageFormatter.arrayFormat(messageFormatString, args).getMessage();
    };

    /**
     * Initializes an enum class with resource bundle capabilities using a registered {@link ResourceBundleHelper}.
     * <p>
     * This method retrieves a {@link ResourceBundleHelper} instance from the {@link ResourceBundleHelperRegistry} using the provided helper key, then delegates
     * to {@link DefaultEnumProvider#initForEnum} to perform the actual initialization.
     * <p>
     * The method automatically configures:
     * <ul>
     *   <li>Message formatting using SLF4J's {@link MessageFormatter}</li>
     *   <li>Log message consumption using SLF4J's fluent logging API</li>
     *   <li>Default log level for messages without explicit level specification</li>
     * </ul>
     * <p>
     * <b>Usage example:</b>
     * <pre>{@code
     * public enum MyMessages implements ResourceBundleUtilityEnum {
     *     ERROR_001("error.001"),
     *     INFO_001("info.001");
     *
     *     private final String propertyName;
     *     private static EnumProvider<Level, MyMessages> provider;
     *
     *     MyMessages(String propertyName) {
     *         this.propertyName = propertyName;
     *     }
     *
     *     @Override
     *     public String getPropertyName() {
     *         return propertyName;
     *     }
     *
     *     public static <L> void init(EnumProvider<L, MyMessages> provider) {
     *         MyMessages.provider = (EnumProvider<Level, MyMessages>) provider;
     *     }
     *
     *     public String getMessage(Object... args) {
     *         return provider.getMessage(this, args);
     *     }
     *
     *     static {
     *         InitEnumProvider.init(
     *             MyMessages.class,
     *             "myMessagesHelper",
     *             LoggerFactory.getLogger(MyMessages.class),
     *             Level.INFO
     *         );
     *     }
     * }
     * }</pre>
     *
     * @param <E>       the type of the enum, which must extend {@link Enum} and implement {@link ResourceBundleUtilityEnum}
     * @param enumClass the class object of the enum to be initialized; must not be null
     * @param keyPrefix the key used to retrieve the {@link ResourceBundleHelper} from the registry; must not be null
     * @param logger    the SLF4J logger instance to be used for logging operations; must not be null
     * @param dftLevel  the default log level to use when logging messages without an explicit level; must not be null
     *
     * @throws ResourceBundleHelperException if the enum class does not declare the required init method with the correct signature, or if an error occurs
     *                                       during reflective invocation of the init method
     * @throws NullPointerException          if keyPrefix does not correspond to a registered helper in the registry
     * @see ResourceBundleHelperRegistry#retrieve(String)
     * @see DefaultEnumProvider#initForEnum
     * @since 1.0.0
     */
    public static <E extends Enum<E> & ResourceBundleUtilityEnum> void init(
            Class<E> enumClass,
            String keyPrefix,
            Logger logger,
            Level dftLevel
    ) {
        ResourceBundleHelper helper = ResourceBundleHelperRegistry.retrieve(keyPrefix);
        if (helper == null) {
            throw new NullPointerException("No ResourceBundleHelper registered for key prefix: " + keyPrefix);
        }
        DefaultEnumProvider.initForEnum(enumClass, helper, logger, dftLevel, LOGGER_CONSUMER, LOG_FORMATTER);
    }
}