package eu.oberon.oss.tools.resource.bundle.helper;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.function.BiFunction;

/**
 * Provides methods to interact with resource bundles, enabling the retrieval of localized strings and objects. The {@code ResourceBundleHelper} interface
 * allows for accessing resource bundle data with support for key formatting, object retrieval, and string localization.
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public interface ResourceBundleHelper {

    /**
     * Returns the key prefix that, together with a relative key, forms the full lookup key for accessing values from the resource bundle.
     *
     * @return The key prefix used to identify the localized strings within the resource bundle.
     *
     * @since 1.0.0
     */
    String getKeyPrefix();

    /**
     * Returns the resource bundle associated with this helper.
     *
     * @return The resource bundle used by this helper.
     *
     * @since 1.0.0
     */
    ResourceBundle getResourceBundle();

    /**
     * List of currently allowed delimiters to use when creating the actual lookup key from the keyPrefix and actual key names.
     * <p>
     * The specified characters in this list are used to separate the key-value pairs and could cause problems when attempting to access entries in the resource
     * bundle.
     *
     * @since 1.0.0
     */
    List<String> ILLEGAL_DELIMITER_LIST = List.of(":", "=", " ");

    /**
     * Retrieves a localized string from the resource bundle using the specified relativeKey.
     *
     * @param relativeKey The relativeKey that, together with the prefix key ({@link #getKeyPrefix()}) is used to identify the localized string within the
     *                    resource bundle.
     *
     * @return The localized string associated with the specified relativeKey.
     *
     * @throws NullPointerException     if the relativeKey is null.
     * @throws MissingResourceException if the relativeKey is not found in the resource bundle.
     * @since 1.0.0
     */
    @NotNull String getString(String relativeKey);

    /**
     * Retrieves a localized string from the resource bundle using the specified relativeKey and formats it using the provided formatter.
     *
     * @param relativeKey The relativeKey that, together with the prefix key ({@link #getKeyPrefix()}) is used to identify the localized string within the
     *                    resource bundle.
     * @param formatter   The formatter used to format the localized string.
     * @param values      The values to be used for formatting the localized string.
     *
     * @return The formatted localized string associated with the specified relativeKey.
     *
     * @throws NullPointerException     if the relativeKey is null.
     * @throws MissingResourceException if the relativeKey is not found in the resource bundle.
     * @since 1.0.0
     */
    @NotNull String getString(String relativeKey, BiFunction<String, Object[], String> formatter, Object... values);

    /**
     * Retrieves an object from the resource bundle using the specified relativeKey.
     *
     * @param relativeKey The relativeKey that, together with the prefix key ({@link #getKeyPrefix()}) is used to identify the localized string within the
     *                    resource bundle.
     * @param <T>         The object to be returned.
     *
     * @return The object associated with the specified relativeKey.
     *
     * @throws NullPointerException     if the relativeKey is null.
     * @throws MissingResourceException if the relativeKey is not found in the resource bundle.
     * @since 1.0.0
     */
    <T> T getObject(String relativeKey);
}
