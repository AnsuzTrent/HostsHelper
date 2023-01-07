package org.akvo.foundation.util.serialize.datatype;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.akvo.foundation.util.serialize.BaseSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JsonSerializer extends BaseSerializer<JsonMapper> {
    public static final JsonSerializer INSTANCE;
    private static final Logger LOGGER;

    static {
        LOGGER = LoggerFactory.getLogger(JsonSerializer.class);
        INSTANCE = new JsonSerializer();
    }

    private JsonSerializer() {
        super("json", LOGGER);
    }

    @Override
    public JsonMapper mapper(JsonInclude.Include include) {
        return configBuilder(JsonMapper::builder, include);
    }

}
