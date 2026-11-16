package kiosk;

import java.util.ArrayList;
import java.util.List;

final class Cart {
    private final List<CartItem> items = new ArrayList<>();

    List<CartItem> items() { return List.copyOf(items); }
    boolean isEmpty() { return items.isEmpty(); }
    long total() { return items.stream().mapToLong(CartItem::subtotal).sum(); }

    void add(MenuItem product, List<Option> options, int quantity) {
        CartItem added = new CartItem(product, options, quantity);
        for (int i = 0; i < items.size(); i++) {
            CartItem current = items.get(i);
            if (current.product() == product && current.options().equals(added.options())) {
                changeQuantity(i, current.quantity() + quantity);
                return;
            }
        }
        items.add(added);
    }

    void changeQuantity(int index, int quantity) {
        CartItem item = items.get(index);
        items.set(index, new CartItem(item.product(), item.options(), quantity));
    }

    void remove(int index) { items.remove(index); }
    void clear() { items.clear(); }
}
