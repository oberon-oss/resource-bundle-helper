package eu.oberon.oss.tools.resource.bundle.helper;

import org.jetbrains.annotations.NotNull;

import java.util.ResourceBundle;

/**
 * Interface defining the contract for a provider that supplies resource bundle details and configurations.
 * <p>
 * This interface is used by the {@link ResourceBundleHelperRegistry} to detect implementations of resource bundle helpers, by the ServiceLoader mechanism.
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public interface ResourceBundleHelperProvider {
    /**
     * Retrieves the resource bundle associated with the implementation.
     *
     * @return the resource bundle for localized string retrieval and configuration.
     *
     * @since 1.0.0
     */
    @NotNull ResourceBundle getResourceBundle();

    /**
     * Retrieves the key prefix associated with the implementation.
     *
     * @return the key prefix for localized string retrieval and configuration.
     *
     * @since 1.0.0
     */
    @NotNull String getKeyPrefix();

    /**
     * Retrieves the delimiter associated with the implementation.
     * <p>
     * The default implementation provided here will return a dot (".")
     *
     * @return the delimiter for localized string retrieval and configuration.
     *
     * @since 1.0.0
     */
    default @NotNull String getDelimiter() {
        return ".";
    }

}
