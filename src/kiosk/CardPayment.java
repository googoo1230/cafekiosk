package kiosk;

final class CardPayment implements PaymentMethod {
    public PaymentResult pay(long amount) {
        return new PaymentResult(true, "카드", 0);
    }
}
