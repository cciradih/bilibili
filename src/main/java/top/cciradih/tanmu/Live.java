package top.cciradih.tanmu;

public enum Live {
    ROOM_ID("888");

    private String value;

    Live(String value) {
        this.value = value;
    }

    public String getValue() {
        return Http.getInstance().getRoom(value).getJSONObject("data").getString("roomid");
    }
}
