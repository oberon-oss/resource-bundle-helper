package eu.oberon.oss.tools.resource.bundle.helper.util;

import eu.oberon.oss.tools.resource.bundle.helper.ResourceBundleHelper;
import eu.oberon.oss.tools.resource.bundle.helper.ResourceBundleHelperException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.function.BiFunction;

import static eu.oberon.oss.tools.resource.bundle.helper.util.MethodTool.getMethodSignature;

/**
 * Implementation of the {@link EnumProvider} interface that provides utility methods for managing enum constants tied to resource bundles.
 * <p>
 * This class facilitates message retrieval, logging, and exception creation based on enum constants.
 *
 * @param <W> the type of the logger
 * @param <L> the type of the log level
 * @param <E> the type of the Enum, which extends {@link ResourceBundleUtilityEnum}
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public final class DefaultEnumProvider<W, L, E extends Enum<E> & ResourceBundleUtilityEnum> implements EnumProvider<L, E> {
    private final ResourceBundleHelper helper;

    private final BiFunction<String, Object[], String> messageFormatter;
    private final LoggerConsumer<W, L> logConsumer;
    private final W logger;
    private final L currentLogLevel;

    /**
     * Creates and initializes an instance of DefaultEnumProvider for the given Enum class.
     * <p>
     * The enum class identified by the enumClass parameter, in addition to implementing {@link ResourceBundleUtilityEnum}, needs to define the following method
     * {@code public static <L> void init(EnumProvider<L, TestEnum> provider)}.
     * <p>
     * After initialization, this method will call the init() method to inject the EnumProvider instance into the enum class. The enum, after the init() method was
     * called, has access to the methods provided by the EnumProvider instance to retrieve localized messages and log messages.
     *
     * @param <W>             the type of the logger
     * @param <L>             the type of the log level
     * @param <E>             the type of the Enum, which extends {@link ResourceBundleUtilityEnum}
     * @param enumClass       the class object of the Enum that needs to be associated with this provider
     * @param helper          the ResourceBundleHelper instance for resource bundle operations
     * @param logger          the logger instance that will be used for logging
     * @param dftLevel        the default log level to use when no specific level is provided
     * @param logConsumer     the LoggerConsumer instance to handle log message consumption
     * @param logMsgFormatter a BiFunction to format log messages with placeholders and values
     *
     * @return an instance of DefaultEnumProvider associated with the provided Enum class
     *
     * @throws ResourceBundleHelperException if the Enum class does not declare a public static void init(EnumProvider) method, or if an error occurs during the
     *                                       reflective invocation of the init method
     * @since 1.0.0
     */
    public static <W, L, E extends Enum<E> & ResourceBundleUtilityEnum> DefaultEnumProvider<W, L, E> getInstance(Class<E> enumClass, ResourceBundleHelper helper, W logger, L dftLevel, LoggerConsumer<W, L> logConsumer, BiFunction<String, Object[], String> logMsgFormatter) {

        DefaultEnumProvider<W, L, E> defaultEnumProvider = new DefaultEnumProvider<>(helper, logger, dftLevel, logConsumer, logMsgFormatter);

        try {
            getMethod(enumClass).invoke(null, defaultEnumProvider);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new ResourceBundleHelperException("Enum class " + EnumProvider.class.getName() + " no static method 'init(EnumProvider)' was found", e);
        } catch (InvocationTargetException e) {
            throw new ResourceBundleHelperException("Enum class " + EnumProvider.class.getName() + " failed while invoking init(EnumProvider)", e.getCause());
        }
        return defaultEnumProvider;
    }

    private static <E extends Enum<E> & ResourceBundleUtilityEnum> Method getMethod(Class<E> enumClass) throws NoSuchMethodException {
        Method method = enumClass.getDeclaredMethod("init", EnumProvider.class);
        int modifiers = method.getModifiers();

        if (!Modifier.isStatic(modifiers) || !Modifier.isPublic(modifiers) || method.getReturnType() != void.class) {
            throw new NoSuchMethodException(
                    "Incorrect signature: " + getMethodSignature(method)
                            + "; expected: " + getExpectedInitMethodSignature(enumClass)
            );
        }
        return method;
    }

    private static <E extends Enum<E> & ResourceBundleUtilityEnum> String getExpectedInitMethodSignature(Class<E> enumClass) {
        return "public static <L> void init("
                + EnumProvider.class.getSimpleName()
                + "<L, "
                + enumClass.getSimpleName()
                + ">)";
    }

    /**
     * Constructor for DefaultEnumProvider.
     *
     * @param helper          the ResourceBundleHelper instance
     * @param logger          the logger instance
     * @param dftLevel        the default log level
     * @param consumer        the LoggerConsumer instance
     * @param logMsgFormatter the BiFunction for formatting log messages
     *
     * @since 1.0.0
     */
    private DefaultEnumProvider(ResourceBundleHelper helper, W logger, L dftLevel, LoggerConsumer<W, L> consumer, BiFunction<String, Object[], String> logMsgFormatter) {

        this.helper = helper;
        this.logger = logger;
        this.currentLogLevel = dftLevel;
        this.logConsumer = consumer;
        this.messageFormatter = logMsgFormatter;
    }

    @Override
    public String getMessage(E enumConstant, Object[] values) {
        return helper.getString(enumConstant.getPropertyName(), messageFormatter, values);
    }

    @Override
    public void logMessage(E enumConstant, Object... values) {
        logMessage(enumConstant, currentLogLevel, values);
    }

    @Override
    public void logMessage(E enumConstant, L level, Object... values) {
        String logMessage = helper.getString(enumConstant.getPropertyName(), messageFormatter, values);
        logConsumer.accept(logger, level, logMessage);
    }

    @Override
    public <T extends Exception> T getException(E enumConstant, Class<T> exceptionClass, Object... values) {
        return helper.createException(exceptionClass, enumConstant.getPropertyName(), values);
    }

    @Override
    public <T extends Exception> T getException(E enumConstant, Class<T> exceptionClass, Throwable cause, Object... values) {
        return helper.createException(exceptionClass, cause, enumConstant.getPropertyName(), values);
    }
}
