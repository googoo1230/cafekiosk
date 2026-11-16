package kiosk;

record CouponDiscount(String code) implements DiscountPolicy {
    private static final String VALID_CODE = "CAFE1000";
    private static final int AMOUNT = 1000;

    boolean isValid() { return VALID_CODE.equalsIgnoreCase(code.strip()); }
    public String label() { return "쿠폰 할인"; }
    public long discount(long total) { return isValid() ? Math.min(total, AMOUNT) : 0; }
}
