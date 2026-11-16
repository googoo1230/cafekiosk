package kiosk;

final class OrderService {
    private int nextNumber = 1;

    OrderQuote quote(Cart cart, OrderMode mode, DiscountPolicy policy) {
        if (cart.isEmpty()) {
            throw new IllegalArgumentException("장바구니가 비어 있습니다.");
        }
        long subtotal = cart.total();
        return new OrderQuote(cart.items(), mode, subtotal,
                policy == null ? "할인 없음" : policy.label(),
                policy == null ? 0 : policy.discount(subtotal));
    }

    java.util.Optional<Order> place(OrderQuote quote, PaymentMethod method) {
        PaymentResult payment = method.pay(quote.total());
        if (!payment.approved()) {
            return java.util.Optional.empty();
        }
        return java.util.Optional.of(new Order(nextNumber++, quote, payment));
    }
}
