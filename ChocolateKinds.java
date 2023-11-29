package org.example.app.entities;

public enum ChocolateKinds {
    white("белый"), milk("молочный"), bitter("горький"), creamy("сливочный");
    private String text;

    ChocolateKinds(String ck) {
        text = ck;
    }

    public String getText() {
        return text;
    }

    public static ChocolateKinds getInstance(String value) {
        ChocolateKinds kind = null;
        for (ChocolateKinds m : ChocolateKinds.values()) {
            if (m.getText().equals(value)) {
                kind = m;
                break;
            }
        }
        return kind;
    }
}
