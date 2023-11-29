package org.example.app.entities;

public enum Types {
    tile("Плиточный"), sweets("Конфеты"), bars("Батончик"), powder("Порошковый"),
    nuts("Орехи в шоколаде"), cookies("Шоколадное печенье"), paste("Шоколадная паста");
    private String text;

    Types(String t) {
        text = t;
    }

    public String getText() {
        return text;
    }

    public static Types getInstance(String value) {
        Types type = null;
        for (Types m : Types.values()) {
            if (m.getText().equals(value)) {
                type = m;
                break;
            }
        }
        return type;
    }
}
