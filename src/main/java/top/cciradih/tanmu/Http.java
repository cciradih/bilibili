package top.cciradih.tanmu;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Http {
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final HttpRequest.Builder HTTP_REQUEST_BUILDER = HttpRequest.newBuilder();

    private Http() {
    }

    static Http getInstance() {
        return HttpHolder.HTTP;
    }

    JSONObject checkLogin(String cookie) {
        HttpRequest httpRequest = HTTP_REQUEST_BUILDER
                .GET()
                .uri(URI.create("https://api.live.bilibili.com/xlive/web-ucenter/user/get_user_info"))
                .header("Cookie", cookie)
                .build();
        try {
            return JSON.parseObject(HTTP_CLIENT.send(httpRequest, HttpResponse.BodyHandlers.ofString()).body());
        } catch (IOException | InterruptedException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    JSONObject getLoginUrl() {
        HttpRequest httpRequest = HTTP_REQUEST_BUILDER
                .GET()
                .uri(URI.create("https://passport.bilibili.com/qrcode/getLoginUrl"))
                .build();
        try {
            return JSON.parseObject(HTTP_CLIENT.send(httpRequest, HttpResponse.BodyHandlers.ofString()).body());
        } catch (IOException | InterruptedException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    JSONObject getLoginInfo(String oauthKey) {
        HttpRequest httpRequest = HTTP_REQUEST_BUILDER
                .POST(HttpRequest.BodyPublishers.ofString("oauthKey=" + oauthKey))
                .uri(URI.create("https://passport.bilibili.com/qrcode/getLoginInfo"))
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .build();
        try {
            return JSON.parseObject(HTTP_CLIENT.send(httpRequest, HttpResponse.BodyHandlers.ofString()).body());
        } catch (IOException | InterruptedException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    JSONObject getRoom(String roomId) {
        HttpRequest httpRequest = HTTP_REQUEST_BUILDER
                .GET()
                .uri(URI.create("https://api.live.bilibili.com/room_ex/v1/RoomNews/get?roomid=" + roomId))
                .build();
        try {
            return JSON.parseObject(HTTP_CLIENT.send(httpRequest, HttpResponse.BodyHandlers.ofString()).body());
        } catch (IOException | InterruptedException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    JSONObject getHost() {
        HttpRequest httpRequest = HTTP_REQUEST_BUILDER
                .GET()
                .uri(URI.create("https://api.live.bilibili.com/room/v1/Danmu/getConf"))
                .build();
        try {
            return JSON.parseObject(HTTP_CLIENT.send(httpRequest, HttpResponse.BodyHandlers.ofString()).body());
        } catch (IOException | InterruptedException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    void sendDanmu(String cookie, String message, String roomId) {
        Matcher matcher = Pattern.compile("bili_jct=(\\w+)").matcher(cookie);
        String csrfToken = "";
        if (matcher.find()) {
            csrfToken = matcher.group(1);
        }
        HttpRequest httpRequest = HTTP_REQUEST_BUILDER
                .POST(HttpRequest.BodyPublishers.ofString("color=1&fontsize=1&mode=1&msg=" + message + "&rnd=1&roomid=" + roomId + "&bubble=0&csrf_token=" + csrfToken + "&csrf=" + csrfToken))
                .uri(URI.create("https://api.live.bilibili.com/msg/send"))
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .header("cookie", cookie)
                .build();
        try {
            HTTP_CLIENT.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.err.println(e.getMessage());
        }
    }

    private static class HttpHolder {
        private static final Http HTTP = new Http();
    }
}
