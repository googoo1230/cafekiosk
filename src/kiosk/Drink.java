package kiosk;

import java.util.List;

final class Drink extends MenuItem {
    private final List<OptionGroup> options;

    Drink(String name, int price, OptionGroup... options) {
        super(name, price);
        this.options = List.of(options);
    }

    @Override
    List<OptionGroup> options() { return options; }
}
