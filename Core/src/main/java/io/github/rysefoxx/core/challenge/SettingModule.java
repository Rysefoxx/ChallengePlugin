package io.github.rysefoxx.core.challenge;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;


/**
 * @author Rysefoxx
 * @since 06.01.2024
 */
@Getter
public class SettingModule<T> {

    private final String key;
    private final T value;
    private final String type;

    public SettingModule(String key, T value) {
        this.key = key;
        this.value = value;
        this.type = value.getClass().getName();
    }

    public @NotNull String toJson() {
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("key", key);
        jsonObject.addProperty("type", type);
        jsonObject.add("value", gson.toJsonTree(value));
        return jsonObject.toString();
    }

    public static SettingModule<?> fromJson(@NotNull String json) throws ClassNotFoundException {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
        String key = jsonObject.get("key").getAsString();
        String type = jsonObject.get("type").getAsString();
        Class<?> clazz = Class.forName(type);
        Object value = gson.fromJson(jsonObject.get("value"), clazz);
        return new SettingModule<>(key, value);
    }
}