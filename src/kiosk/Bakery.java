package kiosk;

import java.util.List;

final class Bakery extends MenuItem {
    Bakery(String name, int price) { super(name, price); }

    @Override
    List<OptionGroup> options() { return List.of(); }
}
