package top.cciradih.tanmu;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import javafx.application.Platform;

import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.concurrent.CompletionStage;

final class WsListener implements WebSocket.Listener {
    private static final byte[] OPEN_HEAD = {0x00, 0x00, 0x00, 0, 0x00, 0x10, 0x00, 0x00, 0x00, 0x00, 0x00, 0x07, 0x00, 0x00, 0x00, 0x00};
    private static final byte[] OPEN_BODY = ("{\"uid\":0,\"roomid\":" + Live.ROOM_ID.getId() + "}").getBytes();
    private static Controller controller;
    private Pipe pipe;

    static WsListener getInstance() {
        return WsListenerHolder.wsListener;
    }

    private WsListener() {
    }

    Pipe getPipe() {
        return pipe;
    }

    static WsListener getInstance(Controller controller) {
        WsListener.controller = controller;
        return WsListenerHolder.wsListener;
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
        System.out.println(error.getMessage());
        WebSocket.Listener.super.onError(webSocket, error);
    }

    @Override
    public void onOpen(WebSocket webSocket) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(Ws.getInstance().generatePacket(OPEN_HEAD, OPEN_BODY));
        webSocket.sendBinary(byteBuffer, true);
        WebSocket.Listener.super.onOpen(webSocket);
    }

    //|封包长度   |头部长度|协议版本|操作码     |常数       |
    //|00 00 00 29|00 10   |00 00   |00 00 00 07|00 00 00 00|
    @Override
    public CompletionStage<?> onBinary(WebSocket webSocket, ByteBuffer data, boolean last) {
        int length = data.remaining();
        byte[] bytes = new byte[length];
        data.get(bytes, 0, length);
        boolean hasMore = true;
        while (hasMore) {
            int packetLength = handleBytes(bytes);
            if (bytes.length > packetLength) {
                bytes = Arrays.copyOfRange(bytes, packetLength, bytes.length);
            } else {
                hasMore = false;
            }
        }
        return WebSocket.Listener.super.onBinary(webSocket, data, last);
    }

    private int handleBytes(byte[] bytes) {
        int packetLengthStart = 0;
        int packetLengthEnd = 4;
        int headLengthStart = 4;
        int headLengthEnd = 6;
        int opcodeStart = 8;
        int opcodeEnd = 12;

        byte[] packetLengthBytes = Arrays.copyOfRange(bytes, packetLengthStart, packetLengthEnd);
        int packetLength = handleFourBitBytes(packetLengthBytes);

        byte[] headLengthBytes = Arrays.copyOfRange(bytes, headLengthStart, headLengthEnd);
        int headLength = handleTwoBitBytes(headLengthBytes);
        byte[] bodyBytes = Arrays.copyOfRange(bytes, headLength, packetLength);

        byte[] opcodeBytes = Arrays.copyOfRange(bytes, opcodeStart, opcodeStart + opcodeEnd);
        int opcode = handleFourBitBytes(opcodeBytes);

        if (opcode == 8) {
            setRoomIdText(Live.ROOM_ID.getShortId());
            System.out.println("已连接。");
        }

        if (opcode == 3) {
            int popularity = handleFourBitBytes(bodyBytes);
            setPopularityText(popularity);
            System.out.println("已检测心跳包。");
        }

        if (opcode == 5) {
            checkType(JSON.parseObject(new String(bodyBytes, Charset.forName("UTF-8"))));
        }
        return packetLength;
    }

    private int handleFourBitBytes(byte[] fourBitBytes) {
        int i = (fourBitBytes[0] & 0xff) << 24;
        i |= (fourBitBytes[1] & 0xff) << 16;
        i |= (fourBitBytes[2] & 0xff) << 8;
        return i |= fourBitBytes[3] & 0xff;
    }

    private int handleTwoBitBytes(byte[] twoBitBytes) {
        int i = (twoBitBytes[0] & 0xff) << 8;
        return i | twoBitBytes[1] & 0xff;
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

    private void setPopularityText(int popularity) {
        Platform.runLater(() -> controller.setPopularityText("人气：" + popularity));
    }

    private void setRoomIdText(String roomId) {
        Platform.runLater(() -> controller.setRoomIdText("房间：" + roomId));
    }

    private static class WsListenerHolder {
        private static final WsListener wsListener = new WsListener();
    }
}
