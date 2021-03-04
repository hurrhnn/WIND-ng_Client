package client;

public enum Info {
    FONT_NAME("BM JUA");

    final private String value;

    Info(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
