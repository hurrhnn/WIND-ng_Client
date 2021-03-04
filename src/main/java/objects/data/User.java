package objects.data;

import org.json.JSONObject;
import utils.JSONHandler;

public class User {
    private final String raw;

    private final String profile;
    private final String name;
    private final int id;

    public User(JSONObject jsonUserObject) {
        JSONHandler jsonHandler = new JSONHandler();
        this.raw = jsonUserObject.toString(4);
        this.profile = jsonHandler.NullableJSONValueHandler(jsonUserObject, "profile") == null ? "NULL" : jsonUserObject.getString("profile");
        this.name = jsonUserObject.getString("name");
        this.id = jsonUserObject.getInt("id");
    }

    public String toString() {
        return this.raw;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getProfile() {
        return profile;
    }
}
