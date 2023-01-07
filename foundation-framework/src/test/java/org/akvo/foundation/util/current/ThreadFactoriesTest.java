package org.akvo.foundation.util.current;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

class ThreadFactoriesTest {
    private static final Runnable NOOP = () -> {
    };

    @Test
    void shouldOverrideName_defaultFactory() {
        // given
        String name = "name";
        ThreadFactory tf = ThreadFactories.builder(name)
            .build();

        // when
        Thread thread1 = tf.newThread(() -> {
        });
        Thread thread2 = Executors.defaultThreadFactory()
            .newThread(() -> {
            });

        Assertions.assertThat(thread1.getName())
            .contains(name)
            .endsWith("0");

        // then
        Assertions.assertThat(thread1.isDaemon())
            .isEqualTo(thread2.isDaemon());
        Assertions.assertThat(thread1.getUncaughtExceptionHandler())
            .isEqualTo(thread2.getUncaughtExceptionHandler());
    }

    @Test
    void shouldOverrideDaemon_defaultFactory() {
        // given
        String name = "name";
        Thread.UncaughtExceptionHandler uncaughtExceptionHandler = (t, e) -> {
        };
        ThreadFactory tf = ThreadFactories.builder(name)
            .isDaemonThreads(true)
            .withUncaughtExceptionHandler(uncaughtExceptionHandler)
            .build();

        // when
        Thread thread1 = tf.newThread(() -> {
        });

        Assertions.assertThat(thread1.getName())
            .contains(name)
            .endsWith("0");

        // then
        Assertions.assertThat(thread1.isDaemon()).isTrue();
        Assertions.assertThat(thread1.getUncaughtExceptionHandler())
            .isEqualTo(uncaughtExceptionHandler);
    }

    @Test
    void shouldIncrementName() {
        // given
        ThreadFactory tf = ThreadFactories.builder("")
            .build();

        // when
        Thread thread1 = tf.newThread(() -> {
        });
        Thread thread2 = tf.newThread(() -> {
        });

        // then
        Assertions.assertThat(thread1.getName())
            .isEqualTo("0");
        Assertions.assertThat(thread2.getName())
            .isEqualTo("1");
    }

    @Test
    void shouldUseProvidedThreadFactoryAsBase() {
        // given
        Thread.UncaughtExceptionHandler uncaughtExceptionHandler = (t, e) -> {
        };

        ThreadFactory tf = ThreadFactories.builder("")
            .isDaemonThreads(true)
            .withUncaughtExceptionHandler(uncaughtExceptionHandler)
            .build();

        // when
        ThreadFactory derived = ThreadFactories.builder("")
            .fromThreadFactory(tf)
            .build();

        // then
        Assertions.assertThat(derived.newThread(() -> {
                })
                .getUncaughtExceptionHandler())
            .isEqualTo(uncaughtExceptionHandler);
    }

    @Test
    void shouldPrefixAndThrowException_defaultFactory() {
        Assertions.assertThatThrownBy(() -> ThreadFactories.prefixed(null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldPrefixAndThrowException_customFactory() {
        Assertions.assertThatThrownBy(() -> ThreadFactories.prefixed(null, r -> new Thread())).isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldPrefixThreadName_defaultFactory() {
        // given
        final String prefix = "foo";
        final ThreadFactory tf = ThreadFactories.prefixed(prefix);

        // when
        Thread thread = tf.newThread(NOOP);

        // then
        Assertions.assertThat(thread.getName()).startsWith(prefix);
    }

    @Test
    void shouldSuffixThreadName_customFactory() {
        // given
        final String suffix = "foo";
        final String name = "name";

        ThreadFactory tf = ThreadFactories.suffixed(suffix, r -> new Thread(name));

        // when
        Thread thread = tf.newThread(NOOP);

        // then
        Assertions.assertThat(thread.getName()).isEqualTo(name + "-" + suffix);
    }

    @Test
    void shouldSuffixAndThrowException_defaultFactory() {
        Assertions.assertThatThrownBy(() -> ThreadFactories.suffixed(null)).isInstanceOf(NullPointerException.class);
    }


    @Test
    void shouldSuffixAndThrowException_customFactory() {
        Assertions.assertThatThrownBy(() -> ThreadFactories.suffixed(null, r -> new Thread())).isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldSuffixThreadName_defaultFactory() {
        // given
        final String suffix = "foo";
        final ThreadFactory tf = ThreadFactories.suffixed(suffix);

        // when
        Thread thread = tf.newThread(NOOP);

        // then
        Assertions.assertThat(thread.getName()).endsWith(suffix);
    }

    @Test
    void shouldPrefixThreadName_customFactory() {
        // given
        final String prefix = "foo";
        final String name = "name";

        ThreadFactory tf = ThreadFactories.prefixed(prefix, r -> new Thread(name));

        // when
        Thread thread = tf.newThread(NOOP);

        // then
        Assertions.assertThat(thread.getName()).isEqualTo(prefix + "-" + name);
    }
}
