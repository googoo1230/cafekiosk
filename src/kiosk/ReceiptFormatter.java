package kiosk;

final class ReceiptFormatter {
    String totals(OrderQuote quote) {
        return "할인 전 " + quote.subtotal() + "원 | " + quote.discountLabel() + " "
                + quote.discount() + "원 | 최종 결제 " + quote.total() + "원";
    }

    String format(Order order) {
        OrderQuote quote = order.quote();
        StringBuilder text = new StringBuilder("[영수증] 주문번호 %03d | %s\n"
                .formatted(order.number(), quote.mode().label()));
        String separator = "-".repeat(60);
        text.append(separator).append("\n\n\n");
        for (CartItem item : quote.items()) {
            text.append(item.description().replace(" = ", "=")).append('\n');
        }
        text.append("\n\n").append(separator).append("\n\n\n");
        text.append(totals(quote)).append("\n결제 수단 ").append(order.payment().method());
        if (order.payment().change() > 0) {
            text.append("\n거스름돈 ").append(order.payment().change()).append("원");
        }
        return text.toString();
    }
}
