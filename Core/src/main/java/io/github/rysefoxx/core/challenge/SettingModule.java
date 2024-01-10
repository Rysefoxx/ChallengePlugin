package io.github.rysefoxx.core.challenge;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;


/**
 * @author Rysefoxx
 * @since 06.01.2024
 */
@Getter
public class SettingModule<T> {

    private final String type;
    @Setter
    private String key;
    @Setter
    private T value;

    /**
     * Creates a new setting module with the given key and value
     *
     * @param key   The key of the setting
     * @param value The value of the setting
     */
    public SettingModule(@NotNull String key, @NotNull T value) {
        this.key = key;
        this.value = value;
        this.type = value.getClass().getName();
    }

    /**
     * Saves the setting as json
     *
     * @return The json string
     */
    public @NotNull String toJson() {
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("key", key);
        jsonObject.addProperty("type", type);
        jsonObject.add("value", gson.toJsonTree(value));
        return jsonObject.toString();
    }

    /**
     * Loads a setting from json
     *
     * @param json The json string
     * @return The setting
     * @throws ClassNotFoundException If the class of the setting could not be found
     */
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