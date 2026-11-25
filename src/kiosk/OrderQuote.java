package kiosk;

import java.util.List;

record OrderQuote(List<CartItem> items, OrderMode mode, long subtotal,
                  String discountLabel, long discount) {
    OrderQuote {
        items = List.copyOf(items);
        if (subtotal < 0) {
            throw new IllegalArgumentException("주문 금액은 0 이상이어야 합니다.");
        }
        if (discount < 0 || discount > subtotal) {
            throw new IllegalArgumentException("할인액은 0 이상 주문 금액 이하여야 합니다.");
        }
    }

    long total() { return subtotal - discount; }
}
