package objects.data;

import org.json.JSONObject;

import java.math.BigInteger;

public class SelfUser extends User{

    public SelfUser(JSONObject jsonUserObject) {
        super(jsonUserObject);
    }

    public String toString() {
        return super.toString();
    }

    public String getName() {
        return super.getName();
    }

    public BigInteger getId() {
        return super.getId();
    }

    public String getProfile() {
        return super.getProfile();
    }
}
