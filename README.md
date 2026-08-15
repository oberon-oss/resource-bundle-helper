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

    @Override
    public @NotNull String getDelimiter() {
        return ".";
    }
}
```

#### 2. Register the Provider

**Class Path:**
Create a file named `META-INF/services/eu.oberon.oss.tools.resource.bundle.helper.ResourceBundleHelperProvider` in your JAR and add the fully qualified name of your implementation:

```text
com.example.i18n.MyMessagesProvider
```

**Module Path (JPMS):**
In your `module-info.java`, declare that your module provides the service:

```java
provides eu.oberon.oss.tools.resource.bundle.helper.ResourceBundleHelperProvider
    with com.example.i18n.MyMessagesProvider;
```

#### 3. Accessing Registered Helpers

Once registered, the helper is automatically loaded by `ResourceBundleHelperRegistry` and can be retrieved using its prefix:

```java
ResourceBundleHelper helper = ResourceBundleHelperRegistry.retrieve("my.app");
if (helper != null) {
    String greeting = helper.getString("welcome");
}
```

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
