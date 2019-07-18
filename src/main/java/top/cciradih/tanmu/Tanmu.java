package top.cciradih.tanmu;

import com.alibaba.fastjson.JSONObject;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tanmu extends Application {
    private static final Cookie COOKIE = new Cookie();
    private static final Http HTTP = new Http();
    private static final QrCode QR_CODE = new QrCode();

    private boolean isLogin = false;
    private String oauthKey = "";

    @Override
    public void init() {
        String cookie = COOKIE.checkFile();
        JSONObject jsonObject = HTTP.checkLogin(cookie);
        if (jsonObject.getIntValue("code") == -101) {
            jsonObject = HTTP.getLoginUrl();
            jsonObject = jsonObject.getJSONObject("data");
            this.oauthKey = jsonObject.getString("oauthKey");
            String url = jsonObject.getString("url");
            QR_CODE.generateQrCode(url);
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
        primaryStage.setAlwaysOnTop(true);
        primaryStage.setResizable(false);
        primaryStage.show();
        Controller controller = fxmlLoader.getController();
        if (isLogin) {
            controller.setDanmuListVisible(true);
            controller.setDanmuInputVisible(true);
            controller.setDanmuListItems();
            Ws ws = new Ws(controller);
            ws.listen();
        } else {
            controller.setQrCodeImage();
            Timer timer = new Timer();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    JSONObject jsonObject = HTTP.getLoginInfo(oauthKey);
                    if (jsonObject.getBooleanValue("status")) {
                        String url = jsonObject.getJSONObject("data").getString("url");
                        URI uri = URI.create(url);
                        String cookie = uri.getQuery().replace('&', ';');
                        COOKIE.writeToFile(cookie);
                        timer.cancel();
                        timer.purge();
                    }
                }
            };
            timer.scheduleAtFixedRate(timerTask, 0, 3 * 1000);
        }
    }
}
