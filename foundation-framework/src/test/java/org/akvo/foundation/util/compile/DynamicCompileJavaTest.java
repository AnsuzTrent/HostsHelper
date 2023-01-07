package org.akvo.foundation.util.compile;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;

class DynamicCompileJavaTest {
    @Test
    void dynamicCompileJava() throws Exception {
        // language=java
        String code = """
            package org.test;

            public class HelloWorldTest {
                public static String say(String str) {
                    return String.format("Hello, %s!", str);
                }

                public void print() {
                    System.out.println("print something");
                }
            }""";
        String pkgName = "org.test";
        String clsName = "HelloWorldTest";
        String fullClassName = pkgName + "." + clsName;
        Class<?> compile = DynamicCompileJava.compile(code, fullClassName, List.of());

        Method sayMethod = compile.getMethod("say", String.class);
        Method printMethod = compile.getMethod("print");

        String say = (String) sayMethod.invoke(null, "dynamic");
        System.out.println(say);

        Object o = compile.getConstructor().newInstance();

        System.out.println(printMethod.invoke(o));

        Assertions.assertEquals("Hello, dynamic!", say);
    }

}
