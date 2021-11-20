import client.UpdateClientUI;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import org.json.JSONObject;

import java.util.Scanner;

public class SendProtocolMessageThread extends Thread {

    WINDWebSocketClient windWebSocketClient;

    public SendProtocolMessageThread(WINDWebSocketClient windWebSocketClient) {
        this.windWebSocketClient = windWebSocketClient;
    }

    @Override
    public void run() {
        Scanner scanner = windWebSocketClient.scanner;
        new Thread(() -> { // CLI
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
                        long userId = Long.parseLong(stringBuilder.substring(6).substring(0, stringBuilder.substring(6).indexOf(",")));
                        String message = stringBuilder.substring(6).substring(stringBuilder.substring(6).indexOf(",") + 2).replace(";", "");

                        windWebSocketClient.send("{\n" +
                                "    \"type\": \"chat\",\n" +
                                "    \"payload\": {\n" +
                                "        \"type\": \"send\",\n" +
                                "        \"chat_id\": " + userId + ",\n" +
                                "        \"content\": \"" + message + "\"\n" +
                                "    }\n" +
                                "}");
                    } else
                        windWebSocketClient.send(stringBuilder.toString().replace("\n\n", "").replace(";", ""));
                } catch (Exception ignored) {}
            }
        }).start();

        while (true) // Waiting until toolkit initializes.
            if (windWebSocketClient.currentVBox.toString().equals("0")) break;

        // GUI
        TextArea textArea = (TextArea) UpdateClientUI.globalStage.getScene().lookup("#textarea_input_text");
        textArea.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && !(textArea.getText().trim().isEmpty())) {
                String rawPayload = "{\n" +
                        "    \"type\": \"chat\",\n" +
                        "    \"payload\": {\n" +
                        "        \"type\": \"send\",\n" +
                        "        \"chat_id\": " + windWebSocketClient.currentVBox.toString() + ",\n" +
                        "    }\n" +
                        "}";
                JSONObject jsonObject = new JSONObject(rawPayload);
                jsonObject.getJSONObject("payload").accumulate("content", textArea.getText().substring(0, textArea.getText().length() - 1));
                windWebSocketClient.send(jsonObject.toString());
                textArea.setText("");
            }
        });
    }
}
