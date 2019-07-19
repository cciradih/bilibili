package top.cciradih.tanmu;

public enum Live {
    ROOM_ID("5096");

    private String value;

    Live(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
