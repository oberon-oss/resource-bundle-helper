# Resource Bundle Helper

A lightweight Java library to simplify `ResourceBundle` access. It supports manual registration and automatic discovery via the `ServiceLoader` mechanism.

## Installation

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>eu.oberon-oss.tools</groupId>
    <artifactId>resource-bundle-helper</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

## Basic Usage

### Manual Registration

You can register a `ResourceBundleHelper` manually by providing a key prefix, a `ResourceBundle`, and a delimiter.

```java
import eu.oberon.oss.tools.resource.bundle.helper.ResourceBundleHelper;
import eu.oberon.oss.tools.resource.bundle.helper.ResourceBundleHelperRegistry;
import java.util.ResourceBundle;

// Register a helper
ResourceBundle bundle = ResourceBundle.getBundle("messages");
ResourceBundleHelper helper = ResourceBundleHelperRegistry.register("app.ui", bundle, ".");

// Use the helper to retrieve localized strings
String title = helper.getString("main.title"); // Lookups "app.ui.main.title"
```

### Automatic Discovery via ServiceLoader

This library allows projects to contribute `ResourceBundleHelper` instances automatically using the Java `ServiceLoader` mechanism.

#### 1. Implement `ResourceBundleHelperProvider`

Create a class that implements the `ResourceBundleHelperProvider` interface:

```java
package com.example.i18n;

import eu.oberon.oss.tools.resource.bundle.helper.ResourceBundleHelperProvider;
import org.jetbrains.annotations.NotNull;
import java.util.ResourceBundle;

public class MyMessagesProvider implements ResourceBundleHelperProvider {
    @Override
    public @NotNull ResourceBundle getResourceBundle() {
        return ResourceBundle.getBundle("my-messages");
    }

    @Override
    public @NotNull String getKeyPrefix() {
        return "my.app";
    }
}
```

#### 2. Register the Provider

**Class Path:**
Create a file named `META-INF/services/eu.oberon.oss.tools.resource.bundle.helper.ResourceBundleHelperProvider` in your JAR and add the fully qualified name of your implementation:

#### 3. Accessing Registered Helpers

Once registered, the helper is automatically loaded by `ResourceBundleHelperRegistry` during class initialization and can be retrieved using its prefix:

```java
void testRetrieve() {
    ResourceBundleHelper helper = ResourceBundleHelperRegistry.retrieve("my.app");
    if (helper != null) {
        String greeting = helper.getString("welcome");
    }
}
```

#### 4. Manual Discovery Trigger

In some environments (e.g., modular applications or complex classloader hierarchies), the automatic discovery might trigger before all providers are visible to the thread context class loader. You can manually trigger (or re-trigger) the discovery by calling:

```java
static {
    ResourceBundleHelperRegistry.loadProviders();
}
```

This will scan for and register any newly discovered providers that haven't been registered yet.

## Advanced Usage: Enum Utilities

The `util` package allows tying enums to resource bundles for simplified messaging and logging.

### 1. Implement `ResourceBundleUtilityEnum`

Your enum should implement `ResourceBundleUtilityEnum` and delegate to an `EnumProvider`. It must also provide a `public static <L> void init(EnumProvider<L, YourEnum> provider)` method.

```java
public enum MyMessages implements ResourceBundleUtilityEnum {
    USER_NOT_FOUND("err.404");

    private final String key;
    private static EnumProvider<?, MyMessages> _provider;

    MyMessages(String key) { this.key = key; }

    public static <L> void init(EnumProvider<L, MyMessages> provider) { _provider = provider; }

    @Override public String getPropertyName() { return key; }
    @Override public String getMessage(Object... v) { return _provider.getMessage(this, v); }
    @Override public void logMessage(Object... v) { _provider.logMessage(this, v); }
    @Override public <L> void logMessage(L l, Object... v) { ((EnumProvider<L, MyMessages>)_provider).logMessage(this, l, v); }
    @Override public <T extends Exception> T getException(Class<T> c, Object... v) { return _provider.getException(this, c, v); }
    @Override public <T extends Exception> T getException(Class<T> c, Throwable t, Object... v) { return _provider.getException(this, c, t, v); }
}
```

### 2. Initialize and Use

Use `DefaultEnumProvider` to link your enum with a `ResourceBundleHelper` and a logger.

```java
void example() {
    // Initialize the provider for the enum
    DefaultEnumProvider.initForEnum(
            MyMessages.class, helper, logger, Level.INFO,
            (log, level, msg) -> log.atLevel(level).log(msg),
            (fmt, args) -> String.format(fmt, args)
    );

    // Use the enum methods directly
    String msg = MyMessages.USER_NOT_FOUND.getMessage();
    throw MyMessages.USER_NOT_FOUND.getException(RuntimeException.class);
}
```

### Reflection Utilities

`MethodTool` provides helper methods for reflection:
- `getMethodSignature(Method)`: Generates a human-readable method signature.
- `getSimpleTypeName(Type)`: Returns a simple name for complex generic types.

## Features

- **Key Prefixing**: Automatically prepends a prefix to all keys.
- **Custom Delimiters**: Define how prefixes and keys are joined (e.g., `.`, `_`, `/`).
- **Flexible Retrieval**: Retrieve strings, formatted strings, or objects.
- **Thread-Safe Registry**: Global registry for managing helpers.

## Build status:
[![Quality gate status](https://sonarcloud.io/api/project_badges/measure?project=oberon-oss_resource-bundle-helper&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=oberon-oss_resource-bundle-helper)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=oberon-oss_resource-bundle-helper&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=oberon-oss_resource-bundle-helper)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=oberon-oss_resource-bundle-helper&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=oberon-oss_resource-bundle-helper)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=oberon-oss_resource-bundle-helper&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=oberon-oss_resource-bundle-helper)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=oberon-oss_resource-bundle-helper&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=oberon-oss_resource-bundle-helper)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=oberon-oss_resource-bundle-helper&metric=coverage)](https://sonarcloud.io/summary/new_code?id=oberon-oss_resource-bundle-helper)

[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=oberon-oss_resource-bundle-helper&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=oberon-oss_resource-bundle-helper)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=oberon-oss_resource-bundle-helper&metric=bugs)](https://sonarcloud.io/summary/new_code?id=oberon-oss_resource-bundle-helper)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=oberon-oss_resource-bundle-helper&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=oberon-oss_resource-bundle-helper)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=oberon-oss_resource-bundle-helper&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=oberon-oss_resource-bundle-helper)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=oberon-oss_resource-bundle-helper&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=oberon-oss_resource-bundle-helper)
