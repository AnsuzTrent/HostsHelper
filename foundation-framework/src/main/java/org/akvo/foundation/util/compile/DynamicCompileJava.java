package org.akvo.foundation.util.compile;

import javax.tools.*;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public abstract class DynamicCompileJava {
    /**
     * 存放编译之后的字节码
     * <p>
     * 类全名 -> 字节码对象
     */
    private static final Map<String, ByteJavaFileObject> JAVA_FILE_OBJECT_MAP = new ConcurrentHashMap<>();

    public static Class<?> compile(String sourceCode, String fullClassName, List<String> options) throws Exception {
        // 当前编译器
        var compiler = ToolProvider.getSystemJavaCompiler();
        // 自定义的编译异常处理方式
        var diagnosticsCollector = new DiagnosticCollector<>();
        // Java标准文件管理器
        var manager = new StringJavaFileManager(compiler, diagnosticsCollector);
        // 编译单元
        var compileUnits = List.of(new StringJavaFileObject(fullClassName, sourceCode));
        // 设置缓存方式，这里使用-d
        var optionList = new ArrayList<>(options);
        // 设置编译条件
        var task = compiler.getTask(null, manager, diagnosticsCollector, optionList, null, compileUnits);

        if (Boolean.TRUE.equals(task.call())) {
            ClassLoader classLoader = new StringJavaClassLoader();
            return classLoader.loadClass(fullClassName);
        }

        var diagnostics = diagnosticsCollector.getDiagnostics();

        var errors = diagnostics.stream()
            .map(Object::toString)
            .collect(Collectors.joining("\n"));
        throw new IllegalStateException(errors);
    }

    private abstract static class BaseJavaFileObject extends SimpleJavaFileObject {
        protected BaseJavaFileObject(String clsName, JavaFileObject.Kind kind) {
            super(createUri(clsName), kind);
        }

        protected static URI createUri(String clsName) {
            return URI.create("string:///%s%s".formatted(
                clsName.replace(".", "/"), Kind.SOURCE.extension));
        }

    }

    /**
     * 源码对象
     */
    private static class StringJavaFileObject extends BaseJavaFileObject {
        /**
         * 源代码
         */
        private final String content;

        StringJavaFileObject(String clsName, String sourceCode) {
            super(clsName, Kind.SOURCE);
            content = sourceCode;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return content;
        }
    }

    /**
     * 字节码对象
     */
    private static class ByteJavaFileObject extends BaseJavaFileObject {
        /**
         * 字节码输出流
         */
        private ByteArrayOutputStream outPutStream;

        ByteJavaFileObject(String clsName, Kind kind) {
            super(clsName, kind);
        }

        @Override
        public OutputStream openOutputStream() {
            outPutStream = new ByteArrayOutputStream();
            return outPutStream;
        }

        public byte[] getCompiledBytes() {
            return outPutStream.toByteArray();
        }
    }

    /**
     * 自定义文件管理器，用于避免生成的字节码以文件的方式存在本地
     */
    private static class StringJavaFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {
        StringJavaFileManager(JavaCompiler compiler, DiagnosticCollector<? super JavaFileObject> diagnosticCollector) {
            super(compiler.getStandardFileManager(diagnosticCollector, null, null));
        }

        /**
         * 获取输出的文件对象，它表示给定位置处指定类型的指定类
         *
         * @param location  a package-oriented location
         * @param className the name of a class
         * @param kind      the kind of file, must be one of {@link
         *                  JavaFileObject.Kind#SOURCE SOURCE} or {@link
         *                  JavaFileObject.Kind#CLASS CLASS}
         * @param sibling   a file object to be used as hint for placement;
         *                  might be {@code null}
         */
        @Override
        public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) {
            ByteJavaFileObject byteJavaFileObject = new ByteJavaFileObject(className, kind);
            JAVA_FILE_OBJECT_MAP.put(className, byteJavaFileObject);
            return byteJavaFileObject;
        }
    }

    /**
     * 自定义类加载器，直接加载字节码，否则可能找不到类
     */
    private static class StringJavaClassLoader extends ClassLoader {
        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            ByteJavaFileObject fileObject = JAVA_FILE_OBJECT_MAP.get(name);
            if (fileObject != null) {
                byte[] bytes = fileObject.getCompiledBytes();
                return defineClass(name, bytes, 0, bytes.length);
            }
            try {
                return ClassLoader.getSystemClassLoader().loadClass(name);
            } catch (Exception e) {
                return super.findClass(name);
            }
        }
    }
}
