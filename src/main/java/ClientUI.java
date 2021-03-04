import client.Info;
import client.UpdateClientUI;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.slf4j.Logger;

public class ClientUI extends Application implements Runnable {

    private final Logger LOGGER = utils.Logger.getLogger(this.getClass());

    @Override
    public void run() {
        launch();
    }

    @Override
    public void start(Stage primaryStage) {
        Font.loadFont(getClass().getResourceAsStream("BMJUA_ttf.ttf"), 16);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Please Wait...");

        Label label = new Label("Loading...");
        label.setFont(new Font(Info.FONT_NAME.getValue(), 100));
        label.setPrefSize(1280, 720);
        label.setAlignment(Pos.CENTER);

        primaryStage.setScene(new Scene(new Pane(label), 1280, 720));
        primaryStage.show();

        UpdateClientUI.globalStage = primaryStage;
        LOGGER.info("JavaFX Application Started.");
    }

    @Override
    public void stop() {
        LOGGER.info("JavaFX Application Terminated, Shutdown the Program.");
        System.exit(0);
    }
}
