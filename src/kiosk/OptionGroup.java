package kiosk;

import java.util.List;

record OptionGroup(String label, List<Option> choices) {
    OptionGroup {
        choices = List.copyOf(choices);
    }

    static final OptionGroup TEMPERATURE = new OptionGroup("온도",
            List.of(new Option("HOT", 0), new Option("ICE", 0)));
}
