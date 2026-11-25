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

    List<Option> validateOptions(List<Option> selected) {
        List<OptionGroup> groups = options();
        if (selected.size() != groups.size()) {
            throw new IllegalArgumentException("각 옵션에서 하나씩 선택해 주세요.");
        }
        for (int i = 0; i < groups.size(); i++) {
            if (!groups.get(i).choices().contains(selected.get(i))) {
                throw new IllegalArgumentException("해당 상품에서 제공하지 않는 옵션입니다.");
            }
        }
        return List.copyOf(selected);
    }
}
