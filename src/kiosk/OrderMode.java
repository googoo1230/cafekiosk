package kiosk;

enum OrderMode {
    DINE_IN("매장 이용"), TAKEOUT("테이크아웃");

    private final String label;
    OrderMode(String label) { this.label = label; }
    String label() { return label; }
}
