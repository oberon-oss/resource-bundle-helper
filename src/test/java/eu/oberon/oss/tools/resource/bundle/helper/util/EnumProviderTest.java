package eu.oberon.oss.tools.resource.bundle.helper.util;

import eu.oberon.oss.tools.resource.bundle.helper.DefaultResourceBundleHelper;
import eu.oberon.oss.tools.resource.bundle.helper.ResourceBundleHelper;
import eu.oberon.oss.tools.resource.bundle.helper.ResourceBundleHelperException;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import java.util.ResourceBundle;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings({"java:S1186","java:S1172", "java:S1144", "unused"})
class EnumProviderTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(EnumProviderTest.class);


    private final ResourceBundle resourceBundle = ResourceBundle.getBundle("test-i18n");
    private final ResourceBundleHelper helper = new DefaultResourceBundleHelper(resourceBundle, "eu.oberon.oss.tools.resource.bundle.helper");

    @Test
    void testEnumProviderIntegration() {
        LoggerConsumer<Logger, Level> loggerConsumer = (logger, level, message) -> {
            if (logger.isEnabledForLevel(level)) {
                logger.atLevel(level).log(message);
            }
        };
        BiFunction<String, Object[], String> logFormatter = (formatString, args) -> {
            if (args == null || args.length == 0) {
                return formatString;
            }
            return String.format(formatString, args);
        };

        DefaultEnumProvider<Logger, Level, TestEnum> defaultEnumProvider = DefaultEnumProvider.getInstance(
                TestEnum.class, helper, LOGGER, Level.INFO, loggerConsumer, logFormatter
        );

        assertNotNull(defaultEnumProvider);

        // Test message retrieval via Enum
        assertEquals("Message 1", TestEnum.TEST_VALUE_1.getMessage());
        assertEquals("Message 2 with value foo", TestEnum.TEST_VALUE_2.getMessage("foo"));

        // Test exception creation
        RuntimeException ex = TestEnum.TEST_VALUE_1.getException(RuntimeException.class);
        assertEquals("Message 1", ex.getMessage());

        RuntimeException ex2 = TestEnum.TEST_VALUE_2.getException(RuntimeException.class, "bar");
        assertEquals("Message 2 with value bar", ex2.getMessage());

        // Test logging (manually verified via console if needed, or we could use a mock)
        TestEnum.TEST_VALUE_1.logMessage();
        TestEnum.TEST_VALUE_2.logMessage(Level.INFO, new Object[]{"baz"});
        TestEnum.TEST_VALUE_2.logMessage(new Object[]{"baz"});
    }

    @Test
    void testGetInstance_Failure_NoInitMethod() {
        // We need an enum without init method
        assertThrows(ResourceBundleHelperException.class, () -> DefaultEnumProvider.getInstance(NoInitEnum.class, helper, LOGGER, Level.INFO, (_, _, _) -> {}, (f, _) -> f));
    }

    @Test
    void testGetInstance_Failure_WrongInitSignature() {
        assertThrows(ResourceBundleHelperException.class, () -> DefaultEnumProvider.getInstance(WrongInitEnum.class, helper, LOGGER, Level.INFO, (_, _, _) -> {}, (f, _) -> f));
    }

    @Test
    void testGetInstance_Failure_InitThrowsException() {
        ResourceBundleHelperException ex = assertThrows(ResourceBundleHelperException.class, () -> DefaultEnumProvider.getInstance(ThrowingInitEnum.class, helper, LOGGER, Level.INFO, (_, _, _) -> {}, (f, _) -> f));
        assertTrue(ex.getMessage().contains("failed while invoking init(EnumProvider)"));
        assertNotNull(ex.getCause());
        assertInstanceOf(RuntimeException.class, ex.getCause());
        assertEquals("Init failed", ex.getCause().getMessage());
    }

    @Test
    void testGetInstance_Failure_PrivateInit() {
        ResourceBundleHelperException ex = assertThrows(ResourceBundleHelperException.class, () -> DefaultEnumProvider.getInstance(PrivateInitEnum.class, helper, LOGGER, Level.INFO, (_, _, _) -> {}, (f, _) -> f));
        assertTrue(ex.getMessage().contains("no static method 'init(EnumProvider)' was found"));
        assertInstanceOf(NoSuchMethodException.class, ex.getCause());
        assertTrue(ex.getCause().getMessage().contains("Incorrect signature"));
        assertTrue(ex.getCause().getMessage().contains("private"));
    }

    @Test
    void testGetInstance_Failure_NonVoidInit() {
        ResourceBundleHelperException ex = assertThrows(ResourceBundleHelperException.class, () -> DefaultEnumProvider.getInstance(NonVoidInitEnum.class, helper, LOGGER, Level.INFO, (_, _, _) -> {}, (f, _) -> f));
        assertTrue(ex.getMessage().contains("no static method 'init(EnumProvider)' was found"));
        assertInstanceOf(NoSuchMethodException.class, ex.getCause());
        assertTrue(ex.getCause().getMessage().contains("Incorrect signature"));
        assertTrue(ex.getCause().getMessage().contains("int init"));
    }

    enum NoInitEnum implements ResourceBundleUtilityEnum {
        VAL;
        @Override public String getPropertyName() { return "val"; }
        @Override public String getMessage(Object... values) { return null; }
        @Override public <T extends Exception> T getException(Class<T> exceptionClass, Object... values) { return null; }
        @Override public <T extends Exception> T getException(Class<T> exceptionClass, Throwable cause, Object... values) { return null; }
        @Override public void logMessage(Object... values) {}
        @Override public <L> void logMessage(L level, Object... values) {}
    }

    enum WrongInitEnum implements ResourceBundleUtilityEnum {
        VAL;
        @Override public String getPropertyName() { return "val"; }
        @Override public String getMessage(Object... values) { return null; }
        @Override public <T extends Exception> T getException(Class<T> exceptionClass, Object... values) { return null; }
        @Override public <T extends Exception> T getException(Class<T> exceptionClass, Throwable cause, Object... values) { return null; }
        @Override public void logMessage(Object... values) {}
        @Override public <L> void logMessage(L level, Object... values) {}

        // Not static
        public void init(EnumProvider<?, WrongInitEnum> ignoredProvider) {}
    }

    enum ThrowingInitEnum implements ResourceBundleUtilityEnum {
        VAL;
        @Override public String getPropertyName() { return "val"; }
        @Override public String getMessage(Object... values) { return null; }
        @Override public <T extends Exception> T getException(Class<T> exceptionClass, Object... values) { return null; }
        @Override public <T extends Exception> T getException(Class<T> exceptionClass, Throwable cause, Object... values) { return null; }
        @Override public void logMessage(Object... values) {}
        @Override public <L> void logMessage(L level, Object... values) {}

        public static <L> void init(EnumProvider<L, ThrowingInitEnum> ignoredProvider) {
            throw new RuntimeException("Init failed");
        }
    }

    enum PrivateInitEnum implements ResourceBundleUtilityEnum {
        VAL;
        @Override public String getPropertyName() { return "val"; }
        @Override public String getMessage(Object... values) { return null; }
        @Override public <T extends Exception> T getException(Class<T> exceptionClass, Object... values) { return null; }
        @Override public <T extends Exception> T getException(Class<T> exceptionClass, Throwable cause, Object... values) { return null; }
        @Override public void logMessage(Object... values) {}
        @Override public <L> void logMessage(L level, Object... values) {}

        private static <L> void init(EnumProvider<L, PrivateInitEnum> ignoredProvider) {}
    }

    enum NonVoidInitEnum implements ResourceBundleUtilityEnum {
        VAL;
        @Override public String getPropertyName() { return "val"; }
        @Override public String getMessage(Object... values) { return null; }
        @Override public <T extends Exception> T getException(Class<T> exceptionClass, Object... values) { return null; }
        @Override public <T extends Exception> T getException(Class<T> exceptionClass, Throwable cause, Object... values) { return null; }
        @Override public void logMessage(Object... values) {}
        @Override public <L> void logMessage(L level, Object... values) {}

        public static <L> int init(EnumProvider<L, NonVoidInitEnum> ignoredProvider) { return 0; }
    }
}
