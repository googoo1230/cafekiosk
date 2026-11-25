package kiosk;

enum DiscountType {
    NONE("할인 없음"), STUDENT("학생 할인"), COUPON("쿠폰 할인");

    private final String label;
    DiscountType(String label) { this.label = label; }
    @Override
    public String toString() { return label; }
}
