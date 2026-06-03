package com.edu.talim.entity.enums;

public enum HarbiyUnvon {
    KURSANT("Kursant"),
    LEYTENANT("Leytenant"),
    KATTA_LEYTENANT("Katta leytenant"),
    MAYOR("Mayor"),
    PODPOLKOVNIK("Podpolkovnik"),
    POLKOVNIK("Polkovnik"),
    GENERAL("General");

    private final String label;

    HarbiyUnvon(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static HarbiyUnvon fromLabel(String label) {
        if (label == null || label.isEmpty()) return null;
        for (HarbiyUnvon h : values()) {
            if (h.label.equalsIgnoreCase(label.trim())) {
                return h;
            }
        }
        return null;
    }
}