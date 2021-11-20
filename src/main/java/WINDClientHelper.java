import objects.data.User;

import java.math.BigInteger;

public class WINDClientHelper {
    private final WINDWebSocketClient windWebSocketClient;
    public WINDClientHelper(WINDWebSocketClient windWebSocketClient) {
        this.windWebSocketClient = windWebSocketClient;
    }

    public User getUserById(BigInteger id) {
        for(User user : windWebSocketClient.userList) {
            if(id.toString().equals(user.getId().toString()))
                return user;
        }
        return null;
    }
}
