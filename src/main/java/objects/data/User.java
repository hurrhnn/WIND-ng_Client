package objects.data;

import org.json.JSONObject;
import utils.JSONHandler;

import java.math.BigInteger;

public class User {
    private final String raw;

    private final String profile;
    private final String name;
    private final BigInteger id;

    public User(JSONObject jsonUserObject) {
        JSONHandler jsonHandler = new JSONHandler();

        this.raw = jsonUserObject.toString(4);
        this.profile = jsonHandler.NullableJSONValueHandler(jsonUserObject, "profile") == null ? "NULL" : jsonUserObject.getString("profile");
        this.name = jsonUserObject.getString("name");
        this.id = new BigInteger(jsonUserObject.getString("id"));
    }

    public String toString() {
        return this.raw;
    }

    public String getName() {
        return name;
    }

    public BigInteger getId() {
        return id;
    }

    public String getProfile() {
        return profile;
    }
}
