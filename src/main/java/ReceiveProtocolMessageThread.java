import client.Info;
import client.UpdateClientUI;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import objects.data.DMessage;
import objects.data.SelfUser;
import objects.data.User;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;

import javax.swing.*;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

public class ReceiveProtocolMessageThread implements Runnable {

    private final Logger LOGGER = utils.Logger.getLogger(this.getClass());

    WINDWebSocketClient windWebSocketClient;
    private final ArrayBlockingQueue<JSONObject> messageQueue;

    public ReceiveProtocolMessageThread(WINDWebSocketClient windWebSocketClient, ArrayBlockingQueue<JSONObject> messageQueue) {
        this.messageQueue = messageQueue;
        this.windWebSocketClient = windWebSocketClient;
    }

    @Override
    public void run() {
        while (true) {
            try {
                JSONObject jsonObject = messageQueue.take();
                String type = jsonObject.getString("type");
                switch (type) {
                    case "handshake":
                        UpdateClientUI.onUpdate(() -> {
                            Scene scene = UpdateClientUI.globalStage.getScene();
                            try {
                                BorderPane rootPane = FXMLLoader.load(getClass().getResource("root.fxml"));
                                scene = new Scene(rootPane, 1280, 720);
                                UpdateClientUI.globalStage.setScene(scene);
                                ((ScrollPane) UpdateClientUI.globalStage.getScene().lookup("#scr_pane_chat")).setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            windWebSocketClient.selfUser = new SelfUser(jsonObject.getJSONObject("payload").getJSONObject("user_info"));
                            windWebSocketClient.userList.add(new User(new JSONObject(windWebSocketClient.selfUser.toString())));

                            TextArea textArea = (TextArea) scene.lookup("#textarea_input_text");
                            textArea.setFont(new Font(Info.FONT_NAME.getValue(), 15));

                            Text textName = (Text) scene.lookup("#text_name");
                            textName.setText("Name: " + windWebSocketClient.selfUser.getName());
                            textName.setFont(new Font(Info.FONT_NAME.getValue(), 15));

                            Text textId = (Text) scene.lookup("#text_id");
                            textId.setText("Id: " + windWebSocketClient.selfUser.getId());
                            textId.setFont(new Font(Info.FONT_NAME.getValue(), 15));

                            Text text = (Text) scene.lookup("#text_profile");
                            text.setText("Profile: " + windWebSocketClient.selfUser.getProfile());
                            text.setFont(new Font(Info.FONT_NAME.getValue(), 15));

                            JSONArray jsonArray = jsonObject.getJSONObject("payload").getJSONArray("friends");


                            for (int i = 0; i < jsonArray.length(); i++)
                                windWebSocketClient.userList.add(new User(jsonArray.getJSONObject(i)));

                            for (User user : windWebSocketClient.userList) {
                                Button button = new Button();
                                button.setPrefSize(200, 50);
                                button.setId("btn_userid_" + user.getId());
                                button.setText(user.getName().equals(windWebSocketClient.selfUser.getName()) ? "[ME]" + user.getName() : user.getName());
                                button.setFont(new Font(Info.FONT_NAME.getValue(), 15));

                                windWebSocketClient.DMDatabase.put(user.getId(), new ArrayList<>());
                                button.setOnMouseClicked(event -> {
                                    windWebSocketClient.currentVBox = user.getId();
                                    utils.Logger.getLogger(getClass()).debug("Activated " + user.getName() + "User Button.");
                                    VBox vBox_chat = (VBox) UpdateClientUI.globalStage.getScene().lookup("#vbox_chat");
                                    vBox_chat.getChildren().clear();
                                    for (int i = 0; i < windWebSocketClient.DMDatabase.get(user.getId()).size(); i++) {
                                        Text textChat = new Text();
                                        DMessage dMessage = windWebSocketClient.DMDatabase.get(user.getId()).get(i);
                                        BigInteger realId = dMessage.getUserId() == dMessage.getChatId() ? BigInteger.valueOf(dMessage.getChatId()) : BigInteger.valueOf(dMessage.getUserId());
                                        textChat.setText(windWebSocketClient.windObjectGetter.getUserById(realId).getName() + ": " + windWebSocketClient.DMDatabase.get(user.getId()).get(i).getContent());
                                        textChat.setFont(new Font(Info.FONT_NAME.getValue(), 15));
                                        vBox_chat.getChildren().add(textChat);
                                        ((ScrollPane) UpdateClientUI.globalStage.getScene().lookup("#scr_pane_chat")).setVvalue(1);
                                    }
                                });

                                VBox vBox_friends = (VBox) UpdateClientUI.globalStage.getScene().lookup("#vbox_friends");
                                vBox_friends.getChildren().add(button);
                            }
                            UpdateClientUI.globalStage.setTitle("WIND - Logged in as [" + windWebSocketClient.selfUser.getName() + "]");
                            windWebSocketClient.currentVBox = windWebSocketClient.selfUser.getId();
                        });


                        LOGGER.info("You've successfully completed the WebSocket handshake process!");
                        break;

                    case "chat":
                        DMessage dMessage = new DMessage(jsonObject.getJSONObject("payload"));
                        BigInteger realId = dMessage.getUserId() == dMessage.getChatId() ? BigInteger.valueOf(dMessage.getUserId()) : BigInteger.valueOf(dMessage.getUserId());

                        if (realId == windWebSocketClient.selfUser.getId()) {
                            if (!windWebSocketClient.isReceiveFromMe) {
                                windWebSocketClient.isReceiveFromMe = true;
                            } else {
                                windWebSocketClient.isReceiveFromMe = false;
                                continue;
                            }
                        }
                        List<DMessage> beforeDMList = windWebSocketClient.DMDatabase.get(realId);
                        List<DMessage> afterDMList = new ArrayList<>(beforeDMList);
                        afterDMList.add(dMessage);
                        windWebSocketClient.DMDatabase.replace(realId, beforeDMList, afterDMList);

                        if (windWebSocketClient.currentVBox == realId) {
                            Text textChat = new Text();
                            textChat.setText(windWebSocketClient.windObjectGetter.getUserById(BigInteger.valueOf(dMessage.getUserId())).getName() + ": " + dMessage.getContent());
                            textChat.setFont(new Font(Info.FONT_NAME.getValue(), 15));

                            VBox vBox_chat = (VBox) UpdateClientUI.globalStage.getScene().lookup("#vbox_chat");
                            UpdateClientUI.onUpdate(() -> {
                                vBox_chat.getChildren().add(textChat);
                                ((ScrollPane) UpdateClientUI.globalStage.getScene().lookup("#scr_pane_chat")).setVvalue(1);
                            });
                        }
                        break;

                    default:
                        System.out.println(jsonObject.toString(4));
                        break;

                }
            } catch (InterruptedException e) {
                LOGGER.error("An error has occurred!");
                LOGGER.error(e.getClass().getCanonicalName() + ": " + e.getMessage());
                System.exit(1);
            }
        }
    }
}
