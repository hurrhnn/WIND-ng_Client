import java.util.Scanner;

public class SendProtocolMessageThread extends Thread {

    WINDWebSocketClient windWebSocketClient;

    public SendProtocolMessageThread(WINDWebSocketClient windWebSocketClient) {
        this.windWebSocketClient = windWebSocketClient;
    }

    @Override
    public void run() {
        Scanner scanner = windWebSocketClient.scanner;
        while (!windWebSocketClient.isClosed()) {
            StringBuilder stringBuilder = new StringBuilder();
            do {
                stringBuilder.append(scanner.nextLine());
                stringBuilder.append("\n");
            }
            while (!stringBuilder.toString().contains(";"));

            try {
                stringBuilder.replace(stringBuilder.length() - 1, stringBuilder.length(), "");
                if (stringBuilder.substring(0, 4).equalsIgnoreCase("chat")) {
                    int userId = Integer.parseInt(stringBuilder.substring(6).substring(0, stringBuilder.substring(6).indexOf(",")));
                    String message = stringBuilder.substring(6).substring(stringBuilder.substring(6).indexOf(",") + 2).replace(";", "");

                    windWebSocketClient.send("{\n" +
                            "    \"type\": \"chat\",\n" +
                            "    \"payload\": {\n" +
                            "        \"type\": \"send\",\n" +
                            "        \"chat_id\": " + userId + ",\n" +
                            "        \"content\": \"" + message + "\"\n" +
                            "    }\n" +
                            "}");
                } else {
                    windWebSocketClient.send(stringBuilder.toString().replace(";", ""));
                }
            } catch (Exception ignored) {
                System.out.println("E: Type Input Error!");
            }
        }
    }
}
