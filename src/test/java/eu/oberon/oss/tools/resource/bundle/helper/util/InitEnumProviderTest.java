package eu.oberon.oss.tools.resource.bundle.helper.util;

import eu.oberon.oss.tools.resource.bundle.helper.ResourceBundleHelperException;
import eu.oberon.oss.tools.resource.bundle.helper.ResourceBundleHelperRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.slf4j.spi.LoggingEventBuilder;

import java.util.ResourceBundle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class InitEnumProviderTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(InitEnumProviderTest.class);
    private static final String HELPER_KEY = "eu.oberon.oss.tools.resource.bundle.helper";

    @BeforeEach
    void setUp() {
        ResourceBundle bundle = ResourceBundle.getBundle("test-i18n");
        ResourceBundleHelperRegistry.register(HELPER_KEY, bundle, ".");
    }

    @AfterEach
    void tearDown() {
        ResourceBundleHelperRegistry.unRegister(HELPER_KEY);
    }

    @Test
    void testInit_Success() {
        InitEnumProvider.init(TestEnum.class, HELPER_KEY, LOGGER, Level.INFO);
        
        assertThat(TestEnum.TEST_VALUE_1.getMessage()).isEqualTo("Message 1");
        assertThat(TestEnum.TEST_VALUE_2.getMessage("param")).isEqualTo("Message 2 with value param");
    }

    @Test
    void testInit_NullKey() {
        assertThrows(NullPointerException.class, () -> 
            InitEnumProvider.init(TestEnum.class, null, LOGGER, Level.INFO)
        );
    }

    @Test
    void testInit_MissingHelper() {
        // The Javadoc says it throws NullPointerException if keyPrefix does not correspond to a registered helper
        assertThrows(NullPointerException.class, () -> 
            InitEnumProvider.init(TestEnum.class, "nonExistent", LOGGER, Level.INFO)
        );
    }

    @Test
    void testInit_InvalidEnum() {
        assertThrows(ResourceBundleHelperException.class, () -> 
            InitEnumProvider.init(NoInitEnum.class, HELPER_KEY, LOGGER, Level.INFO)
        );
    }

    @Test
    void testConstructorIsPrivate() throws Exception {
        java.lang.reflect.Constructor<InitEnumProvider> constructor = InitEnumProvider.class.getDeclaredConstructor();
        assertThat(java.lang.reflect.Modifier.isPrivate(constructor.getModifiers())).isTrue();
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    void testLoggerConsumer_Enabled() {
        Logger mockLogger = mock(Logger.class);
        LoggingEventBuilder mockBuilder = mock(LoggingEventBuilder.class);

        when(mockLogger.isEnabledForLevel(Level.INFO)).thenReturn(true);
        when(mockLogger.atLevel(Level.INFO)).thenReturn(mockBuilder);

        InitEnumProvider.LOGGER_CONSUMER.accept(mockLogger, Level.INFO, "Test message");

        verify(mockLogger).isEnabledForLevel(Level.INFO);
        verify(mockLogger).atLevel(Level.INFO);
        verify(mockBuilder).log("Test message");
    }

    @Test
    void testLoggerConsumer_Disabled() {
        Logger mockLogger = mock(Logger.class);

        when(mockLogger.isEnabledForLevel(Level.INFO)).thenReturn(false);

        InitEnumProvider.LOGGER_CONSUMER.accept(mockLogger, Level.INFO, "Test message");

        verify(mockLogger).isEnabledForLevel(Level.INFO);
        verify(mockLogger, never()).atLevel(any());
    }

    @Test
    void testLogFormatter() {
        // Null args
        assertThat(InitEnumProvider.LOG_FORMATTER.apply("message", null)).isEqualTo("message");

        // Empty args
        assertThat(InitEnumProvider.LOG_FORMATTER.apply("message", new Object[0])).isEqualTo("message");

        // One arg
        assertThat(InitEnumProvider.LOG_FORMATTER.apply("message {}", new Object[]{"val"})).isEqualTo("message val");

        // Multiple args
        assertThat(InitEnumProvider.LOG_FORMATTER.apply("{} and {}", new Object[]{"a", "b"})).isEqualTo("a and b");
    }

    enum NoInitEnum implements ResourceBundleUtilityEnum {
        VAL;
        @Override public String getPropertyName() { return "val"; }
        @Override public String getMessage(Object... values) { return null; }
        @Override public <T extends Exception> T getException(Class<T> exceptionClass, Object... values) { return null; }
        @Override public <T extends Exception> T getException(Class<T> exceptionClass, Throwable cause, Object... values) { return null; }
        @Override public void logMessage(Object... values) { /* Test */ }
        @Override public <L> void logMessage(L level, Object... values) { /* Test */ }
        // Missing init method
    }
}
