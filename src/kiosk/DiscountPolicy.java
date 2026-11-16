package kiosk;

interface DiscountPolicy {
    String label();
    long discount(long total);
}
