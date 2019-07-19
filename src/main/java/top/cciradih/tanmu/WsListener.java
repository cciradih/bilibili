package top.cciradih.tanmu;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import javafx.application.Platform;

import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletionStage;

final class WsListener implements WebSocket.Listener {
    private static final byte[] OPEN_HEAD = {0x00, 0x00, 0x00, 0, 0x00, 0x10, 0x00, 0x01, 0x00, 0x00, 0x00, 0x07, 0x00, 0x00, 0x00, 0x01};
    private static final byte[] OPEN_BODY = ("{\"uid\":0,\"roomid\":" + Live.ROOM_ID.getValue() + "}").getBytes();
    private static Controller controller;

    private WsListener() {
    }

    static WsListener getInstance(Controller controller) {
        WsListener.controller = controller;
        return WsListenerHolder.wsListener;
    }

    @Override
    public void onOpen(WebSocket webSocket) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(Ws.getInstance().generatePacket(OPEN_HEAD, OPEN_BODY));
        webSocket.sendBinary(byteBuffer, true);
        WebSocket.Listener.super.onOpen(webSocket);
    }

    @Override
    public CompletionStage<?> onBinary(WebSocket webSocket, ByteBuffer data, boolean last) {
        byte[] bytes = new byte[data.remaining()];
        data.get(bytes, 0, bytes.length);
        if (bytes[11] == 5) {
            List<String> danmuList = getDanmuList(bytes);
            danmuList.forEach(danmu -> {
                checkType(JSON.parseObject(danmu));
            });
        }
        return WebSocket.Listener.super.onBinary(webSocket, data, last);
    }

    private List<String> getDanmuList(byte[] bytes) {
        bytes = Arrays.copyOfRange(bytes, 16, bytes.length);
        int start = 0;
        int end = 0;
        List<String> danmuJson = new ArrayList<>();
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] == 0) {
                end = i;
                danmuJson.add(new String(Arrays.copyOfRange(bytes, start, end), Charset.forName("UTF-8")));
                i += 16;
                start = i;
            }
        }
        danmuJson.add(new String(Arrays.copyOfRange(bytes, start, bytes.length), Charset.forName("UTF-8")));
        return danmuJson;
    }

    private void checkType(JSONObject jsonObject) {
        if (jsonObject.getString("cmd").equals("DANMU_MSG")) {
            getDanmu(jsonObject);
        }
    }

    private void getDanmu(JSONObject jsonObject) {
        String danmu = jsonObject.getJSONArray("info").getString(1);
        String user = jsonObject.getJSONArray("info").getJSONArray(2).getString(1);
        addDanmuListItem(user + " : " + danmu);
    }

    private void addDanmuListItem(String danmu) {
        Platform.runLater(() -> controller.addDanmuListItem(danmu));
    }

    private static class WsListenerHolder {
        private static final WsListener wsListener = new WsListener();
    }
}
