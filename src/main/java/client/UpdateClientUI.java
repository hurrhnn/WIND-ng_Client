package client;

import javafx.application.Platform;
import javafx.stage.Stage;

public class UpdateClientUI {
    public static Stage globalStage;

    public static void onUpdate(Runnable runnable) throws InterruptedException {
        Platform.runLater(runnable);
    }
}