package eu.oberon.oss.tools.resource.bundle.helper.util;

import java.lang.reflect.*;

/**
 * Utility class for working with Java reflection to generate method signatures and simple type names.
 * <p>
 * This class cannot be instantiated and provides static methods.
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public class MethodTool {
    private MethodTool() {
        //
    }

    /**
     * Generates a method signature string from a given Method object.
     *
     * @param method The Method object to generate the signature for.
     *
     * @return The method signature as a string.
     *
     * @since 1.0.0
     */
    public static String getMethodSignature(Method method) {
        StringBuilder signature = new StringBuilder();

        String modifiers = Modifier.toString(method.getModifiers());
        if (!modifiers.isBlank()) {
            signature.append(modifiers).append(' ');
        }

        TypeVariable<Method>[] typeParameters = method.getTypeParameters();
        if (typeParameters.length > 0) {
            signature.append('<');

            for (int i = 0; i < typeParameters.length; i++) {
                if (i > 0) {
                    signature.append(", ");
                }
                signature.append(typeParameters[i].getName());
            }

            signature.append("> ");
        }

        signature.append(getSimpleTypeName(method.getGenericReturnType()))
                .append(' ')
                .append(method.getName())
                .append('(');

        Type[] parameterTypes = method.getGenericParameterTypes();
        Parameter[] parameters = method.getParameters();

        for (int i = 0; i < parameterTypes.length; i++) {
            if (i > 0) {
                signature.append(", ");
            }

            signature.append(getSimpleTypeName(parameterTypes[i]));

            if (parameters[i].isNamePresent()) {
                signature.append(' ').append(parameters[i].getName());
            }
        }

        signature.append(')');

        return signature.toString();
    }

    /**
     * Generates a simple type name string from a given Type object.
     *
     * @param type The Type object to generate the simple name for.
     *
     * @return The simple type name as a string.
     *
     * @since 1.0.0
     */
    public static String getSimpleTypeName(Type type) {
        if (type instanceof Class<?> typeClass) {
            return typeClass.getSimpleName();
        }

        if (type instanceof TypeVariable<?> typeVariable) {
            return typeVariable.getName();
        }

        if (type instanceof ParameterizedType parameterizedType) {
            StringBuilder typeName = new StringBuilder(getSimpleTypeName(parameterizedType.getRawType()));
            Type[] typeArguments = parameterizedType.getActualTypeArguments();

            typeName.append('<');

            for (int i = 0; i < typeArguments.length; i++) {
                if (i > 0) {
                    typeName.append(", ");
                }
                typeName.append(getSimpleTypeName(typeArguments[i]));
            }

            typeName.append('>');
            return typeName.toString();
        }

        return type.getTypeName();
    }

}
