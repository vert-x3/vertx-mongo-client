package io.vertx.ext.mongo;

import io.vertx.core.json.JsonObject;

import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * Index builder
 * @author <a href="mailto:sergey.kobec@gmail.com">Sergey Kobets</a>
 */
public class Indexes {
    private Indexes() {
    }

    public static JsonObject ascending(List<String> fieldNames) {
        requireNonNull(fieldNames, "fieldNames cannot be null");
        JsonObject jsonObject;
        return compoundIndex(fieldNames, 1);
    }

    private static JsonObject compoundIndex(List<String> fieldNames, Object value) {
        JsonObject document = new JsonObject();
        for (String fieldName : fieldNames) {
            document.put(fieldName, value);
        }
        return document;
    }

}
