package org.example.app.entities;

public enum AccountRole {
    admin("a"), user("u");

    private String text;

    AccountRole(String t) {
        text = t;
    }

    public String getText() {
        return text;
    }

    public String toString() {
        return text;
    }

    public static AccountRole getInstance(String value) {
        AccountRole r = null;
        for (AccountRole a : AccountRole.values()) {
            if (a.getText().equals(value)) {
                r = a;
                break;
            }
        }
        return r;
    }
}
