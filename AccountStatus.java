package org.example.app.entities;

public enum AccountStatus {
    blocked("b"), notBlocked(""),deleted("d");

    private String text;

    AccountStatus(String t) {
        text = t;
    }

    public String getText() {
        return text;
    }

    public String toString() {
        return text;
    }

    public static AccountStatus getInstance(String value) {
        AccountStatus s = null;
        for (AccountStatus a : AccountStatus.values()) {
            if (a.getText().equals(value)) {
                s = a;
                break;
            }
        }
        return s;
    }
}
