package kiosk;

interface PaymentMethod {
    PaymentResult pay(long amount);
}
