package kiosk;

import java.util.List;

record CartItem(MenuItem product, List<Option> options, int quantity) {
    CartItem {
        options = product.validateOptions(options);
        if (quantity < 1 || quantity > 99) {
            throw new IllegalArgumentException("항목당 수량은 1~99개여야 합니다.");
        }
    }

    long subtotal() {
        long surcharge = options.stream().mapToLong(Option::surcharge).sum();
        return (product.price() + surcharge) * quantity;
    }

    String description() {
        String choices = String.join(", ", options.stream().map(Option::label).toList());
        return product.name() + (choices.isEmpty() ? "" : " (" + choices + ")")
                + " × " + quantity + " = " + subtotal() + "원";
    }
}
