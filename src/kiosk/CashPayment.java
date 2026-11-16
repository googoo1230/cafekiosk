package kiosk;

record CashPayment(long received) implements PaymentMethod {
    public PaymentResult pay(long amount) {
        return new PaymentResult(received >= amount, "현금", Math.max(0, received - amount));
    }
}
