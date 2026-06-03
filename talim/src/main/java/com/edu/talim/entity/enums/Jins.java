package com.edu.talim.entity.enums;

public enum Jins {
    ERKAK("Erkak"),
    AYOL("Ayol");

    private final String label;

    Jins(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static Jins fromLabel(String label) {
        for (Jins j : values()) {
            if (j.label.equalsIgnoreCase(label)) {
                return j;
            }
        }
        throw new RuntimeException("Noto'g'ri jins: " + label);
    }
}