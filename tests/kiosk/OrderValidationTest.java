package kiosk;

import java.util.ArrayList;
import java.util.List;

public final class OrderValidationTest {
    public static void main(String[] args) {
        MenuItem latte = MenuCatalog.DRINKS.get(1);
        List<Option> selected = List.of(latte.options().get(0).choices().get(0),
                latte.options().get(1).choices().get(1));
        Cart cart = new Cart();
        cart.add(latte, selected, 1);
        rejects(() -> cart.add(latte, List.of(), 1));
        rejects(() -> cart.add(latte, List.of(selected.get(0), selected.get(0)), 1));
        rejects(() -> cart.add(latte, List.of(selected.get(0), new Option("위조 옵션", -100)), 1));
        rejects(() -> cart.add(MenuCatalog.BAKERY.getFirst(), selected, 1));
        require(cart.total() == 5000 && cart.items().size() == 1);

        DiscountService discounts = new DiscountService();
        require(discounts.select(DiscountType.NONE, "").isEmpty());
        require(discounts.select(DiscountType.STUDENT, "INVALID").isEmpty());
        require(discounts.select(DiscountType.STUDENT, " DEMO2026001 ").orElseThrow().discount(5000) == 500);
        DiscountPolicy coupon = discounts.select(DiscountType.COUPON, " cafe1000 ").orElseThrow();
        require(coupon.discount(500) == 500);
        require(discounts.select(DiscountType.COUPON, "").isEmpty());

        PaymentFactory payments = new PaymentFactory();
        rejects(() -> payments.create(PaymentType.CASH, "-100"));
        rejects(() -> payments.create(PaymentType.CASH, "abc"));
        rejects(() -> payments.create(PaymentType.CASH, "9223372036854775808"));
        rejects(() -> new CashPayment(-1));
        OrderService service = new OrderService();
        OrderQuote quote = service.quote(cart, OrderMode.TAKEOUT, coupon);
        require(service.place(quote, payments.create(PaymentType.CASH, "100")).isEmpty());
        require(cart.total() == 5000);
        Order order = service.place(quote, payments.create(PaymentType.CASH, "4500")).orElseThrow();
        require(order.number() == 1 && order.payment().change() == 500);
        require(new ReceiptFormatter().format(order).contains("최종 결제 4000원"));
        require(service.place(quote, payments.create(PaymentType.CARD, "")).isPresent());
        rejects(() -> new OrderQuote(cart.items(), OrderMode.DINE_IN, 5000, "오류", 5001));
        rejects(() -> new OrderQuote(cart.items(), OrderMode.DINE_IN, 5000, "오류", -1));

        List<Option> mutable = new ArrayList<>(selected);
        CartItem snapshot = new CartItem(latte, mutable, 1);
        mutable.clear();
        require(snapshot.subtotal() == 5000);
        System.out.println("Order validation tests passed");
    }

    private static void require(boolean condition) {
        if (!condition) throw new AssertionError("검증 실패");
    }

    private static void rejects(Runnable action) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            return;
        }
        throw new AssertionError("잘못된 입력이 허용됨");
    }
}
