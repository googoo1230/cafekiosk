package kiosk;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public final class KioskApp {
    private final Scanner input;
    private final Cart cart = new Cart();
    private final OrderService orders = new OrderService();
    private KioskApp(Scanner input) { this.input = input; }

    public static void main(String[] args) {
        try (Scanner input = new Scanner(System.in, "UTF-8")) {
            new KioskApp(input).run();
        } catch (NoSuchElementException endOfInput) {
            System.out.println("\n입력이 종료되었습니다.");
        }
    }

    private void run() {
        System.out.println("카페 키오스크 스마트 오더");
        while (true) {
            showCart();
            System.out.println("1. 상품 추가  2. 수량 변경  3. 항목 삭제  4. 결제  0. 종료");
            try {
                switch (number("선택", 0, 4)) {
                    case 0 -> { return; }
                    case 1 -> addProduct();
                    case 2 -> editCart(false);
                    case 3 -> editCart(true);
                    case 4 -> checkout();
                    default -> throw new IllegalStateException("알 수 없는 메뉴");
                }
            } catch (IllegalArgumentException error) {
                System.out.println(error.getMessage());
            }
        }
    }

    private void showCart() {
        System.out.println("\n[장바구니]");
        List<CartItem> items = cart.items();
        if (items.isEmpty()) {
            System.out.println("장바구니가 비어 있습니다.");
        }
        for (int i = 0; i < items.size(); i++) {
            System.out.println((i + 1) + ". " + items.get(i).description());
        }
        System.out.println("합계 " + cart.total() + "원");
    }

    private void addProduct() {
        System.out.println("1. 음료  2. 베이커리  0. 돌아가기");
        int category = number("분류", 0, 2);
        if (category == 0) return;
        List<MenuItem> menu = category == 1 ? MenuCatalog.DRINKS : MenuCatalog.BAKERY;
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.get(i);
            System.out.printf("%d. %s %d원%n", i + 1, item.name(), item.price());
        }
        int choice = number("상품 (0: 돌아가기)", 0, menu.size());
        if (choice == 0) return;
        MenuItem product = menu.get(choice - 1);
        List<Option> options = new ArrayList<>();
        for (OptionGroup group : product.options()) {
            if (group.choices().size() == 1) {
                Option option = group.choices().getFirst();
                System.out.println(group.label() + " " + option.label());
                options.add(option);
                continue;
            }
            for (int i = 0; i < group.choices().size(); i++) {
                Option option = group.choices().get(i);
                System.out.printf("%d. %s (+%d원)%n", i + 1, option.label(), option.surcharge());
            }
            options.add(group.choices().get(number(group.label(), 1, group.choices().size()) - 1));
        }
        cart.add(product, options, number("수량", 1, 99));
    }

    private void editCart(boolean remove) {
        if (cart.isEmpty()) {
            System.out.println("장바구니가 비어 있습니다.");
            return;
        }
        int item = number("항목 (0: 돌아가기)", 0, cart.items().size());
        if (item == 0) return;
        if (remove) {
            cart.remove(item - 1);
        } else {
            cart.changeQuantity(item - 1, number("새 수량", 1, 99));
        }
    }

    private void checkout() {
        if (cart.isEmpty()) {
            System.out.println("빈 장바구니는 결제할 수 없습니다.");
            return;
        }
        System.out.println("1. 매장 이용  2. 테이크아웃  0. 돌아가기");
        int mode = number("이용 방식", 0, 2);
        if (mode == 0) return;
        OrderMode orderMode = mode == 1 ? OrderMode.DINE_IN : OrderMode.TAKEOUT;
        OrderQuote quote = orders.quote(cart, orderMode, selectDiscount());
        showTotals(quote);

        System.out.println("1. 카드  2. 현금  0. 결제 취소");
        int choice = number("결제 수단", 0, 2);
        if (choice == 0) return;
        PaymentMethod payment = choice == 1 ? new CardPayment()
                : new CashPayment(number("투입 금액 (원)", 0, Integer.MAX_VALUE));
        var result = orders.place(quote, payment);
        if (result.isEmpty()) {
            System.out.println("현금이 부족합니다. 장바구니에 주문을 유지합니다.");
            return;
        }
        printReceipt(result.get());
        cart.clear();
    }

    private DiscountPolicy selectDiscount() {
        System.out.println("1. 학생 할인  2. 쿠폰 할인  0. 할인 없음");
        return switch (number("할인", 0, 2)) {
            case 1 -> {
                System.out.print("시연용 학생증 번호 입력 (Enter: 건너뛰기): ");
                String studentCardNumber = input.nextLine();
                if (studentCardNumber.isBlank()) yield null;
                StudentDiscount student = new StudentDiscount(studentCardNumber);
                if (student.isVerified()) {
                    System.out.println("학생증 번호가 확인되었습니다.");
                    yield student;
                }
                System.out.println("등록된 시연용 학생증 번호가 없어 할인을 적용하지 않습니다.");
                yield null;
            }
            case 2 -> {
                System.out.print("쿠폰 코드 입력: ");
                CouponDiscount coupon = new CouponDiscount(input.nextLine());
                if (coupon.isValid()) yield coupon;
                System.out.println("유효하지 않은 쿠폰 코드입니다.");
                yield null;
            }
            default -> null;
        };
    }

    private void printReceipt(Order order) {
        OrderQuote quote = order.quote();
        System.out.printf("%n[영수증] 주문번호 %03d | %s%n", order.number(), quote.mode().label());
        for (CartItem item : quote.items()) {
            System.out.println(item.description());
        }
        showTotals(quote);
        System.out.println("결제 수단 " + order.payment().method());
        if (order.payment().change() > 0) {
            System.out.println("거스름돈 " + order.payment().change() + "원");
        }
    }

    private void showTotals(OrderQuote quote) {
        System.out.printf("할인 전 %d원 | %s %d원 | 최종 결제 %d원%n",
                quote.subtotal(), quote.discountLabel(), quote.discount(), quote.total());
    }

    private int number(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt + ": ");
            String line = input.nextLine().strip();
            try {
                int value = Integer.parseInt(line);
                if (value >= min && value <= max) return value;
            } catch (NumberFormatException invalidNumber) {
                // 숫자가 아닌 입력에는 아래 안내를 출력하고 다시 입력받는다.
            }
            System.out.printf("%d~%d 사이의 정수를 입력하세요.%n", min, max);
        }
    }
}
