package top.cciradih.tanmu;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import javafx.application.Platform;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletionStage;

public class Ws {
    private Controller controller;

    public Ws(Controller controller) {
        this.controller = controller;
    }

    //|封包长度   |头部长度|协议版本|操作码     |常数       |
    //|00 00 00 29|00 10   |00 01   |00 00 00 07|00 00 00 01|
    public void listen() {
        WebSocket webSocket = HttpClient.newHttpClient().newWebSocketBuilder().buildAsync(URI.create("wss://broadcastlv.chat.bilibili.com/sub"), new WebSocketListener(controller)).join();
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                byte[] heartbeatHead = {0x00, 0x00, 0x00, 0x1f, 0x00, 0x10, 0x00, 0x01, 0x00, 0x00, 0x00, 0x02, 0x00, 0x00, 0x00, 0x01};
                byte[] heartbeatBody = "[object Object]".getBytes();
                byte[] heartbeat = Arrays.copyOf(heartbeatHead, heartbeatHead.length + heartbeatBody.length);
                System.arraycopy(heartbeatBody, 0, heartbeat, heartbeatHead.length, heartbeatBody.length);
                ByteBuffer byteBuffer = ByteBuffer.wrap(heartbeat);
                webSocket.sendBinary(byteBuffer, true);
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 30 * 1000);
    }

    private static class WebSocketListener implements WebSocket.Listener {
        private Controller controller;

        WebSocketListener(Controller controller) {
            this.controller = controller;
        }

        @Override
        public void onOpen(WebSocket webSocket) {
            byte[] openHead = {0x00, 0x00, 0x00, 0, 0x00, 0x10, 0x00, 0x01, 0x00, 0x00, 0x00, 0x07, 0x00, 0x00, 0x00, 0x01};
            byte[] openBody = "{\"uid\":0,\"roomid\":7685334}".getBytes();
            byte openLength = (byte) (openHead.length + openBody.length);
            openHead[3] = openLength;
            byte[] open = Arrays.copyOf(openHead, openHead.length + openBody.length);
            System.arraycopy(openBody, 0, open, openHead.length, openBody.length);
            ByteBuffer byteBuffer = ByteBuffer.wrap(open);
            webSocket.sendBinary(byteBuffer, true);
            WebSocket.Listener.super.onOpen(webSocket);
        }

        @Override
        public CompletionStage<?> onBinary(WebSocket webSocket, ByteBuffer data, boolean last) {
            byte[] bytes = new byte[data.capacity()];
            for (int i = 0; i < data.capacity(); i++) {
                bytes[i] = data.get();
            }
            if (bytes[11] == 5) {
                bytes = Arrays.copyOfRange(bytes, 16, bytes.length);
                int start = 0;
                int end = 0;
                for (int i = 0; i < bytes.length; i++) {
                    if (bytes[i] == 0) {
                        end = i;
                        JSONObject jsonObject = JSON.parseObject(new String(Arrays.copyOfRange(bytes, start, end), Charset.forName("UTF-8")));
                        if (jsonObject.getString("cmd").equals("DANMU_MSG")) {
                            getDanmu(jsonObject);
                        }
                        i += 16;
                        start = i;
                    }
                }
                JSONObject jsonObject = JSON.parseObject(new String(Arrays.copyOfRange(bytes, start, bytes.length), Charset.forName("UTF-8")));
                if (jsonObject.getString("cmd").equals("DANMU_MSG")) {
                    getDanmu(jsonObject);
                }
            }
            return WebSocket.Listener.super.onBinary(webSocket, data, last);
        }

        private void getDanmu(JSONObject jsonObject) {
            String danmu = jsonObject.getJSONArray("info").getString(1);
            String user = jsonObject.getJSONArray("info").getJSONArray(2).getString(1);
            addDanmuListItem(user + " : " + danmu);
        }

        private void addDanmuListItem(String danmu) {
            Platform.runLater(() -> controller.addDanmuListItem(danmu));
        }
    }
}
