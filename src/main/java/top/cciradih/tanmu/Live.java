package top.cciradih.tanmu;

public enum Live {
    ROOM_ID("616");

    private String value;

    Live(String value) {
        this.value = value;
    }

    public String getShortId() {
        return value;
    }

    public String getId() {
        return Http.getInstance().getRoom(value).getJSONObject("data").getString("roomid");
    }
}
