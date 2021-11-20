package objects.data;

import org.json.JSONObject;

import java.math.BigInteger;

public class DMessage {
    private final String raw;

    private final BigInteger user_id;
    private final String type;
    private final String content;
    private final BigInteger chat_id;

    public DMessage(JSONObject jsonDMObject) {
        this.raw = jsonDMObject.toString(4);
        this.user_id = new BigInteger(jsonDMObject.getString("user_id"));
        this.type = jsonDMObject.getString("type");
        this.content = jsonDMObject.getString("content");
        this.chat_id = new BigInteger(jsonDMObject.getString("chat_id"));
    }


    public String toString() {
        return raw;
    }

    public BigInteger getUserId() {
        return user_id;
    }

    public String getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public BigInteger getChatId() {
        return chat_id;
    }
}
