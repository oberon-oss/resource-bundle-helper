package eu.oberon.oss.tools.resource.bundle.helper.test;

import eu.oberon.oss.tools.resource.bundle.helper.ResourceBundleHelperProvider;
import org.jetbrains.annotations.NotNull;

import java.util.ListResourceBundle;
import java.util.ResourceBundle;

public final class TestResourceBundleHelperProvider implements ResourceBundleHelperProvider {

    @Override
    public @NotNull ResourceBundle getResourceBundle() {
        return new ListResourceBundle() {
            @Override
            protected Object[][] getContents() {
                return new Object[][]{
                        {"test.message", "Hello from ServiceLoader"}
                };
            }
        };
    }

    @Override
    public String getKeyPrefix() {
        return "test";
    }

    @Override
    public String getDelimiter() {
        return ".";
    }
}