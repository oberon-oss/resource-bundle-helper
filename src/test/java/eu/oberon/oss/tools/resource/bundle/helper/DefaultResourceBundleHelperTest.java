package eu.oberon.oss.tools.resource.bundle.helper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ResourceBundle;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultResourceBundleHelperTest {

    @Mock
    private ResourceBundle resourceBundle;

    private static final String KEY_PREFIX = "test.prefix";
    private static final String RELATIVE_KEY = "message";
    private static final String FULL_KEY = "test.prefix.message";
    private static final String VALUE = "Hello World";

    @BeforeEach
    void setUp() {
        // Ensure clean state for registry tests
        DefaultResourceBundleHelper.unRegister(KEY_PREFIX);
    }

    @AfterEach
    void tearDown() {
        DefaultResourceBundleHelper.unRegister(KEY_PREFIX);
    }

    @Test
    void constructor_WithDefaultDelimiter_ShouldInitializeCorrectly() {
        DefaultResourceBundleHelper helper = new DefaultResourceBundleHelper(resourceBundle, KEY_PREFIX);

        assertEquals(KEY_PREFIX, helper.getKeyPrefix());
        assertEquals(resourceBundle, helper.getResourceBundle());
    }

    @Test
    void constructor_WithCustomDelimiter_ShouldInitializeCorrectly() {
        String customDelimiter = "_";
        DefaultResourceBundleHelper helper = new DefaultResourceBundleHelper(resourceBundle, KEY_PREFIX, customDelimiter);

        assertEquals(KEY_PREFIX, helper.getKeyPrefix());
        assertEquals(resourceBundle, helper.getResourceBundle());
        
        when(resourceBundle.getString(KEY_PREFIX + customDelimiter + RELATIVE_KEY)).thenReturn(VALUE);
        assertEquals(VALUE, helper.getString(RELATIVE_KEY));
    }

    @Test
    void constructor_WithNullBundle_ShouldThrowException() {
        assertThrows(NullPointerException.class, () -> new DefaultResourceBundleHelper(null, KEY_PREFIX));
    }

    @Test
    void constructor_WithNullKeyPrefix_ShouldThrowException() {
        assertThrows(NullPointerException.class, () -> new DefaultResourceBundleHelper(resourceBundle, null));
    }

    @Test
    void constructor_WithNullDelimiter_ShouldThrowException() {
        assertThrows(NullPointerException.class, () -> new DefaultResourceBundleHelper(resourceBundle, KEY_PREFIX, null));
    }

    @Test
    void constructor_WithEmptyDelimiter_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new DefaultResourceBundleHelper(resourceBundle, KEY_PREFIX, ""));
    }

    @Test
    void constructor_WithWhitespaceDelimiter_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new DefaultResourceBundleHelper(resourceBundle, KEY_PREFIX, "  "));
    }

    @Test
    void constructor_WithIllegalDelimiter_ShouldThrowException() {
        for (String illegal : ResourceBundleHelper.ILLEGAL_DELIMITER_LIST) {
            assertThrows(IllegalArgumentException.class, () -> new DefaultResourceBundleHelper(resourceBundle, KEY_PREFIX, illegal),
                    "Should throw for delimiter: '" + illegal + "'");
        }
    }

    @Test
    void getString_ShouldReturnCorrectValue() {
        DefaultResourceBundleHelper helper = new DefaultResourceBundleHelper(resourceBundle, KEY_PREFIX);
        when(resourceBundle.getString(FULL_KEY)).thenReturn(VALUE);

        String result = helper.getString(RELATIVE_KEY);

        assertEquals(VALUE, result);
        verify(resourceBundle).getString(FULL_KEY);
    }

    @Test
    void getString_WithFormatter_ShouldReturnFormattedValue() {
        DefaultResourceBundleHelper helper = new DefaultResourceBundleHelper(resourceBundle, KEY_PREFIX);
        Object[] args = {"arg1", 123};
        BiFunction<String, Object[], String> formatter = (key, values) -> {
            assertEquals(FULL_KEY, key);
            assertArrayEquals(args, values);
            return "Formatted Value";
        };

        String result = helper.getString(RELATIVE_KEY, formatter, args);

        assertEquals("Formatted Value", result);
    }

    @Test
    void getObject_ShouldReturnCorrectObject() {
        DefaultResourceBundleHelper helper = new DefaultResourceBundleHelper(resourceBundle, KEY_PREFIX);
        Object expectedObject = new Object();
        when(resourceBundle.getObject(FULL_KEY)).thenReturn(expectedObject);

        Object result = helper.getObject(RELATIVE_KEY);

        assertEquals(expectedObject, result);
        verify(resourceBundle).getObject(FULL_KEY);
    }

    @Test
    void register_ShouldAddHelperToRegistry() {
        ResourceBundleHelper helper = DefaultResourceBundleHelper.register(KEY_PREFIX, resourceBundle);

        // This test is expected to fail currently due to the bug in DefaultResourceBundleHelper.register
        assertNotNull(helper, "Register should return the new helper instance");
        assertEquals(KEY_PREFIX, helper.getKeyPrefix());
        
        ResourceBundleHelper retrieved = DefaultResourceBundleHelper.retrieve(KEY_PREFIX);
        assertSame(helper, retrieved);
    }

    @Test
    void register_WithExistingPrefix_ShouldThrowException() {
        DefaultResourceBundleHelper.register(KEY_PREFIX, resourceBundle);
        
        assertThrows(IllegalArgumentException.class, () -> DefaultResourceBundleHelper.register(KEY_PREFIX, resourceBundle));
    }

    @Test
    void unRegister_ShouldRemoveHelperFromRegistry() {
        DefaultResourceBundleHelper.register(KEY_PREFIX, resourceBundle);
        assertNotNull(DefaultResourceBundleHelper.retrieve(KEY_PREFIX));

        DefaultResourceBundleHelper.unRegister(KEY_PREFIX);

        assertNull(DefaultResourceBundleHelper.retrieve(KEY_PREFIX));
    }
}
