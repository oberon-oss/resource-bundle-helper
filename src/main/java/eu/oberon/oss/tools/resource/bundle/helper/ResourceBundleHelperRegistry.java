package eu.oberon.oss.tools.resource.bundle.helper;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

/**
 * Central registry for {@link ResourceBundleHelper} instances.
 * <p>
 * Helpers are stored by their key prefix and can be retrieved through {@link #retrieve(String)}. A helper may be registered explicitly by calling
 * {@link #register(String, ResourceBundle, String)}, or automatically by providing one or more {@link ResourceBundleHelperProvider} implementations through
 * Java's {@link ServiceLoader} mechanism.
 * <p>
 * When this class is initialized, it calls {@link #loadProviders()}. This method uses the current thread context class loader, when available, to discover all
 * visible {@link ResourceBundleHelperProvider} services. This makes it possible for applications that depend on this library to contribute providers without
 * this library knowing their implementation classes at compile time.
 * <p>
 * To make a provider discoverable on the classpath, the consuming application or provider JAR should include a file named:
 * <pre>{@code
 * META-INF/services/eu.oberon.oss.tools.resource.bundle.helper.ResourceBundleHelperProvider
 * }</pre>
 * The file must contain the fully qualified class name of each provider implementation, one per line, for example:
 * <pre>{@code
 * com.example.i18n.MessagesResourceBundleHelperProvider
 * com.example.i18n.ErrorsResourceBundleHelperProvider
 * }</pre>
 * Each listed provider class must be public, must implement {@link ResourceBundleHelperProvider}, and must be instantiable by {@link ServiceLoader}.
 * <p>
 * In a named Java module, a provider can instead be declared with a {@code provides} directive:
 * <pre>{@code
 * provides eu.oberon.oss.tools.resource.bundle.helper.ResourceBundleHelperProvider
 *     with com.example.i18n.MessagesResourceBundleHelperProvider;
 * }</pre>
 * A module containing this registry should declare a matching {@code uses} directive if it has a {@code module-info.java}:
 * <pre>{@code
 * uses eu.oberon.oss.tools.resource.bundle.helper.ResourceBundleHelperProvider;
 * }</pre>
 * <p>
 * Provider-loaded helpers are registered using the provider's resource bundle, key-prefix, and delimiter. If multiple providers declare the same key prefix,
 * registration fails with an {@link IllegalArgumentException}. If a provider cannot be loaded or instantiated, {@link ServiceLoader} may throw a
 * {@link ServiceConfigurationError}.
 * <p>
 * This class is not instantiable.
 *
 * @author TigerLilly64
 * @see ResourceBundleHelper
 * @see ResourceBundleHelperProvider
 * @see ServiceLoader
 * @since 1.0.0
 */
public final class ResourceBundleHelperRegistry {

    private static final Map<String, ResourceBundleHelper> RESOLVERS = new ConcurrentHashMap<>();
    private static final Object PROVIDER_LOAD_LOCK = new Object();

    static {
        loadProviders();
    }

    private ResourceBundleHelperRegistry() {
    }

    /**
     * Loads all {@link ResourceBundleHelperProvider} implementations available through {@link ServiceLoader} and registers them in this registry.
     *
     * @throws ServiceConfigurationError if a provider cannot be loaded or instantiated
     * @since 1.0.0
     */
    public static void loadProviders() {
        synchronized (PROVIDER_LOAD_LOCK) {
            final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

            final ServiceLoader<ResourceBundleHelperProvider> providers = classLoader == null
                    ? ServiceLoader.load(ResourceBundleHelperProvider.class)
                    : ServiceLoader.load(ResourceBundleHelperProvider.class, classLoader);

            for (ResourceBundleHelperProvider provider : providers) {
                if (retrieve(provider.getKeyPrefix()) == null) {
                    register(
                            provider.getKeyPrefix(),
                            provider.getResourceBundle(),
                            provider.getDelimiter()
                    );
                }
            }
        }
    }

    /**
     * Registers a new ResourceBundleResolver for the specified package prefix.
     *
     * @param keyPrefix the prefix that will be added to all the key parameter in the {@link ResourceBundleHelper#getString(String)},
     *                  {@link ResourceBundleHelper#getObject(String)} and {@link ResourceBundleHelper#getString(String, BiFunction, Object...)} methods.
     * @param bundle    the bundle to register with the new ResourceBundleHelper
     * @param delimiter the delimiter used when creating to full lookup key from the keyPrefix and the relative key when looking up values from the resource
     *                  bundle
     *
     * @return the registered resolver
     *
     * @throws IllegalArgumentException if a resolver is already registered for the specified key prefix
     * @since 1.0.0
     */
    public static ResourceBundleHelper register(String keyPrefix, ResourceBundle bundle, String delimiter) {
        final String prefix = Objects.requireNonNull(keyPrefix);
        final ResourceBundleHelper helper = new DefaultResourceBundleHelper(bundle, keyPrefix, delimiter);

        final ResourceBundleHelper previous = RESOLVERS.putIfAbsent(prefix, helper);

        if (previous != null) {
            throw new IllegalArgumentException("A resolver is already registered for the package prefix: " + prefix);
        }

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
