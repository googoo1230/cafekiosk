package kiosk;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public final class KioskGui extends JFrame {
    private static final Color PAPER = new Color(247, 244, 237);
    private static final Color GREEN = new Color(33, 76, 58);
    private final Cart cart = new Cart();
    private final OrderService orders = new OrderService();
    private final DiscountService discounts = new DiscountService();
    private final ReceiptFormatter receipts = new ReceiptFormatter();
    private final DefaultListModel<String> cartRows = new DefaultListModel<>();
    private final JList<String> cartList = new JList<>(cartRows);
    private final JLabel total = new JLabel();
    private final JButton checkout = primary("결제하기");

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new KioskGui().setVisible(true));
    }

    private KioskGui() {
        super("카페 스마트 오더");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        JPanel root = new JPanel(new BorderLayout(24, 24));
        root.setBackground(PAPER);
        root.setBorder(new EmptyBorder(26, 28, 26, 28));
        setContentPane(root);

        JLabel heading = new JLabel("카페 스마트 오더");
        heading.setForeground(GREEN);
        heading.setFont(heading.getFont().deriveFont(Font.BOLD, 28f));
        root.add(heading, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("음료", products(MenuCatalog.DRINKS));
        tabs.addTab("베이커리", products(MenuCatalog.BAKERY));
        root.add(tabs, BorderLayout.CENTER);
        root.add(cartPanel(), BorderLayout.EAST);
        refreshCart();
        setMinimumSize(new Dimension(780, 430));
        setSize(960, 540);
        setLocationRelativeTo(null);
    }

    private JPanel products(List<MenuItem> menu) {
        JPanel panel = new JPanel(new GridLayout(0, 2, 12, 12));
        panel.setBackground(PAPER);
        panel.setBorder(new EmptyBorder(14, 0, 0, 0));
        for (MenuItem product : menu) {
            JButton button = new JButton("<html><center><b>" + product.name()
                    + "</b><br>" + money(product.price()) + "</center></html>");
            button.setBackground(Color.WHITE);
            button.setForeground(GREEN);
            button.setFont(button.getFont().deriveFont(17f));
            button.addActionListener(event -> addProduct(product));
            panel.add(button);
        }
        return panel;
    }

    private JPanel cartPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setPreferredSize(new Dimension(340, 0));
        panel.setBackground(PAPER);
        JLabel title = new JLabel("장바구니");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 19f));
        panel.add(title, BorderLayout.NORTH);
        cartList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(cartList), BorderLayout.CENTER);

        JPanel actions = new JPanel(new GridLayout(0, 1, 0, 8));
        actions.setOpaque(false);
        JPanel edits = new JPanel(new GridLayout(1, 2, 8, 0));
        edits.setOpaque(false);
        JButton quantity = new JButton("수량 변경");
        quantity.addActionListener(event -> editSelected(this::changeQuantity));
        JButton remove = new JButton("삭제");
        remove.addActionListener(event -> editSelected(cart::remove));
        edits.add(quantity);
        edits.add(remove);
        actions.add(edits);
        total.setHorizontalAlignment(SwingConstants.RIGHT);
        total.setFont(total.getFont().deriveFont(Font.BOLD, 17f));
        actions.add(total);
        checkout.addActionListener(event -> checkout());
        actions.add(checkout);
        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }

    private static JButton primary(String label) {
        JButton button = new JButton(label);
        button.setBackground(GREEN);
        button.setForeground(Color.WHITE);
        return button;
    }

    private void addProduct(MenuItem product) {
        JPanel form = new JPanel(new GridLayout(0, 2, 10, 8));
        List<JComboBox<String>> selections = new ArrayList<>();
        for (OptionGroup group : product.options()) {
            String[] labels = group.choices().stream()
                    .map(option -> option.label() + (option.surcharge() == 0
                            ? "" : " +" + money(option.surcharge())))
                    .toArray(String[]::new);
            JComboBox<String> selector = new JComboBox<>(labels);
            form.add(new JLabel(group.label()));
            form.add(selector);
            selections.add(selector);
        }
        JSpinner quantity = quantitySpinner(1);
        form.add(new JLabel("수량"));
        form.add(quantity);
        if (!confirm(form, product.name() + "   " + money(product.price()))) return;

        List<Option> options = new ArrayList<>();
        for (int i = 0; i < selections.size(); i++) {
            options.add(product.options().get(i).choices().get(selections.get(i).getSelectedIndex()));
        }
        try {
            cart.add(product, options, (Integer) quantity.getValue());
            refreshCart();
        } catch (IllegalArgumentException error) {
            message(error.getMessage(), "수량을 확인해 주세요");
        }
    }

    private void editSelected(IntConsumer action) {
        int index = cartList.getSelectedIndex();
        if (index < 0) {
            message("장바구니에서 상품을 선택해 주세요.", "상품 선택");
            return;
        }
        action.accept(index);
        refreshCart();
    }

    private void changeQuantity(int index) {
        JSpinner quantity = quantitySpinner(cart.items().get(index).quantity());
        if (confirm(quantity, "새 수량")) {
            cart.changeQuantity(index, (Integer) quantity.getValue());
        }
    }

    private static JSpinner quantitySpinner(int value) {
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(value, 1, 99, 1));
        ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField().setEditable(false);
        return spinner;
    }

    private void refreshCart() {
        cartRows.clear();
        for (CartItem item : cart.items()) cartRows.addElement(item.description());
        total.setText("합계  " + money(cart.total()));
        checkout.setEnabled(!cart.isEmpty());
    }

    private void checkout() {
        JComboBox<String> mode = new JComboBox<>(new String[]{"매장 이용", "테이크아웃"});
        JComboBox<String> discount = new JComboBox<>(new String[]{"할인 없음", "학생 할인", "쿠폰 할인"});
        JPanel form = new JPanel(new GridLayout(0, 2, 10, 8));
        form.add(new JLabel("이용 방식"));
        form.add(mode);
        form.add(new JLabel("할인"));
        form.add(discount);
        if (!confirm(form, "주문 정보")) return;

        DiscountPolicy policy = null;
        if (discount.getSelectedIndex() != 0) {
            boolean student = discount.getSelectedIndex() == 1;
            String code = JOptionPane.showInputDialog(this, student ? "시연용 학생증 번호" : "쿠폰 코드");
            if (code == null) return;
            policy = (student ? discounts.student(code) : discounts.coupon(code)).orElse(null);
            if (policy == null && !confirm("등록된 번호가 없습니다. 정상가로 계속할까요?", "할인 확인")) return;
        }
        OrderMode orderMode = mode.getSelectedIndex() == 0 ? OrderMode.DINE_IN : OrderMode.TAKEOUT;
        OrderQuote quote = orders.quote(cart, orderMode, policy);
        pay(quote);
    }

    private void pay(OrderQuote quote) {
        Object[] methods = {"카드", "현금", "취소"};
        int choice = JOptionPane.showOptionDialog(this, receipts.totals(quote), "모의 결제",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, methods, methods[0]);
        if (choice != 0 && choice != 1) return;

        PaymentMethod method = new CardPayment();
        if (choice == 1) {
            String input = JOptionPane.showInputDialog(this, "투입 금액 (원)");
            if (input == null) return;
            try {
                long amount = Long.parseLong(input.strip());
                if (amount < 0) throw new NumberFormatException();
                method = new CashPayment(amount);
            } catch (NumberFormatException error) {
                message("0 이상의 정수를 입력해 주세요.", "금액 확인");
                return;
            }
        }

        var result = orders.place(quote, method);
        if (result.isEmpty()) {
            message("현금이 부족합니다. 장바구니에 주문을 유지합니다.", "결제 실패");
            return;
        }

        Order order = result.get();
        cart.clear();
        refreshCart();
        String receiptHtml = receipts.format(order)
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("-".repeat(60), "<hr width='100%' size='1' noshade>")
                .replace("\n", "<br>");
        JEditorPane receiptText = new JEditorPane("text/html",
                "<html><body style='margin:0'>" + receiptHtml + "</body></html>");
        receiptText.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        receiptText.setFont(UIManager.getFont("Label.font"));
        receiptText.setEditable(false);
        receiptText.setCaretPosition(0);
        receiptText.setPreferredSize(new Dimension(540, 300));
        message(new JScrollPane(receiptText), "영수증 · 주문번호 %03d".formatted(order.number()));
    }

    private static String money(long amount) { return "%,d원".formatted(amount); }

    private boolean confirm(Object content, String title) {
        return JOptionPane.showConfirmDialog(this, content, title,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION;
    }

    private void message(Object content, String title) {
        JOptionPane.showMessageDialog(this, content, title, JOptionPane.PLAIN_MESSAGE);
    }
}
