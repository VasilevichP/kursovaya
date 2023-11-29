package org.example.app.entities;

public enum Nuts {
    almond("миндаль"), hazelnut("фундук"), cashew("кешью"), walnut("грецкий");
    private String text;

    Nuts(String n) {
        text = n;
    }

    public String getText() {
        return text;
    }

    public static Nuts getInstance(String value) {
        Nuts nut = null;
        for (Nuts m : Nuts.values()) {
            if (m.getText().equals(value)) {
                nut = m;
                break;
            }
        }
        return nut;
    }
}
