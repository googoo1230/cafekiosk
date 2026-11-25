package kiosk;

record CashPayment(long received) implements PaymentMethod {
    CashPayment {
        if (received < 0) throw new IllegalArgumentException("현금은 0 이상이어야 합니다.");
    }

    public PaymentResult pay(long amount) {
        return new PaymentResult(received >= amount, "현금", Math.max(0, received - amount));
    }
}
