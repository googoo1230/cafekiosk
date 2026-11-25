package kiosk;

final class PaymentFactory {
    PaymentMethod create(PaymentType type, String cashInput) {
        if (type == PaymentType.CARD) return new CardPayment();
        try {
            long received = Long.parseLong(cashInput.strip());
            if (received < 0) throw new NumberFormatException();
            return new CashPayment(received);
        } catch (NumberFormatException error) {
            throw new IllegalArgumentException("0 이상의 정수를 입력해 주세요.");
        }
    }
}
