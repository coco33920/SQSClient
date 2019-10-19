package fr.stfc.quizz.client;

import com.jpro.webapi.JProApplication;
import fr.colin.stfc.quizzapi.QuizzAPI;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SQSClient extends JProApplication {


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/sample.fxml"));
        Scene scene = null;
        Thread autoMajThread = new Thread(new AutoMAJ(QuizzAPI.DEFAULT_INSTANCE));
        autoMajThread.start();
        try {
            Parent root = loader.load();
            SQSClientController clientController = loader.getController();
            clientController.init(this);
            scene = new Scene(root);
            scene.getStylesheets().add("/css/main.css");
        } catch (Exception e) {
            e.printStackTrace();
        }
        primaryStage.setOnCloseRequest(event -> {
            Platform.exit();
        });
        primaryStage.setTitle("SQS Client");
        primaryStage.setScene(scene);
        primaryStage.setHeight(720);
        primaryStage.setWidth(1280);
        primaryStage.show();

    }

}
