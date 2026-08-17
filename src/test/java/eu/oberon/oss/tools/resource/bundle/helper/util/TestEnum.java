package eu.oberon.oss.tools.resource.bundle.helper.util;

public enum TestEnum implements ResourceBundleUtilityEnum {
    TEST_VALUE_1,
    TEST_VALUE_2("custom_relative_key"),
    ;

    final String propertyName;

    TestEnum() {
        this.propertyName = name().toLowerCase().replace("_", ".");
    }

    TestEnum(String propertyName) {
        this.propertyName = propertyName;
    }

    @Override
    public String getPropertyName() {
        return propertyName;
    }

    @Override
    public String getMessage(Object... values) {
        return _provider.getMessage(this, values);
    }

    @Override
    public <T extends Exception> T getException(Class<T> exceptionClass, Object... values) {
        return _provider.getException(this, exceptionClass, values);
    }

    @Override
    public <T extends Exception> T getException(Class<T> exceptionClass, Throwable cause, Object... values) {
        return _provider.getException(this, exceptionClass, cause, values);
    }

    @Override
    public void logMessage(Object... values) {
        _provider.logMessage(this, values);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <L> void logMessage(L level, Object... values) {
        ((EnumProvider<L, TestEnum>) _provider).logMessage(this, level, values);
    }

    private static EnumProvider<?, TestEnum> _provider;

    @SuppressWarnings("unused")
    public static <L> void init(EnumProvider<L, TestEnum> provider) {
        _provider = provider;
    }
}
