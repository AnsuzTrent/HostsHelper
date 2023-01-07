package org.akvo.foundation.util.current;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;


public final class ThreadFactories {
    private static final String NAME_SPLITTER = "-";
    private static final ThreadFactory DEFAULT_THREAD_FACTORY = Executors.defaultThreadFactory();

    private ThreadFactories() {
    }

    /**
     * 对于默认的{@link ThreadFactory}，将其新建的线程名称增加给定的前缀。
     * <p>
     * 例如，若线程默认名称为"thread"，将会更改为"prefix-thread"。
     * 当前缀的值为{@code null} 时，将其视为空字符串。
     *
     * @param prefix 默认名称之前附加的自定义字符串
     * @return 自定义的线程工厂实
     */
    public static ThreadFactory prefixed(String prefix) {
        Objects.requireNonNull(prefix, "prefix can't be null");
        return prefixed(prefix, DEFAULT_THREAD_FACTORY);
    }

    /**
     * 对于给定的{@link ThreadFactory}，将其新建的线程名称增加给定的前缀。
     * <p>
     * 例如，若线程默认名称为"thread"，将会更改为"prefix-thread"。
     * 当前缀的值为{@code null} 时，将其视为空字符串。
     *
     * @param prefix        默认名称之前附加的自定义字符串
     * @param threadFactory 用作基础的线程工厂实例
     * @return 自定义的线程工厂实
     */
    public static ThreadFactory prefixed(String prefix, ThreadFactory threadFactory) {
        Objects.requireNonNull(threadFactory, "threadFactory can't be null");
        Objects.requireNonNull(prefix, "prefix can't be null");
        return from(prefix, null, threadFactory);
    }

    /**
     * 对于默认的{@link ThreadFactory}，将其新建的线程名称增加给定的后缀。
     * <p>
     * 例如，若线程默认名称为"thread"，将会更改为"thread-suffix"。
     * 当后缀的值为{@code null} 时，将其视为空字符串。
     *
     * @param suffix 默认名称后附加的自定义字符串
     * @return 自定义的线程工厂实
     */
    public static ThreadFactory suffixed(String suffix) {
        Objects.requireNonNull(suffix, "suffix can't be null");
        return suffixed(suffix, DEFAULT_THREAD_FACTORY);
    }

    /**
     * 对于给定的{@link ThreadFactory}，将其新建的线程名称增加给定的后缀。
     * <p>
     * 例如，若线程默认名称为"thread"，将会更改为"thread-suffix"。
     * 当后缀的值为{@code null} 时，将其视为空字符串。
     *
     * @param suffix        默认名称后附加的自定义字符串
     * @param threadFactory 用作基础的线程工厂实例
     * @return 自定义的线程工厂实
     */
    public static ThreadFactory suffixed(String suffix, ThreadFactory threadFactory) {
        Objects.requireNonNull(threadFactory, "threadFactory can't be null");
        Objects.requireNonNull(suffix, "suffix can't be null");
        return from(null, suffix, threadFactory);
    }

    /**
     * 对于给定的{@link ThreadFactory}，将其新建的线程名称增加给定的前缀或/和后缀。
     * <p>
     * 例如，若线程默认名称为"thread"，将会更改为"prefix-thread-suffix"。
     * 当前缀或后缀的值为{@code null} 时，将其视为空字符串。
     *
     * @param prefix        默认名称之前附加的自定义字符串
     * @param suffix        默认名称后附加的自定义字符串
     * @param threadFactory 用作基础的线程工厂实例
     * @return 自定义的线程工厂实
     */
    public static ThreadFactory from(String prefix, String suffix, ThreadFactory threadFactory) {
        var innerPrefix = prefix == null ? "" : prefix + NAME_SPLITTER;
        var innerSuffix = suffix == null ? "" : NAME_SPLITTER + suffix;
        return r -> {
            var thread = threadFactory.newThread(r);
            thread.setName(innerPrefix + thread.getName() + innerSuffix);
            return thread;
        };
    }

    /**
     * 返回预配置为使用命名方案“thread”的 ThreadFactoryBuilder 实例，并会在名称之后加一个%d。
     * <p>
     * 如果之后不会提供name，工厂将创建名为：“thread-0”、“thread-1”、“thread-2”等的线程
     *
     * @return 自定义的线程工厂实例
     */
    public static ThreadFactoriesBuilder builder() {
        return builder(null);
    }

    /**
     * 返回预配置为使用自定义命名方案的 ThreadFactoryBuilder 实例，并会在名称之后加一个%d。
     * <p>
     * 如果提供的 name 是“pool”，工厂将创建名为：“pool-0”、“pool-1”、“pool-2”等的线程
     *
     * @param name 自定义的线程名称
     * @return 自定义的线程工厂实例
     */
    public static ThreadFactoriesBuilder builder(String name) {
        return builder(name, null, null);
    }

    /**
     * 返回预配置为使用自定义命名方案的 ThreadFactoryBuilder 实例，并会在名称之后加一个%d。
     * 如果提供的 name 是“pool”，工厂将创建名为：“prefix-pool-0-suffix”、“prefix-pool-1-suffix”、“prefix-pool-2-suffix”等的线程
     *
     * @param name   自定义的线程名称
     * @param prefix 名称之前附加的自定义字符串
     * @param suffix 名称后附加的自定义字符串
     * @return 自定义的线程工厂实例
     */
    public static ThreadFactoriesBuilder builder(String name, String prefix, String suffix) {
        return new ThreadFactoriesBuilder(name, prefix, suffix);
    }


    public static class ThreadFactoriesBuilder {
        private boolean isDaemon = false;
        private Thread.UncaughtExceptionHandler uncaughtExceptionHandler = null;
        private String newSplitter = NAME_SPLITTER;
        private String name;
        private String prefix;
        private String suffix;
        private ThreadFactory basedThreadFactory;

        public ThreadFactoriesBuilder(String name, String prefix, String suffix) {
            this.name = name;
            this.prefix = prefix;
            this.suffix = suffix;
        }

        public ThreadFactoriesBuilder withPrefix(String prefix) {
            this.prefix = prefix;
            return this;
        }

        public ThreadFactoriesBuilder withSuffix(String suffix) {
            this.suffix = suffix;
            return this;
        }

        public ThreadFactoriesBuilder withSplitter(String splitter) {
            this.newSplitter = splitter;
            return this;
        }

        /**
         * @param threadFactory 用作基础的线程工厂实例
         */
        public ThreadFactoriesBuilder fromThreadFactory(ThreadFactory threadFactory) {
            this.basedThreadFactory = threadFactory;
            return this;
        }

        /**
         * @param daemon 使用此线程工厂创建的新线程是否将成为守护进程线程
         */
        public ThreadFactoriesBuilder isDaemonThreads(boolean daemon) {
            this.isDaemon = daemon;
            return this;
        }

        /**
         * @param uncaughtExceptionHandler 使用此线程工厂创建的新线程的未捕获异常处理程序
         */
        public ThreadFactoriesBuilder withUncaughtExceptionHandler(Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
            this.uncaughtExceptionHandler = uncaughtExceptionHandler;
            return this;
        }

        /**
         * 根据生成期间提供的设置返回新的线程工厂
         *
         * @return {@link ThreadFactory}
         */
        public ThreadFactory build() {
            if (name == null || name.isEmpty()) {
                name = "thread";
            }
            name = String.join(newSplitter, name, "%d");

            if (prefix != null && !prefix.isEmpty()) {
                name = String.join(newSplitter, prefix, name);
            }
            if (suffix != null && !suffix.isEmpty()) {
                name = String.join(newSplitter, name, suffix);
            }

            if (basedThreadFactory == null) {
                basedThreadFactory = Executors.defaultThreadFactory();
            }

            return new ThreadFactory() {
                final AtomicInteger count = new AtomicInteger(0);

                @Override
                public Thread newThread(Runnable r) {
                    var thread = basedThreadFactory.newThread(r);
                    thread.setName(String.format(name, count.getAndIncrement()));
                    thread.setDaemon(isDaemon);
                    // 继承来源threadFactory 的handler
                    if (uncaughtExceptionHandler != null) {
                        thread.setUncaughtExceptionHandler(uncaughtExceptionHandler);
                    }
                    return thread;
                }
            };
        }
    }
}
