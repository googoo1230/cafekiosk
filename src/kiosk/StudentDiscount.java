package kiosk;

record StudentDiscount(String studentCardNumber) implements DiscountPolicy {
    private static final int RATE = 10;

    private static final java.util.Set<String> DEMO_STUDENT_CARDS =
            java.util.Set.of("DEMO2026001", "DEMO2026002");

    boolean isVerified() {
        return DEMO_STUDENT_CARDS.contains(studentCardNumber.strip());
    }

    public String label() { return "학생 할인"; }
    public long discount(long total) { return isVerified() ? total * RATE / 100 : 0; }
}
