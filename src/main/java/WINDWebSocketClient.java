import javafx.application.Platform;
import objects.data.DMessage;
import objects.data.SelfUser;
import objects.data.User;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;
import org.slf4j.Logger;

import java.math.BigInteger;
import java.net.URI;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

public class WINDWebSocketClient extends org.java_websocket.client.WebSocketClient {

    private final Logger LOGGER = utils.Logger.getLogger(this.getClass());

    final public URI serverUri;
    private final String token;
    Scanner scanner;

    private ArrayBlockingQueue<JSONObject> messageQueue;
    Thread sendProtocolMessageThread = null;

    volatile public BigInteger currentVBox = BigInteger.valueOf(0);
    volatile public boolean isReceiveFromMe = false;

    public WINDWebSocketClient(URI serverUri, Scanner scanner, String token) {
        super(serverUri);
        this.serverUri = this.getURI();
        this.scanner = scanner;
        this.token = token;
    }

    public SelfUser selfUser;
    public List<User> userList = new ArrayList<>();

    public Map<BigInteger, List<DMessage>> DMDatabase = new HashMap<>();
    WINDClientHelper windObjectGetter = new WINDClientHelper(this);

    @Override
    public void onOpen(ServerHandshake handshake) {
        LOGGER.info("WebSocket Opened: " + handshake.getHttpStatus() + ", " + handshake.getHttpStatusMessage());
        onAuth(this);

        ClientUI clientUI = new ClientUI();
        Thread uiThread = new Thread(clientUI);
        uiThread.setDaemon(true);
        uiThread.start();

        Platform.startup(() -> {});
        messageQueue = new ArrayBlockingQueue<>(100);
        ReceiveProtocolMessageThread receiveProtocolMessageThread = new ReceiveProtocolMessageThread(this, messageQueue);

        new Thread(() -> {
            try {
                Thread.sleep(5000);
                if (selfUser == null) {
                    LOGGER.error("Timeout: Unable to authenticate with server, Shutdown the Program.");
                    System.exit(1);
                }
            } catch (InterruptedException e) {
                onError(e);
            }
        }).start();
        new Thread(receiveProtocolMessageThread).start();
        sendProtocolMessageThread = new SendProtocolMessageThread(this);
        sendProtocolMessageThread.start();
    }

    @Override
    public void onMessage(String message) {
        try {
            JSONObject jsonObject = new JSONObject(message);
            messageQueue.put(jsonObject);
        } catch (InterruptedException e) {
            onError(e);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        LOGGER.error("WebSocket Closed: " + code + ", " + (reason.isEmpty() ? "Shutdown the Program." : (reason + ", Shutdown the Program.")));
        System.exit(0);
    }

    @Override
    public void onError(Exception e) {
        LOGGER.error("An error has occurred!");
        LOGGER.error(e.getClass().getCanonicalName() + ": " + e.getMessage());
        System.exit(1);
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
