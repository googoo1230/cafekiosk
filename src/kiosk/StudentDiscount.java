package kiosk;

record StudentDiscount(boolean verified) implements DiscountPolicy {
    private static final int RATE = 10;

    public String label() { return "학생 할인"; }
    public long discount(long total) { return verified ? total * RATE / 100 : 0; }
}
