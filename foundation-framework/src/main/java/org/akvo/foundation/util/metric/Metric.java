package org.akvo.foundation.util.metric;

import io.micrometer.core.instrument.*;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class Metric {
    private static final Map<String, Meter> operator = new ConcurrentHashMap<>();
    private static final CompositeMeterRegistry registry = Metrics.globalRegistry;
    private static final List<Tag> metricTags = new ArrayList<>();
    private static final double[] percent = {0.50, 0.75, 0.90, 0.95, 0.99, 0.999, 0.9999};
    private String desc;

    protected Metric() {
    }

    public static Metric create() {
        return new Metric();
    }

    public static Metric create(String desc) {
        Metric metric = create();
        metric.setDesc(desc);
        return metric;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Meter> T meter(String name, Class<T> clazz, Supplier<T> ifNotExists) {
        return (T) operator.computeIfAbsent("%s|%s".formatted(name, clazz.getSimpleName()),
            k -> ifNotExists.get());
    }

    private static Gauge gauge(String name, Supplier<Number> supplier, String desc) {
        return meter(name, Gauge.class,
            () -> Gauge.builder(name, supplier)
                .tags(metricTags)
                .description(desc)
                .register(registry));
    }

    private static Counter counter(String name, String desc) {
        return meter(name, Counter.class,
            () -> Counter.builder(name)
                .tags(metricTags)
                .description(desc)
                .register(registry));
    }

    private static Timer timer(String name, String desc) {
        return meter(name, Timer.class,
            () -> Timer.builder(name)
                .tags(metricTags)
                .description(desc)
                .publishPercentiles(percent)
                .publishPercentileHistogram()
                .register(registry));
    }

    private static DistributionSummary summary(String name, String desc) {
        return meter(name, DistributionSummary.class,
            () -> DistributionSummary.builder(name)
                .tags(metricTags)
                .description(desc)
                .publishPercentiles(percent)
                .publishPercentileHistogram()
                .register(registry));
    }

    private void setDesc(String desc) {
        this.desc = desc;
    }

    public Metric withTag(String key, String value) {
        metricTags.add(new ImmutableTag(key, value));
        return this;
    }

    public Metric withTags(Map<String, String> tags) {
        tags.forEach((k, v) -> metricTags.add(new ImmutableTag(k, v)));
        return this;
    }

    public Metric withTags(List<Tag> tags) {
        metricTags.addAll(tags);
        return this;
    }

    public void addGauge(String name, Supplier<Number> supplier) {
        gauge(name, supplier, desc)
            .measure();
    }

    public void recordOne(String name) {
        recordMany(name, 1L);
    }

    public void recordOne(String name, Instant beginTime) {
        recordOne(name, beginTime, Instant.now());
    }

    public void recordOne(String name, Instant beginTime, Instant endTime) {
        recordOne(name, Duration.between(beginTime, endTime).toMillis());
    }

    public void recordOne(String name, long millis) {
        timer(name, desc)
            .record(millis, TimeUnit.MILLISECONDS);
    }

    public void recordSize(String name, long count) {
        summary(name, desc)
            .record(count);
    }

    public void recordMany(String name, long count) {
        counter(name, desc)
            .increment(count);
    }
}
