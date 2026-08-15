package eu.oberon.oss.tools.resource.bundle.helper;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;


/**
 * A utility class for simplifying access to a {@link ResourceBundle} with a specific key prefix. This class helps in organizing and retrieving localized
 * strings or objects from resource bundles using a predefined prefix.
 * <p>
 * The prefix specified at construction time will be used whenever a call is made to retrieve data from the {@link ResourceBundle} that was also specified at
 * construction time.
 * <p>
 * Note: When attempting to register a resolver with a key prefix that is already in use, an {@link IllegalArgumentException} will be thrown. To replace an
 * existing resolver, use the {@link #unRegister(String)} method to unregister the existing resolver before registering the new one.
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class DefaultResourceBundleHelper implements ResourceBundleHelper {
    private final ResourceBundle bundle;
    private final String keyPrefix;
    private final String delimiter;

    /**
     * Creates a new instance of {@link DefaultResourceBundleHelper} with the specified {@link ResourceBundle} and key prefix, using the default delimiter.
     *
     * @param bundle    The {@link ResourceBundle} to be used for accessing localized strings and objects.
     * @param keyPrefix The key prefix used to identify specific keys within the resource bundle.
     *
     * @throws NullPointerException if either the {@code bundle} or {@code keyPrefix} is null.
     * @since 1.0.0
     */
    public DefaultResourceBundleHelper(ResourceBundle bundle, String keyPrefix) {
        this(bundle, keyPrefix, ".");
    }


    /**
     * Creates a new instance of {@link DefaultResourceBundleHelper} with the specified {@link ResourceBundle} and key prefix.
     * <p>
     * If not already registered, it registers itself with the key prefix.
     *
     * @param bundle    The {@link ResourceBundle} to be used for accessing localized strings and objects.
     * @param keyPrefix The key prefix used to identify specific keys within the resource bundle.
     * @param delimiter The delimiter used to separate keys from values in the resource bundle.
     *
     * @throws NullPointerException     if either the {@code bundle} or {@code keyPrefix} is null.
     * @throws IllegalArgumentException if the {@code delimiter} is empty or whitespace only, or if it is not one of the allowed values.
     * @see #ILLEGAL_DELIMITER_LIST
     * @since 1.0.0
     */
    public DefaultResourceBundleHelper(ResourceBundle bundle, String keyPrefix, String delimiter) {
        this.bundle = Objects.requireNonNull(bundle);
        this.keyPrefix = Objects.requireNonNull(keyPrefix);
        this.delimiter = Objects.requireNonNull(delimiter);

        if (delimiter.trim().isEmpty()) {
            throw new IllegalArgumentException("Delimiter cannot be empty or whitespace only.");
        }

        if (ILLEGAL_DELIMITER_LIST.contains(delimiter)) {
            throw new IllegalArgumentException("Delimiter " + delimiter + " cannot be either of: " + ILLEGAL_DELIMITER_LIST);
        }
    }

    @Override
    public @NotNull String getString(String relativeKey) {
        return bundle.getString(keyPrefix + delimiter + relativeKey);
    }

    @Override
    public @NotNull String getString(String relativeKey, BiFunction<String, Object[], String> formatter, Object... values) {
        return formatter.apply(keyPrefix + delimiter + relativeKey, values);
    }

    @Override
    public <T> T getObject(String relativeKey) {
        //noinspection unchecked
        return (T) bundle.getObject(keyPrefix + delimiter + relativeKey);
    }

    @Override
    public String getKeyPrefix() {
        return keyPrefix;
    }

    @Override
    public ResourceBundle getResourceBundle() {
        return bundle;
    }

    private static final Map<String, ResourceBundleHelper> RESOLVERS = new ConcurrentHashMap<>();

    /**
     * Registers a new ResourceBundleResolver for the specified package prefix.
     *
     * @param keyPrefix the prefix that will be added to all the key parameter in the {@link #getString(String)}, {@link #getObject(String)} and
     *                  {@link #getString(String, BiFunction, Object...)} methods.
     * @param bundle    the bundle to register with the new ResourceBundleHelper
     *
     * @return the registered resolver
     *
     * @throws IllegalArgumentException if a resolver is already registered for the specified key prefix
     * @since 1.0.0
     */
    public static ResourceBundleHelper register(String keyPrefix, ResourceBundle bundle) {
        final String prefix = Objects.requireNonNull(keyPrefix);

        if (RESOLVERS.containsKey(prefix)) {
            throw new IllegalArgumentException("A resolver is already registered for the package prefix: " + prefix);
        }

        DefaultResourceBundleHelper helper = new DefaultResourceBundleHelper(bundle, keyPrefix);
        RESOLVERS.put(prefix, helper);
        return helper;
    }

    /**
     * Unregisters the ResourceBundleResolver associated with the specified package prefix.
     *
     * @param keyPrefix the key prefix associated with the resolver to unregister
     *
     * @since 1.0.0
     */
    public static void unRegister(String keyPrefix) {
        RESOLVERS.remove(Objects.requireNonNull(keyPrefix));
    }

    /**
     * Retrieves the ResourceBundleHelper associated with the specified package prefix.
     *
     * @param keyPrefix the key prefix associated with the resolver to retrieve
     *
     * @return the ResourceBundleHelper associated with the specified package prefix, or null if no resolver is registered for the specified package prefix
     *
     * @throws NullPointerException if the key prefix is null
     * @since 1.0.0
     */
    public static ResourceBundleHelper retrieve(String keyPrefix) {
        return RESOLVERS.get(Objects.requireNonNull(keyPrefix));
    }
}
