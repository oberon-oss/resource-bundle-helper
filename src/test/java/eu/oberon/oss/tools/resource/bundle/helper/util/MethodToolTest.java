package eu.oberon.oss.tools.resource.bundle.helper.util;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("java:S1144")
class MethodToolTest {

    @Test
    void testGetSimpleTypeName_Class() {
        assertThat(MethodTool.getSimpleTypeName(String.class)).isEqualTo("String");
        assertThat(MethodTool.getSimpleTypeName(int.class)).isEqualTo("int");
        assertThat(MethodTool.getSimpleTypeName(void.class)).isEqualTo("void");
    }

    @Test
    void testGetSimpleTypeName_ParameterizedType() throws NoSuchMethodException {
        Method method = TestClass.class.getDeclaredMethod("listMethod", List.class);
        assertThat(MethodTool.getSimpleTypeName(method.getGenericParameterTypes()[0])).isEqualTo("List<String>");

        method = TestClass.class.getDeclaredMethod("mapMethod", Map.class);
        assertThat(MethodTool.getSimpleTypeName(method.getGenericParameterTypes()[0])).isEqualTo("Map<String, Integer>");
    }

    @Test
    void testGetSimpleTypeName_NestedParameterizedType() throws NoSuchMethodException {
        Method method = TestClass.class.getDeclaredMethod("nestedMethod", List.class);
        assertThat(MethodTool.getSimpleTypeName(method.getGenericParameterTypes()[0])).isEqualTo("List<Map<String, List<Integer>>>");
    }

    @Test
    void testGetMethodSignature_Basic() throws NoSuchMethodException {
        Method method = TestClass.class.getDeclaredMethod("simpleMethod");
        assertThat(MethodTool.getMethodSignature(method)).isEqualTo("public void simpleMethod()");
    }

    @Test
    void testGetMethodSignature_WithParameters() throws NoSuchMethodException {
        Method method = TestClass.class.getDeclaredMethod("methodWithParams", String.class, int.class);
        // Note: parameter names might not be present unless compiled with -parameters
        String signature = MethodTool.getMethodSignature(method);
        assertThat(signature)
                .startsWith("private String methodWithParams(String")
                .contains("int")
                .endsWith(")");
    }

    @Test
    void testGetMethodSignature_WithGenericParameters() throws NoSuchMethodException {
        Method method = TestClass.class.getDeclaredMethod("listMethod", List.class);
        String signature = MethodTool.getMethodSignature(method);
        assertThat(signature).startsWith("public void listMethod(List<String>");
    }

    @Test
    void testGetMethodSignature_WithModifiers() throws NoSuchMethodException {
        Method method = TestClass.class.getDeclaredMethod("staticMethod");
        assertThat(MethodTool.getMethodSignature(method)).isEqualTo("public static void staticMethod()");
    }

    @Test
    void testGetMethodSignature_WithTypeParameters() throws NoSuchMethodException {
        Method method = TestClass.class.getDeclaredMethod("genericMethod", Object.class);
        String signature = MethodTool.getMethodSignature(method);
        assertThat(signature)
                .startsWith("public <T> T genericMethod(T")
                .endsWith(")");
    }

    @SuppressWarnings({"unused","java:S1172"})
    static class TestClass {
        public void simpleMethod() {
            // For testing only
        }

        private String methodWithParams(String s, int i) {
            return null;
        }

        public void listMethod(List<String> list) {
            // For testing only
        }

        public void mapMethod(Map<String, Integer> map) {
            // For testing only
        }

        public void nestedMethod(List<Map<String, List<Integer>>> nested) {
            // For testing only
        }

        public static void staticMethod() {
            // For testing only
        }

        public <T> T genericMethod(T t) {
            return t;
        }
    }
}
