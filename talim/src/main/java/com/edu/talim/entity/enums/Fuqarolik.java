package com.edu.talim.entity.enums;

public enum Fuqarolik {
    OZBEKISTON("O'zbekiston"),
    QOZOGISTON("Qozog'iston"),
    ROSSIYA("Rossiya"),
    TOJIKISTON("Tojikiston"),
    QIRGIZISTON("Qirg'iziston");

    private final String label;

    Fuqarolik(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static Fuqarolik fromLabel(String label) {
        for (Fuqarolik f : values()) {
            if (f.label.equalsIgnoreCase(label)) {
                return f;
            }
        }
        throw new RuntimeException("Noto'g'ri fuqarolik: " + label);
    }
}