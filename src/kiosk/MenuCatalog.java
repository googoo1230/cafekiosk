package kiosk;

import java.util.List;

final class MenuCatalog {
    static final List<MenuItem> DRINKS = List.of(
            new Drink("아메리카노", 3500, OptionGroup.TEMPERATURE,
                    new OptionGroup("샷", List.of(new Option("기본 샷", 0), new Option("샷 추가", 500)))),
            new Drink("카페라테", 4500, OptionGroup.TEMPERATURE,
                    new OptionGroup("우유", List.of(new Option("기본 우유", 0), new Option("오트밀크", 500)))),
            new Drink("레모네이드", 5000,
                    new OptionGroup("온도", List.of(new Option("ICE", 0))),
                    new OptionGroup("당도", List.of(new Option("기본 당도", 0), new Option("당도 낮게", 0)))));
    static final List<MenuItem> BAKERY = List.of(
            new Bakery("크루아상", 3200), new Bakery("소금빵", 2800), new Bakery("초코 머핀", 3400));

    private MenuCatalog() {}
}
