package eu.oberon.oss.tools.resource.bundle.helper;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.BiFunction;


/**
 * A utility class for simplifying access to a {@link ResourceBundle} with a specific key prefix. This class helps in organizing and retrieving localized
 * strings or objects from resource bundles using a predefined prefix.
 * <p>
 * The prefix specified at construction time will be used whenever a call is made to retrieve data from the {@link ResourceBundle} that was also specified at
 * construction time.
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
        return formatter.apply(getString(relativeKey), values);
    }

    private static final BiFunction<String, Object[], String> STRING_FORMATTER = (stringFormat, values) -> {
        if (values == null || values.length == 0) {
            return stringFormat;
        }
        return String.format(stringFormat, values);
    };

    @Override
    public @NotNull String getString(String relativeKey, Object... values) {
        return STRING_FORMATTER.apply(getString(relativeKey), values);
    }

    @Override
    public <E extends Exception> @NotNull E createException(Class<E> exceptionClass, String relativeKey, Object... values) {
        try {
            return exceptionClass.getConstructor(String.class).newInstance(getString(relativeKey, values));
        } catch (Exception e) {
            throw new ResourceBundleHelperException("Failed to create exception", e);
        }
    }

    @Override
    public <E extends Exception> @NotNull E createException(Class<E> exceptionClass, Throwable cause, String relativeKey, Object... values) {
        try {
            return exceptionClass.getConstructor(String.class, Throwable.class).newInstance(getString(relativeKey, values), cause);
        } catch (Exception e) {
            throw new ResourceBundleHelperException("Failed to create exception", e);
        }
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

}
