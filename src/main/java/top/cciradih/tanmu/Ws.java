package top.cciradih.tanmu;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

class Ws {
    private static final byte[] HEARTBEAT_HEAD = {0x00, 0x00, 0x00, 0, 0x00, 0x10, 0x00, 0x00, 0x00, 0x00, 0x00, 0x02, 0x00, 0x00, 0x00, 0x00};
    private static final byte[] HEARTBEAT_BODY = "[object Object]".getBytes();
    private static Controller controller;
    private static WebSocket webSocket;
    private static Timer timer;

    private Ws() {
    }

    static Ws getInstance() {
        return WsHolder.ws;
    }

    static Ws getInstance(Controller controller) {
        Ws.controller = controller;
        return WsHolder.ws;
    }

    void listen() {
        webSocket = HttpClient.newHttpClient().newWebSocketBuilder().buildAsync(URI.create("wss://" + Http.getInstance().getHost().getJSONObject("data").getString("host") + "/sub"), WsListener.getInstance(controller)).join();
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                ByteBuffer byteBuffer = ByteBuffer.wrap(generatePacket(HEARTBEAT_HEAD, HEARTBEAT_BODY));
                webSocket.sendBinary(byteBuffer, true);
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 30 * 1000);
    }

    void stop() {
        timer.cancel();
    }

    byte[] generatePacket(byte[] head, byte[] body) {
        byte packetLength = (byte) (head.length + body.length);
        head[3] = packetLength;
        byte[] packet = Arrays.copyOf(head, head.length + body.length);
        System.arraycopy(body, 0, packet, head.length, body.length);
        return packet;
    }

    private static class WsHolder {
        private static final Ws ws = new Ws();
    }
}
