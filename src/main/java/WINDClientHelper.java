import objects.data.User;

public class WINDClientHelper {
    private final WINDWebSocketClient windWebSocketClient;
    public WINDClientHelper(WINDWebSocketClient windWebSocketClient) {
        this.windWebSocketClient = windWebSocketClient;
    }

    public User getUserById(int id) {
        for(User user : windWebSocketClient.userList) {
            if(id == user.getId())
                return user;
        }
        return null;
    }
}
