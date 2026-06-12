package com.edu.talim.entity.enums;

public enum ChiqishSababi {
    NAVBATDAGI_UVALNENIYA("Navbatdagi uvalneniya"),
    RAGBAT_UVALNENIYA("Rag'bat / navbatdan tashqari uvalneniya"),
    KASAL_POLIKLINIKA("Kasal / poliklinika");

    private final String label;

    ChiqishSababi(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static ChiqishSababi fromLabel(String label) {
        for (ChiqishSababi c : values()) {
            if (c.label.equalsIgnoreCase(label)) return c;
        }
        throw new RuntimeException("Noto'g'ri chiqish sababi: " + label);
    }
}