package eu.oberon.oss.tools.resource.bundle.helper.util;

import eu.oberon.oss.tools.resource.bundle.helper.DefaultResourceBundleHelper;
import eu.oberon.oss.tools.resource.bundle.helper.ResourceBundleHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ResourceBundle;
import java.util.function.BiFunction;

import static org.assertj.core.api.Assertions.assertThat;

class InterfaceMethodsTest {

    private EnumProvider<String, InterfaceTestEnum> provider;
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

        DefaultEnumProvider.initForEnum(InterfaceTestEnum.class, helper, logBuffer, "INFO", loggerConsumer, logFormatter);
        provider = InterfaceTestEnum.provider;
    }

    @Test
    void testResourceBundleUtilityEnumMethods() {
        InterfaceTestEnum enumConstant = InterfaceTestEnum.TEST_VALUE_1;

        // getPropertyName()
        assertThat(enumConstant.getPropertyName()).isEqualTo("test.value.1");

        // getMessage(Object... values)
        assertThat(enumConstant.getMessage()).isEqualTo("Message 1");
        assertThat(InterfaceTestEnum.TEST_VALUE_2.getMessage("param")).isEqualTo("Message 2 with value param");

        // getException(Class<T> exceptionClass, Object... values)
        RuntimeException ex = enumConstant.getException(RuntimeException.class);
        assertThat(ex).isExactlyInstanceOf(RuntimeException.class).hasMessage("Message 1");

        // getException(Class<T> exceptionClass, Throwable cause, Object... values)
        Throwable cause = new RuntimeException("cause");
        ex = enumConstant.getException(RuntimeException.class, cause);
        assertThat(ex).isExactlyInstanceOf(RuntimeException.class).hasMessage("Message 1").hasCause(cause);

        // logMessage(Object... values)
        enumConstant.logMessage();
        assertThat(logBuffer.toString()).contains("[INFO] Message 1");
        logBuffer.setLength(0);

        // logMessage(L level, Object... values)
        enumConstant.logMessage("DEBUG", new Object[0]);
        assertThat(logBuffer.toString()).contains("[DEBUG] Message 1");
    }

    @Test
    void testEnumProviderMethods() {
        InterfaceTestEnum enumConstant = InterfaceTestEnum.TEST_VALUE_2;

        // getMessage(E enumConstant, Object... values)
        assertThat(provider.getMessage(enumConstant, "val")).isEqualTo("Message 2 with value val");

        // logMessage(E enumConstant, Object... values)
        provider.logMessage(enumConstant, new Object[]{"val"});
        assertThat(logBuffer.toString()).contains("[INFO] Message 2 with value val");
        logBuffer.setLength(0);

        // logMessage(E enumConstant, L level, Object... values)
        provider.logMessage(enumConstant, "WARN", "val");
        assertThat(logBuffer.toString()).contains("[WARN] Message 2 with value val");
        logBuffer.setLength(0);

        // getException(E enumConstant, Class<T> exceptionClass, Object... values)
        RuntimeException ex = provider.getException(enumConstant, RuntimeException.class, "val");
        assertThat(ex).hasMessage("Message 2 with value val");

        // getException(E enumConstant, Class<T> exceptionClass, Throwable cause, Object... values)
        Throwable cause = new IllegalArgumentException("wrong");
        ex = provider.getException(enumConstant, RuntimeException.class, cause, "val");
        assertThat(ex).hasMessage("Message 2 with value val").hasCause(cause);
    }

    private enum InterfaceTestEnum implements ResourceBundleUtilityEnum {
        TEST_VALUE_1,
        TEST_VALUE_2("custom_relative_key"),
        ;

        private final String propertyName;

        InterfaceTestEnum() {
            this.propertyName = name().toLowerCase().replace("_", ".");
        }

        InterfaceTestEnum(String propertyName) {
            this.propertyName = propertyName;
        }

        @Override
        public String getPropertyName() {
            return propertyName;
        }

        @Override
        public String getMessage(Object... values) {
            return provider.getMessage(this, values);
        }

        @Override
        public <T extends Exception> T getException(Class<T> exceptionClass, Object... values) {
            return provider.getException(this, exceptionClass, values);
        }

        @Override
        public <T extends Exception> T getException(Class<T> exceptionClass, Throwable cause, Object... values) {
            return provider.getException(this, exceptionClass, cause, values);
        }

        @Override
        public void logMessage(Object... values) {
            provider.logMessage(this, values);
        }

        @Override
        public <L> void logMessage(L level, Object... values) {
            //noinspection unchecked
            ((EnumProvider<L, InterfaceTestEnum>) provider).logMessage(this, level, values);
        }

        private static EnumProvider<String, InterfaceTestEnum> provider;

        @SuppressWarnings("unused")
        public static void init(EnumProvider<String, InterfaceTestEnum> enumProvider) {
            provider = enumProvider;
        }
    }
}
