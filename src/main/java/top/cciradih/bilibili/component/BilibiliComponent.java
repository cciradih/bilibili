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

    /**
     * {
     *     "code": 0,
     *     "status": true,
     *     "ts": 1626963240,
     *     "data": {
     *         "url": "https://passport.bilibili.com/qrcode/h5/login?oauthKey=8789306c7daf55c7fa7e9b131669d08e",
     *         "oauthKey": "8789306c7daf55c7fa7e9b131669d08e"
     *     }
     * }
     */
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

    /**
     * {
     *     "code": 0,
     *     "msg": "ok",
     *     "message": "ok",
     *     "data": {
     *         "roomid": "5440",
     *         "uid": "9617619",
     *         "content": "本期辩题：“黑红”热度，你想要吗？",
     *         "ctime": "2021-07-15 14:29:06",
     *         "status": "0",
     *         "uname": "哔哩哔哩直播"
     *     }
     * }
     */
    public JSONObject getRoom(String roomId) {
        var uri = URI.create("https://api.live.bilibili.com/room_ex/v1/RoomNews/get?roomid=" + roomId);
        var httpRequest = HTTP_REQUEST_BUILDER
                .GET()
                .uri(uri)
                .build();
        return getBody(httpRequest);
    }

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
