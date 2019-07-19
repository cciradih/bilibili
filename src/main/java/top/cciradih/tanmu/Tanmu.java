package top.cciradih.tanmu;

import com.alibaba.fastjson.JSONObject;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;

public class Tanmu extends Application {
    private boolean isLogin = false;
    private String oauthKey = "";

    @Override
    public void init() {
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

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/tanmu.fxml"));
        Pane root = fxmlLoader.load();
        Scene value = new Scene(root);
        primaryStage.setScene(value);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setTitle("Tanmu");
        primaryStage.show();
        Rectangle2D rectangle2D = Screen.getPrimary().getBounds();
        primaryStage.setY(rectangle2D.getHeight() - primaryStage.getHeight() - 30);
        primaryStage.setX(rectangle2D.getWidth() - primaryStage.getWidth());

        Controller controller = fxmlLoader.getController();
        if (isLogin) {
            controller.setDanmuListVisible(true);
            controller.setDanmuInputVisible(true);
            controller.setDanmuListItems();
            Ws.getInstance(controller).listen();
        } else {
            controller.setQrCodeImage();
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
                        controller.setDanmuListVisible(true);
                        controller.setDanmuInputVisible(true);
                        controller.setDanmuListItems();
                        Ws.getInstance(controller).listen();
                        timer.cancel();
                    }
                }
            };
            timer.scheduleAtFixedRate(timerTask, 0, 3 * 1000);
        }
    }

    @Override
    public void stop() {
        Ws.getInstance().stop();
    }
}
