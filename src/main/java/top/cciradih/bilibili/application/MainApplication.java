package top.cciradih.bilibili.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainApplication extends Application {
    @Override
    public void init() throws Exception {
        super.init();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        var url = getClass().getResource("/application.fxml");
        var fxmlLoader = new FXMLLoader(url);
        var pane = (Pane) fxmlLoader.load();
        var scene = new Scene(pane);
        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.UTILITY);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.show();
    }
}
