package top.cciradih.tanmu;

import com.alibaba.fastjson.JSONObject;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;

public class Tanmu extends Application {
    private boolean isLogin = false;
    private String oauthKey = "";

    @Override
    public void init() {
        checkLogin();
    }

    private void checkLogin() {
        JSONObject jsonObject = Http.getInstance().checkLogin(Cookie.getInstance().checkFile());
        if (jsonObject.getIntValue("code") == -101) {
            jsonObject = Http.getInstance().getLoginUrl();
            jsonObject = jsonObject.getJSONObject("data");
            this.oauthKey = jsonObject.getString("oauthKey");
            QrCode.getInstance().generateQrCode(jsonObject.getString("url"));
        } else {
            this.isLogin = true;
        }
    }

    private void setScene(Stage primaryStage, FXMLLoader fxmlLoader) throws IOException {
        Pane root = fxmlLoader.load();
        Scene value = new Scene(root);
        primaryStage.setScene(value);
    }

    private void setStage(Stage primaryStage) {
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setOpacity(0.5);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.setTitle("Tanmu");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));
    }

    private void setStageLocation(Stage primaryStage) {
        Rectangle2D rectangle2D = Screen.getPrimary().getBounds();
        primaryStage.setY(rectangle2D.getHeight() - primaryStage.getHeight() - 40);
        primaryStage.setX(rectangle2D.getWidth() - primaryStage.getWidth());
    }

    private void showDanmu(Controller controller) {
        controller.setPopularityVisible(true);
        controller.setRoomIdVisible(true);
        controller.setDanmuListVisible(true);
        controller.setDanmuInputVisible(true);
        controller.setDanmuListItems();
        Ws.getInstance(controller).listen();
    }

    private void getCookie(Controller controller) {
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                JSONObject jsonObject = Http.getInstance().getLoginInfo(oauthKey);
                if (jsonObject.getBooleanValue("status")) {
                    String url = jsonObject.getJSONObject("data").getString("url");
                    URI uri = URI.create(url);
                    String cookie = uri.getQuery().replace('&', ';');
                    Cookie.getInstance().writeToFile(cookie);
                    controller.setQrCodeVisible(false);
                    showDanmu(controller);
                    timer.cancel();
                }
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 3 * 1000);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/tanmu.fxml"));
        setScene(primaryStage, fxmlLoader);
        setStage(primaryStage);
        primaryStage.show();
        setStageLocation(primaryStage);
        Controller controller = fxmlLoader.getController();
        if (isLogin) {
            showDanmu(controller);
        } else {
            controller.setQrCodeImage(new Image(new FileInputStream("qrCode")));
            controller.setQrCodeVisible(true);
            getCookie(controller);
        }
    }

    @Override
    public void stop() {
        Ws.getInstance().stop();
    }
}
