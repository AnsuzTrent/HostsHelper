package org.akvo.foundation.ioc;

import org.akvo.foundation.ioc.anontations.*;
import org.akvo.foundation.ioc.core.BeanDefinition;
import org.akvo.foundation.util.collection.StreamUtil;
import org.akvo.foundation.util.throwing.Throwing;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class ApplicationContext {
    /**
     * Bean 定义
     */
    private static final Map<String, BeanDefinition<?>> beanDefinitionMap = new ConcurrentHashMap<>();
    /**
     * 正在被实例化的Bean，用于阻断循环依赖
     */
    private static final Map<String, BeanDefinition<?>> beingInstantiatedBeanDefinitionMap = new ConcurrentHashMap<>();
    /**
     * 记录Bean 实例化时需要多少个参数，会在实例化Bean 的时候使用
     */
    private static final Map<String, Integer> beanNameParameterCountMap = new ConcurrentHashMap<>();
    /**
     * 单例池
     */
    private static final Map<String, Object> singletonObjects = new ConcurrentHashMap<>();
    private static final Map<Class<? extends Annotation>, Annotation> loadedAnnotatedMap = new ConcurrentHashMap<>();

    public static void reset() {
        beanDefinitionMap.clear();
        beanNameParameterCountMap.clear();
        singletonObjects.clear();
        beingInstantiatedBeanDefinitionMap.clear();
    }

    public static void run(Class<?> clazz) throws IOException {
        reset();
        // 1. 获得扫描路径
        Optional<ComponentScan> annotation = Optional.ofNullable(clazz.getAnnotation(ComponentScan.class));
        String scanPath = annotation.map(ComponentScan::value)
            .filter(StringUtils::isNotBlank)
            .orElseGet(clazz::getPackageName);
        // 排除路径
        Set<String> excludes = annotation.map(ComponentScan::excludes).stream()
            .flatMap(StreamUtil::streamNullable)
            .filter(StringUtils::isNotBlank)
            .collect(Collectors.toSet());

        // 2. 根据扫描路径获得所有需要处理的Component 类
        scan(clazz, scanPath, excludes);
        System.out.println(beanDefinitionMap.size());

        // 3. 尝试实例化所有的单例Bean
        StreamUtil.streamNullable(beanNameParameterCountMap.entrySet())
            .filter(entry -> Scope.Type.SINGLETON == beanDefinitionMap.get(entry.getKey()).scopeType())
            .collect(Collectors.groupingBy(Map.Entry::getValue,
                Collectors.mapping(Map.Entry::getKey, Collectors.toList())))
            // 实例化所需要的参数数量 -> BeanNameList
            .forEach((count, beanNameList) -> StreamUtil.streamNullable(beanNameList)
                .forEach(ApplicationContext::getBean));
    }

    private static void scan(Class<?> clazz, String scanPath, Set<String> excludes) throws IOException {
        StreamUtil.streamNullable(scanPath.replace(".", "/"))
            .map(Throwing.sneakyFunction(ClassLoader::getSystemResources))
            .map(Enumeration::asIterator)
            .flatMap(StreamUtil::streamIterator)
            .map(URL::getFile)
            .map(File::new)
            // 递归遍历子包
            .flatMap(ApplicationContext::recursivelyScanPackage)
            .parallel()
            .map(File::getAbsolutePath)
            // 只要class
            .filter(fileName -> fileName.endsWith(".class"))
            // 从文件名获得全限定名，排除开头的盘符和末尾的文件类型
            .map(fileName -> {
                int beginIndex = fileName.indexOf(scanPath.replace(".", File.separator));
                int endIndex = fileName.indexOf(".class");
                return fileName.substring(beginIndex, endIndex);
            })
            .map(fileName -> fileName.replace(File.separator, "."))
            // 排除包
            .filter(Predicate.not(excludes::contains))
            .map(Throwing.sneakyFunction(clazz.getClassLoader()::loadClass))
            .filter(beanClass -> beanClass.isAnnotationPresent(Component.class)
                || beanClass.isAnnotationPresent(Spi.class))
            .filter(Predicate.not(Class::isInterface))
            .flatMap(beanClass -> StreamUtil.merge(
                StreamUtil.streamNullable(beanClass),
                StreamUtil.streamNullable(beanClass.getMethods())
                    .filter(method -> !method.getReturnType().equals(Void.class))
                    .filter(method -> !"void".equals(method.getReturnType().getName()))
                    .filter(method -> method.isAnnotationPresent(Bean.class))))
            .distinct()
            .forEach(declaration -> {
                // 如果没标注就是单例
                var scopeType = Optional.ofNullable(declaration.getAnnotation(Scope.class))
                    .map(Scope::value)
                    .orElse(Scope.Type.SINGLETON);
                // 记录Bean 定义
                appendBeanDefinition(declaration, scopeType);
            });
    }

    /**
     * 填充BeanDefinition
     *
     * @param beanDeclaredWith bean 的声明，可能通过类，有可能通过方法
     * @param scopeType        作用域
     */
    private static void appendBeanDefinition(GenericDeclaration beanDeclaredWith,
                                             Scope.Type scopeType) {
        Class<?> beanType;
        Executable executable;
        String beanName;

        if (beanDeclaredWith instanceof Class<?> beanClass) {
            beanType = checkSpi(beanClass);
            executable = obtainAppropriateConstructor(beanClass);
            beanName = obtainBeanName(beanType);
        } else if (beanDeclaredWith instanceof Method beanMethod) {
            beanType = checkSpi(beanMethod.getReturnType());
            executable = beanMethod;
            beanName = obtainBeanName(beanMethod);
        } else {
            throw new IllegalArgumentException("bean [%s] type unknown: %s"
                .formatted(beanDeclaredWith.toString(), beanDeclaredWith.getClass().getName()));
        }

        var beanDefinition = beanDefinitionMap.get(beanName);
        if (beanDefinition != null) {
            throw new IllegalArgumentException("bean [%s](%s) defined: %s"
                .formatted(beanName, beanDeclaredWith.toString(), beanDefinition.type().getName()));
        }

        beanDefinitionMap.put(beanName, new BeanDefinition<>(beanName, beanType, scopeType, executable));
        beanNameParameterCountMap.put(beanName, executable.getParameterCount());
    }


    /**
     * 获取Bean，单例的直接在单例池获取，不在单例池或者原型类型直接递归地创建
     *
     * @param beanName beanName
     * @param <T>      Bean Type
     * @return bean
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String beanName) {
        var beanDefinition = beanDefinitionMap.get(beanName);
        if (beanDefinition == null) {
            throw new NoSuchElementException("bean [%s] not exists".formatted(beanName));
        }

        var buildBean = Throwing.sneakySupplier(() -> Optional.ofNullable(createBean(beanDefinition))
            .orElseThrow(() -> new NoSuchElementException("%s bean [%s] instance fail"
                .formatted(beanDefinition.scopeType().name(), beanName))));

        // 原型，创建
        if (Scope.Type.PROTOTYPE.equals(beanDefinition.scopeType())) {
            return (T) buildBean.get();
        }

        return (T) Optional.ofNullable(singletonObjects.get(beanName))
            .orElseGet(() -> singletonObjects.put(beanName, buildBean.get()));
    }

    private static Class<?> checkSpi(Class<?> beanClass) {
        if (!beanClass.isAnnotationPresent(Spi.class)) {
            return beanClass;
        }
        return recursivelyScanSuperClass(beanClass)
            .filter(clazz -> clazz.isAnnotationPresent(Spi.class))
            .filter(Class::isInterface)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("bean [%s] declared with SPI, but no interface find"
                .formatted(beanClass.getName())));
    }

    public static <T> T getBean(Class<T> beanType) {
        return getBean(obtainBeanName(beanType));
    }

    /**
     * 按照需要参数的数量从少到多构建实例
     *
     * @param definition bean definition
     */
    private static Object createBean(BeanDefinition<?> definition) throws Exception {
        String beingInstantiatedBeanName = definition.name();
        Class<?> type = definition.type();
        if (beingInstantiatedBeanDefinitionMap.containsKey(beingInstantiatedBeanName)) {
            throw new IllegalArgumentException("A circular dependency occurs in bean [%s](%s)"
                .formatted(beingInstantiatedBeanName, type.getName()));
        }

        beingInstantiatedBeanDefinitionMap.put(beingInstantiatedBeanName, definition);
        Executable executable = definition.executable();
        Object[] parameters = obtainParameters(executable);

        Object bean = null;

        if (executable instanceof Constructor<?> constructor) {
            bean = constructor.newInstance(parameters);
        }
        if (executable instanceof Method method) {
            Class<?> declaringClass = method.getDeclaringClass();
            bean = method.invoke(getBean(declaringClass), parameters);
        }

        beingInstantiatedBeanDefinitionMap.remove(beingInstantiatedBeanName);
        return bean;
    }

    /**
     * 获得一个合适的public 构造方法，要么被{@link Autowired} 标记，要么是无参构造方法
     *
     * @param clazz BeanClass
     * @return 构造方法
     */
    private static Constructor<?> obtainAppropriateConstructor(Class<?> clazz) {
        return StreamUtil.streamNullable(clazz.getConstructors())
            .filter(constructor -> constructor.isAnnotationPresent(Autowired.class))
            .findFirst()
            .or(() -> StreamUtil.streamNullable(clazz.getConstructors())
                .filter(constructor -> constructor.getParameterCount() == 0)
                .findFirst())
            .orElseThrow(() -> new IllegalArgumentException("bean [%s](%s) don't have an appropriate constructor"
                .formatted(clazz, clazz.toString())));
    }

    /**
     * 优先检查{@link Component#value()} ，没有就用类全名
     *
     * @param clazz 要获得BeanName 的类
     * @return BeanName
     */
    private static String obtainBeanName(Class<?> clazz) {
        return Optional.ofNullable(checkSpi(clazz))
            .map(c -> c.getAnnotation(Component.class))
            .map(Component::value)
            .filter(StringUtils::isNotBlank)
            .orElseGet(checkSpi(clazz)::getName);
    }

    /**
     * 优先检查{@link Bean#value()} ，没有就用{@link #obtainBeanName(Class)} 来获得方法返回值的BeanName
     *
     * @param method 要获得BeanName 的类
     * @return BeanName
     */
    private static String obtainBeanName(Method method) {
        return Optional.ofNullable(method.getAnnotation(Bean.class))
            .map(Bean::value)
            .filter(StringUtils::isNotBlank)
            .orElseGet(() -> obtainBeanName(method.getReturnType()));
    }

    /**
     * 检查方法或者构造方法的参数，并使用{@link #getBean(String)}尝试获取这些参数的实例
     * <p>
     * 获取实例时优先使用{@link Designate#value()} ，没有就用{@link #obtainBeanName(Class)} 来获得参数类型的BeanName
     *
     * @param executable 方法或者构造方法
     * @return 参数的值
     */
    private static Object[] obtainParameters(Executable executable) {
        return StreamUtil.streamNullable(executable.getParameters())
            .map(parameter -> Optional.ofNullable(parameter.getAnnotation(Designate.class))
                .map(Designate::value)
                .filter(StringUtils::isNotBlank)
                .orElseGet(() -> obtainBeanName(parameter.getType())))
            .map(ApplicationContext::getBean)
            .toArray();
    }

    /**
     * 递归地获取一个目录下的所有文件
     *
     * @param file 目录
     * @return 所有文件
     */
    public static Stream<File> recursivelyScanPackage(File file) {
        return StreamUtil.streamNullable(file)
            .flatMap(f -> StreamUtil.merge(StreamUtil.streamNullable(f),
                StreamUtil.streamNullable(f.listFiles())
                    .flatMap(ApplicationContext::recursivelyScanPackage)))
            .filter(Predicate.not(File::isDirectory))
            .distinct();
    }

    /**
     * 递归地获取一个类的所有父类与实现的接口
     *
     * @param clazz 需要扫描继承关系的类
     * @return 深度优先扫描出的所有父类
     */
    public static Stream<Class<?>> recursivelyScanSuperClass(Class<?> clazz) {
        return StreamUtil.streamNullable(clazz)
            .flatMap(aClass -> StreamUtil.merge(StreamUtil.streamNullable(aClass),
                recursivelyScanSuperClass(aClass.getSuperclass()),
                StreamUtil.streamNullable(aClass.getInterfaces())
                    .flatMap(ApplicationContext::recursivelyScanSuperClass)))
            .distinct();
    }

    /**
     * 递归地获取所有注解以及注解的注解
     *
     * @param element 需要扫描注解的目标
     * @return 深度优先扫描出的所有注解
     */
    public static Stream<Annotation> recursivelyScanAnnotation(AnnotatedElement element) {
        return StreamUtil.streamNullable(element.getAnnotations())
            .filter(annotation -> !loadedAnnotatedMap.containsKey(annotation.annotationType()))
            .peek(annotation -> loadedAnnotatedMap.put(annotation.annotationType(), annotation))
            .flatMap(annotation -> StreamUtil.merge(StreamUtil.streamNullable(annotation),
                recursivelyScanAnnotation(annotation.annotationType())))
            .distinct();
    }

}
