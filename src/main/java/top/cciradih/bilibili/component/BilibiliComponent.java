package top.cciradih.bilibili.component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class BilibiliComponent {
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final HttpRequest.Builder HTTP_REQUEST_BUILDER = HttpRequest.newBuilder();
    private static final HttpResponse.BodyHandler<String> BODY_HANDLER = HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8);

    public JSONObject getLoginUrl() {
        var uri = URI.create("https://passport.bilibili.com/qrcode/getLoginUrl");
        var httpRequest = HTTP_REQUEST_BUILDER
                .GET()
                .uri(uri)
                .build();
        return getBody(httpRequest);
    }

    public JSONObject getLoginInfo(String oauthKey) {
        var bodyPublisher = HttpRequest.BodyPublishers.ofString("oauthKey=" + oauthKey);
        var uri = URI.create("https://passport.bilibili.com/qrcode/getLoginInfo");
        var httpRequest = HTTP_REQUEST_BUILDER
                .POST(bodyPublisher)
                .uri(uri)
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .build();
        return getBody(httpRequest);
    }

//    public JSONObject getRoom

    private JSONObject getBody(HttpRequest httpRequest) {
        String body = null;
        try {
            body = HTTP_CLIENT.send(httpRequest, BODY_HANDLER).body();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return JSON.parseObject(body);
    }
}
