package org.example.app.entities;

public enum Measures {
    piece("шт.", "Штуки"), kilo("кг", "Килограммы"), gram("г", "Граммы");
    private String text;
    private String description;

    Measures(String m, String d) {
        text = m;
        description = d;
    }

    public String getText() {
        return text;
    }

    public String getDescription() {
        return description;
    }
    public static Measures getInstance(String value) {
        Measures mes = null;
        for (Measures m : Measures.values()) {
            if (m.getText().equals(value)) {
                mes = m;
                break;
            }
        }
        return mes;
    }
}
