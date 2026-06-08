package com.edu.talim.entity.enums;

public enum Malumot {
    ORTA("O'rta"),
    ORTA_MAXSUS("O'rta-maxsus"),  // ← tire bilan
    OLIY("Oliy"),
    TUGALLANMAGAN_OLIY("Tugallanmagan oliy");

    private final String label;

    Malumot(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static Malumot fromLabel(String label) {
        for (Malumot m : values()) {
            if (m.label.equalsIgnoreCase(label)) {
                return m;
            }
        }
        throw new RuntimeException("Noto'g'ri ma'lumot: " + label);
    }
}