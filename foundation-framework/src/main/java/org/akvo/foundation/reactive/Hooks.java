package org.akvo.foundation.reactive;

import java.util.function.Supplier;

public abstract class Hooks {
    private static boolean logTrace = false;


    public static void onDebug() {
        setLogTrace(true);
    }

//    public static <T, P extends Flowable<T>> Flowable<T> addAssemblyInfo(P publisher) {
//        return new ReactOnAssembly<>(publisher, new TraceSupplierFactory().get());
//    }

    public static boolean isLogTrace() {
        return logTrace;
    }

    public static void setLogTrace(boolean logTrace) {
        Hooks.logTrace = logTrace;
    }

    static class TraceSupplierFactory implements Supplier<Supplier<String>> {
        @Override
        public Supplier<String> get() {
            return () -> StackWalker.getInstance()
                .walk(stackFrameStream -> stackFrameStream.dropWhile(StackWalker.StackFrame::isNativeMethod)
                    .dropWhile(stackFrame -> {
                        String className = stackFrame.getClassName();
                        return className.startsWith(Hooks.class.getPackageName())
                            && !className.contains("Test");
                    })
                    // 一般留一个就够用了，是实际上调用的位置
                    .limit(3)
                    .reduce(new StringBuilder(),
                        (builder, stackFrame) -> builder.append("\t")
                            .append(stackFrame.toString())
                            .append("\n"),
                        (b1, b2) -> b1))
                .toString();
        }
    }
}
