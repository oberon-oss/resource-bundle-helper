package eu.oberon.oss.tools.resource.bundle.helper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class ResourceBundleHelperRegistryTest {

    @BeforeEach
    void setUp() throws Exception {
        resetRegistry();
    }

    @AfterEach
    void tearDown() throws Exception {
        resetRegistry();
    }

    private void resetRegistry() throws Exception {
        Field resolversField = ResourceBundleHelperRegistry.class.getDeclaredField("RESOLVERS");
        resolversField.setAccessible(true);
        Map<?, ?> resolvers = (Map<?, ?>) resolversField.get(null);
        resolvers.clear();
    }

    @Test
    void shouldLoadProvidersUsingServiceLoader() {
        ResourceBundleHelperRegistry.loadProviders();

        ResourceBundleHelper helper = ResourceBundleHelperRegistry.retrieve("test");

        assertNotNull(helper);
        assertEquals("Hello from ServiceLoader", helper.getString("message"));
    }

    @Test
    void loadProviders_MultipleCalls_ShouldBeSafe() {
        ResourceBundleHelperRegistry.loadProviders();

        // Second call should not throw or cause duplicate registration errors
        ResourceBundleHelperRegistry.loadProviders();
        
        assertNotNull(ResourceBundleHelperRegistry.retrieve("test"));
    }

    @Test
    void loadProviders_WhenClassLoaderIsNull_ShouldStillLoadProviders()  {
        ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(null);
            ResourceBundleHelperRegistry.loadProviders();
            
            ResourceBundleHelper helper = ResourceBundleHelperRegistry.retrieve("test");
            assertNotNull(helper, "Should load providers even if context class loader is null");
        } finally {
            Thread.currentThread().setContextClassLoader(originalClassLoader);
        }
    }

    @Test
    void unRegister_ShouldRemoveHelper() {
        ResourceBundleHelperRegistry.loadProviders();
        assertNotNull(ResourceBundleHelperRegistry.retrieve("test"));
        
        ResourceBundleHelperRegistry.unRegister("test");
        assertNull(ResourceBundleHelperRegistry.retrieve("test"));
    }

    @Test
    void register_ShouldAddHelper() {
        ResourceBundle bundle = mock(ResourceBundle.class);
        ResourceBundleHelper helper = ResourceBundleHelperRegistry.register("manual", bundle, ".");
        
        assertNotNull(helper);
        assertSame(helper, ResourceBundleHelperRegistry.retrieve("manual"));
    }

    @Test
    void register_WhenAlreadyRegistered_ShouldThrowException() {
        ResourceBundle bundle = mock(ResourceBundle.class);
        ResourceBundleHelperRegistry.register("duplicate", bundle, ".");
        
        assertThrows(IllegalArgumentException.class, () -> 
            ResourceBundleHelperRegistry.register("duplicate", bundle, ".")
        );
    }
}