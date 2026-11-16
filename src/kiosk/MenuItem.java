package kiosk;

import java.util.List;

abstract class MenuItem {
    private final String name;
    private final int price;

    MenuItem(String name, int price) {
        this.name = name;
        this.price = price;
    }

    String name() { return name; }
    int price() { return price; }
    abstract List<OptionGroup> options();
}
