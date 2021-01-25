import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.net.URI;
import java.util.Scanner;

public class WINDWebSocketClient extends org.java_websocket.client.WebSocketClient {

    final public URI serverUri;
    Thread sendProtocolMessageThread = null;
    Scanner scanner;
    String token;

    public WINDWebSocketClient(URI serverUri, Scanner scanner, String token) {
        super(serverUri);
        this.serverUri = this.getURI();
        this.scanner = scanner;
        this.token = token;
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        System.out.println("WebSocket Opened: " + handshake.getHttpStatus() + ", " + handshake.getHttpStatusMessage());
        onAuth(this);

        sendProtocolMessageThread = new SendProtocolMessageThread(this);
        sendProtocolMessageThread.start();
    }

    @Override
    public void onMessage(String message) {
        JSONObject jsonObject = new JSONObject(message);
        System.out.println(jsonObject.toString(4));
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("WebSocket Closed: " + code + ", " + reason);
    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
    }

    private void onAuth(WINDWebSocketClient windWebSocketClient) {
        windWebSocketClient.send("{\n" +
                "    \"type\": \"handshake\",\n" +
                "    \"payload\": {\n" +
                "        \"auth\": " + "\"" + token + "\"" + "\n" +
                "    }\n" +
                "}");
    }
}
