package objects.data;

import org.json.JSONObject;

public class DMessage {
    private final String raw;

    private final int user_id;
    private final String type;
    private final String content;
    private final int chat_id;

    public DMessage(JSONObject jsonDMObject) {
        this.raw = jsonDMObject.toString(4);
        this.user_id = jsonDMObject.getInt("user_id");
        this.type = jsonDMObject.getString("type");
        this.content = jsonDMObject.getString("content");
        this.chat_id = jsonDMObject.getInt("chat_id");
    }


    public String toString() {
        return raw;
    }

    public int getUserId() {
        return user_id;
    }

    public String getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public int getChatId() {
        return chat_id;
    }
}
