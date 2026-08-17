package eu.oberon.oss.tools.resource.bundle.helper.util;

import eu.oberon.oss.tools.resource.bundle.helper.DefaultResourceBundleHelper;
import eu.oberon.oss.tools.resource.bundle.helper.ResourceBundleHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ResourceBundle;
import java.util.function.BiFunction;

import static org.assertj.core.api.Assertions.assertThat;

class InterfaceMethodsTest {

    private EnumProvider<String, TestEnum> provider;
    private StringBuilder logBuffer;

    @BeforeEach
    void setUp() {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("test-i18n");
        ResourceBundleHelper helper = new DefaultResourceBundleHelper(resourceBundle, "eu.oberon.oss.tools.resource.bundle.helper");
        
        logBuffer = new StringBuilder();
        LoggerConsumer<StringBuilder, String> loggerConsumer = (log, level, message) -> log.append("[").append(level).append("] ").append(message);
        
        BiFunction<String, Object[], String> logFormatter = (formatString, args) -> {
            if (args == null || args.length == 0) return formatString;
            return String.format(formatString, args);
        };

        provider = DefaultEnumProvider.getInstance(
                TestEnum.class, helper, logBuffer, "INFO", loggerConsumer, logFormatter
        );
    }

    @Test
    void testResourceBundleUtilityEnumMethods() {
        TestEnum constant = TestEnum.TEST_VALUE_1;

        // getPropertyName()
        assertThat(constant.getPropertyName()).isEqualTo("test.value.1");

        // getMessage(Object... values)
        assertThat(constant.getMessage()).isEqualTo("Message 1");
        assertThat(TestEnum.TEST_VALUE_2.getMessage("param")).isEqualTo("Message 2 with value param");

        // getException(Class<T> exceptionClass, Object... values)
        RuntimeException ex = constant.getException(RuntimeException.class);
        assertThat(ex).isExactlyInstanceOf(RuntimeException.class).hasMessage("Message 1");

        // getException(Class<T> exceptionClass, Throwable cause, Object... values)
        Throwable cause = new RuntimeException("cause");
        ex = constant.getException(RuntimeException.class, cause);
        assertThat(ex).isExactlyInstanceOf(RuntimeException.class).hasMessage("Message 1").hasCause(cause);

        // logMessage(Object... values)
        constant.logMessage();
        assertThat(logBuffer.toString()).contains("[INFO] Message 1");
        logBuffer.setLength(0);

        // logMessage(L level, Object... values)
        constant.logMessage("DEBUG", new Object[0]);
        assertThat(logBuffer.toString()).contains("[DEBUG] Message 1");
    }

    @Test
    void testEnumProviderMethods() {
        TestEnum constant = TestEnum.TEST_VALUE_2;

        // getMessage(E enumConstant, Object... values)
        assertThat(provider.getMessage(constant, "val")).isEqualTo("Message 2 with value val");

        // logMessage(E enumConstant, Object... values)
        provider.logMessage(constant, new Object[]{"val"});
        assertThat(logBuffer.toString()).contains("[INFO] Message 2 with value val");
        logBuffer.setLength(0);

        // logMessage(E enumConstant, L level, Object... values)
        provider.logMessage(constant, "WARN", "val");
        assertThat(logBuffer.toString()).contains("[WARN] Message 2 with value val");
        logBuffer.setLength(0);

        // getException(E enumConstant, Class<T> exceptionClass, Object... values)
        RuntimeException ex = provider.getException(constant, RuntimeException.class, "val");
        assertThat(ex).hasMessage("Message 2 with value val");

        // getException(E enumConstant, Class<T> exceptionClass, Throwable cause, Object... values)
        Throwable cause = new IllegalArgumentException("wrong");
        ex = provider.getException(constant, RuntimeException.class, cause, "val");
        assertThat(ex).hasMessage("Message 2 with value val").hasCause(cause);
    }
}
