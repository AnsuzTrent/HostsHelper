package org.akvo.foundation.ioc;

import org.akvo.foundation.ioc.anontations.Component;
import org.akvo.foundation.ioc.anontations.ComponentScan;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;


class InitiationTest {
    @Test
    void testRun() {
        Initiation.run(TestClass1.class, () -> System.out.println("finish"));
        System.out.println(Assertions.assertThrows(IllegalArgumentException.class, () ->
                Initiation.run(TestClass2.class, () -> System.out.println("finish")))
            .getMessage());
    }

    @Test
    void scanAnnotations() {
        ApplicationContext.recursivelyScanAnnotation(TestClass1.class)
            .map(Annotation::annotationType)
            .map(Class::getSimpleName)
            .map("@%s"::formatted)
            .sorted()
            .forEach(System.out::println);
    }

    @Test
    void scanAllSuperClassAndInterface() {
        ApplicationContext.recursivelyScanSuperClass(ComponentScan.class)
            .map(clazz -> "%s %s".formatted((clazz.isInterface() ? "I" : "O"), clazz.getName()))
            .sorted()
            .forEach(System.out::println);
    }

    @Test
    void scanAllAnnotations() {
        ApplicationContext.recursivelyScanSuperClass(TestClass1.class)
            .flatMap(ApplicationContext::recursivelyScanAnnotation)
            .map(Annotation::annotationType)
            .map(Class::getSimpleName)
            .map("@%s"::formatted)
            .sorted()
            .forEach(System.out::println);
    }

    @ComponentScan(excludes = "org.akvo.foundation.ioc.test.BeanSelfDependency")
    static class TestClass1 extends TestClass2 {
    }

    @Component
    static class TestClass2 {
        public TestClass2() {
        }
    }
}
