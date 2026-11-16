package kiosk;

import java.util.List;

record OrderQuote(List<CartItem> items, OrderMode mode, long subtotal,
                  String discountLabel, long discount) {
    OrderQuote {
        items = List.copyOf(items);
    }

    long total() { return subtotal - discount; }
}
