package org.akvo.foundation.ioc;

import org.akvo.foundation.util.metric.Metric;

import java.io.IOException;
import java.time.Instant;

public class Initiation {
    private Initiation() {
    }

    /**
     * @param clazz 启动初始类
     */

    public static void run(Class<?> clazz, Runnable doOnSuccess) {
        Instant beginTime = Instant.now();

        try {
            ApplicationContext.run(clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 发送成功信号
        Metric.create("Application init")
            .recordOne("foundation.start", beginTime);
        doOnSuccess.run();
    }
}
