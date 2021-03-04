package utils;

import org.json.JSONObject;

public class JSONHandler {

    public String NoneableJSONValueHandler(JSONObject jsonObject, String key) {
        return (!jsonObject.has(key)) ? null : NullableJSONValueHandler(jsonObject, key);
    }

    public JSONObject NoneableJSONObjectHandler(JSONObject jsonObject, String key) {
        return (!jsonObject.has(key)) ? null : NullableJSONObjectHandler(jsonObject, key);
    }

    public String NullableJSONValueHandler(JSONObject jsonObject, String key) {
        return (jsonObject.get(key) == JSONObject.NULL) ? null : (String) jsonObject.get(key);
    }

    public JSONObject NullableJSONObjectHandler(JSONObject jsonObject, String key) {
        return (jsonObject.get(key) == JSONObject.NULL) ? null : jsonObject.getJSONObject(key);
    }
}

