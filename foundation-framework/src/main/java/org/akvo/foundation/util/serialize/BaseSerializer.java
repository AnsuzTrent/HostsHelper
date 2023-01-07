package org.akvo.foundation.util.serialize;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.cfg.MapperBuilder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

@SuppressWarnings({"rawtypes", "unused"})
public abstract class BaseSerializer<M extends ObjectMapper> {
    protected final String dataType;
    protected final Logger logger;

    protected BaseSerializer(String dataType, Logger logger) {
        this.dataType = dataType;
        this.logger = logger;
    }

    protected <T extends MapperBuilder<M, T>> M configBuilder(Supplier<T> builderSupplier, JsonInclude.Include include) {
        return builderSupplier.get()
            // 允许时区
            .enable(SerializationFeature.WRITE_DATES_WITH_ZONE_ID)
            .disable(SerializationFeature.WRITE_DATES_WITH_CONTEXT_TIME_ZONE)
            .disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
            // 时间类型使用字符串
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .disable(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS)
            .disable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
            // 反序列化时，忽略在字符串中存在但Java 对象实际没有的属性
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            // 忽略大小写
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
            // 小驼峰
            .propertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE)
            .serializationInclusion(Optional.ofNullable(include)
                .orElse(JsonInclude.Include.NON_NULL))
            .findAndAddModules()
            .build();
    }

    public static JavaType buildType(ObjectMapper mapper, Class<?> clazz) {
        return mapper.getTypeFactory()
            .constructType(clazz);
    }

    /**
     * 构造复杂的Collection类型.
     */
    public static JavaType buildCollectionType(ObjectMapper mapper,
                                               Class<? extends Collection> collectionClass,
                                               Class<?> elementClass) {
        return mapper.getTypeFactory()
            .constructCollectionType(collectionClass, elementClass);
    }

    /**
     * 构造复杂的Map类型.
     */
    public static JavaType buildMapType(ObjectMapper mapper,
                                        Class<? extends Map> mapClass,
                                        Class<?> keyClass,
                                        Class<?> valueClass) {
        return mapper.getTypeFactory()
            .constructMapType(mapClass, keyClass, valueClass);
    }

    public M mapper() {
        return mapper(JsonInclude.Include.NON_NULL);
    }

    public abstract M mapper(JsonInclude.Include include);

    /**
     * 将对象序列化为字符串
     * 默认都会转换成小写，如果需要原文返回需要添加JsonIgnore, JsonProperty 注解
     *
     * @param object 对象
     * @param <T>    对象
     * @return 字符串
     */
    public <T> String serialize(T object) {
        return serialize(object, null);
    }

    public <T> String serialize(T object, String dateFormat) {
        return serialize0(object, false, dateFormat);
    }

    public <T> String serializePretty(T object) {
        return serializePretty(object, null);
    }

    public <T> String serializePretty(T object, String dateFormat) {
        return serialize0(object, true, dateFormat);
    }

    /**
     * 将对象序列化为格式化的字符串<br>
     * 默认都会转换成小写，如果需要原文返回需要添加JsonIgnore, JsonProperty 注解
     *
     * @param object 对象
     * @param <T>    对象
     * @return 字符串
     */
    protected <T> String serialize0(T object, boolean needPretty, String dateFormat) {
        if (object == null) {
            return null;
        }
        try {
            M mapper = mapper();
            DateFormat mapperDateFormat = mapper.getDateFormat();
            if (StringUtils.isNotBlank(dateFormat)) {
                mapper.setDateFormat(new SimpleDateFormat(dateFormat));
            }
            ObjectWriter writer = needPretty ? mapper.writerWithDefaultPrettyPrinter() : mapper.writer();
            var valueAsString = writer.writeValueAsString(object);
            mapper.setDateFormat(mapperDateFormat);
            return valueAsString;
        } catch (Exception e) {
            logger.warn("{} when serialize object to {}", e.getClass().getSimpleName(), dataType, e);
        }
        return null;
    }

    /**
     * 将字符串反序列化为对象
     *
     * @param str    字符串
     * @param tClass 对象
     * @param <T>    对象
     * @return 对象
     */
    public <T> T deserialize(String str, Class<T> tClass) {
        return deserialize(str, tClass, null);
    }

    public <T> T deserialize(String str, Class<T> tClass, String dateFormat) {
        M mapper = mapper();
        return deserialize0(mapper, str, buildType(mapper, tClass), dateFormat);
    }

    public <T> T deserialize(String str,
                             Class<? extends Collection> collectionClass,
                             Class<?> elementClass) {
        return deserialize(str, collectionClass, elementClass, null);
    }

    public <T> T deserialize(String str,
                             Class<? extends Collection> collectionClass,
                             Class<?> elementClass,
                             String dateFormat) {
        M mapper = mapper();
        return deserialize0(mapper, str, buildCollectionType(mapper, collectionClass, elementClass), dateFormat);
    }

    public <T> T deserialize(String str,
                             Class<? extends Map> mapClass,
                             Class<?> keyClass,
                             Class<?> valueClass) {
        return deserialize(str, mapClass, keyClass, valueClass, null);
    }

    public <T> T deserialize(String str,
                             Class<? extends Map> mapClass,
                             Class<?> keyClass,
                             Class<?> valueClass,
                             String dateFormat) {
        M mapper = mapper();
        return deserialize0(mapper, str, buildMapType(mapper, mapClass, keyClass, valueClass), dateFormat);
    }

    /**
     * 复杂类型的反序列化
     *
     * @param str      json
     * @param javaType 可以使用buildCollectionType 、buildMapType 进行构建
     * @param <T>      T
     * @return 对象
     */
    protected <T> T deserialize0(M mapper, String str, JavaType javaType, String dateFormat) {
        if (StringUtils.isBlank(str)) {
            return null;
        }
        try {
            DateFormat mapperDateFormat = mapper.getDateFormat();
            if (StringUtils.isNotBlank(dateFormat)) {
                mapper.setDateFormat(new SimpleDateFormat(dateFormat));
            }
            T readValue = mapper.readValue(str, javaType);
            mapper.setDateFormat(mapperDateFormat);
            return readValue;
        } catch (Exception e) {
            logger.warn("{} when deserialize {} string to object", e.getClass().getSimpleName(), dataType, e);
        }
        return null;
    }
}
