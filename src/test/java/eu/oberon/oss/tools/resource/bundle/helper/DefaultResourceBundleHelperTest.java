package eu.oberon.oss.tools.resource.bundle.helper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ResourceBundle;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultResourceBundleHelperTest {

    @Mock
    private ResourceBundle resourceBundle;

    private static final String KEY_PREFIX = "test.prefix";
    private static final String RELATIVE_KEY = "message";
    private static final String FULL_KEY = "test.prefix.message";
    private static final String VALUE = "Hello World";

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
        when(resourceBundle.getString(FULL_KEY)).thenReturn(VALUE);
        
        Object[] args = {"arg1", 123};
        BiFunction<String, Object[], String> formatter = (message, values) -> {
            assertEquals(VALUE, message);
            assertArrayEquals(args, values);
            return "Formatted Value";
        };

        String result = helper.getString(RELATIVE_KEY, formatter, args);

        assertEquals("Formatted Value", result);
        verify(resourceBundle).getString(FULL_KEY);
    }

    @Test
    void getString_WithVarargs_ShouldReturnFormattedValue() {
        DefaultResourceBundleHelper helper = new DefaultResourceBundleHelper(resourceBundle, KEY_PREFIX);
        String formatValue = "Hello %s!";
        when(resourceBundle.getString(FULL_KEY)).thenReturn(formatValue);

        String result = helper.getString(RELATIVE_KEY, "World");

        assertEquals("Hello World!", result);
        verify(resourceBundle).getString(FULL_KEY);
    }

    @Test
    void getString_WithVarargs_EmptyValues_ShouldReturnUnformattedValue() {
        DefaultResourceBundleHelper helper = new DefaultResourceBundleHelper(resourceBundle, KEY_PREFIX);
        when(resourceBundle.getString(FULL_KEY)).thenReturn(VALUE);

        String result = helper.getString(RELATIVE_KEY, new Object[0]);

        assertEquals(VALUE, result);
        verify(resourceBundle).getString(FULL_KEY);
    }

    @Test
    void getString_WithVarargs_NullValues_ShouldReturnUnformattedValue() {
        DefaultResourceBundleHelper helper = new DefaultResourceBundleHelper(resourceBundle, KEY_PREFIX);
        when(resourceBundle.getString(FULL_KEY)).thenReturn(VALUE);

        String result = helper.getString(RELATIVE_KEY, (Object[]) null);

        assertEquals(VALUE, result);
        verify(resourceBundle).getString(FULL_KEY);
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
    void createException_ShouldReturnExceptionWithMessage() {
        DefaultResourceBundleHelper helper = new DefaultResourceBundleHelper(resourceBundle, KEY_PREFIX);
        when(resourceBundle.getString(FULL_KEY)).thenReturn(VALUE);

        IllegalArgumentException result = helper.createException(IllegalArgumentException.class, RELATIVE_KEY);

        assertNotNull(result);
        assertEquals(VALUE, result.getMessage());
    }

    @Test
    void createException_WithFormatting_ShouldReturnFormattedException() {
        DefaultResourceBundleHelper helper = new DefaultResourceBundleHelper(resourceBundle, KEY_PREFIX);
        when(resourceBundle.getString(FULL_KEY)).thenReturn("Error: %s");

        IllegalArgumentException result = helper.createException(IllegalArgumentException.class, RELATIVE_KEY, "Something went wrong");

        assertNotNull(result);
        assertEquals("Error: Something went wrong", result.getMessage());
    }

    @Test
    void createException_WithCause_ShouldReturnExceptionWithMessageAndCause() {
        DefaultResourceBundleHelper helper = new DefaultResourceBundleHelper(resourceBundle, KEY_PREFIX);
        when(resourceBundle.getString(FULL_KEY)).thenReturn(VALUE);
        Exception cause = new RuntimeException("Original cause");

        IllegalArgumentException result = helper.createException(IllegalArgumentException.class, cause, RELATIVE_KEY);

        assertNotNull(result);
        assertEquals(VALUE, result.getMessage());
        assertSame(cause, result.getCause());
    }

    @Test
    void createException_WhenConstructorNotFound_ShouldThrowResourceBundleHelperException() {
        DefaultResourceBundleHelper helper = new DefaultResourceBundleHelper(resourceBundle, KEY_PREFIX);

        // Test with a class that doesn't have (String) constructor
        assertThrows(ResourceBundleHelperException.class, () ->
                helper.createException(NoStringConstructorException.class, RELATIVE_KEY)
        );
    }

    @Test
    void createException_WithCause_WhenConstructorNotFound_ShouldThrowResourceBundleHelperException() {
        DefaultResourceBundleHelper helper = new DefaultResourceBundleHelper(resourceBundle, KEY_PREFIX);
        Exception cause = new RuntimeException("Original cause");

        // Test with a class that doesn't have (String, Throwable) constructor
        assertThrows(ResourceBundleHelperException.class, () ->
                helper.createException(NoStringConstructorException.class, cause, RELATIVE_KEY)
        );
    }

    public static class NoStringConstructorException extends Exception {
        public NoStringConstructorException() {
            super();
        }
    }
}
