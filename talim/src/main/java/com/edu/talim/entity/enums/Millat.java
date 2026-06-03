package com.edu.talim.entity.enums;

public enum Millat {
    UZBEK("O'zbek"),
    QOZOQ("Qozoq"),
    RUS("Rus"),
    TOJIK("Tojik"),
    QIRGIZ("Qirg'iz");

    private final String label;

    Millat(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static Millat fromLabel(String label) {
        for (Millat m : values()) {
            if (m.label.equalsIgnoreCase(label)) {
                return m;
            }
        }
        throw new RuntimeException("Noto'g'ri millat: " + label);
    }
}