package top.cciradih.bilibili.application;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import top.cciradih.bilibili.component.BilibiliComponent;
import top.cciradih.bilibili.component.QrCodeComponent;
import top.cciradih.bilibili.controller.Controller;

import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;

public class MainApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        var location = getClass().getResource("/application.fxml");
        var fxmlLoader = new FXMLLoader(location);
        var pane = (Pane) fxmlLoader.load();
        var scene = new Scene(pane);
        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.UTILITY);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.show();

        var bilibiliComponent = new BilibiliComponent();
        var loginUrl = bilibiliComponent.getLoginUrl();
        var url = loginUrl.getJSONObject("data").getString("url");
        var qrCodeComponent = new QrCodeComponent();
        var bufferedImage = qrCodeComponent.generateQrCode(url);
        var writableImage = SwingFXUtils.toFXImage(bufferedImage, null);
        var controller = (Controller) fxmlLoader.getController();
        controller.setQrCodeImage(writableImage);
        var oauthKey = loginUrl.getJSONObject("data").getString("oauthKey");
        var time = new Timer();
        var timerTask = new TimerTask() {
            @Override
            public void run() {
                var loginInfo = bilibiliComponent.getLoginInfo(oauthKey);
                var isLogin = loginInfo.getBooleanValue("status");
                if (isLogin) {
                    var url = loginInfo.getJSONObject("data").getString("url");
                    var uri = URI.create(url);
                    var cookie = uri.getQuery().replace('&', ';');
                    System.out.println(cookie);
                    time.cancel();
                }
            }
        };
        time.scheduleAtFixedRate(timerTask, 0, 3000L);
    }
}