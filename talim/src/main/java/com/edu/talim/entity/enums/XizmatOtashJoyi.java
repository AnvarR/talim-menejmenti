package com.edu.talim.entity.enums;

public enum XizmatOtashJoyi {
    NAVBATCHI_YORDAMCHISI("Navbatchi yordamchisi"),
    NOJ_1_NAVBATCHI("1-NOJ bo'yicha navbatchi"),
    NOJ_1_NAVBATCHI_YORDAMCHISI("1-NOJ bo'yicha navbatchi yordamchisi"),
    NOJ_1_POSBON("1-NOJ bo'yicha posbon"),
    NOJ_2_NAVBATCHI("2-NOJ bo'yicha navbatchi"),
    NOJ_2_NAVBATCHI_YORDAMCHISI("2-NOJ bo'yicha navbatchi yordamchisi"),
    NOJ_2_POSBON("2-NOJ bo'yicha posbon"),
    YOTOQXONA_1_NAVBATCHI("1-Yotoqxona bo'yicha navbatchi"),
    YOTOQXONA_1_NAVBATCHI_YORDAMCHISI("1-Yotoqxona bo'yicha navbatchi yordamchisi"),
    YOTOQXONA_1_POSBON("1-Yotoqxona bo'yicha posbon"),
    YOTOQXONA_2_NAVBATCHI("2-Yotoqxona bo'yicha navbatchi"),
    YOTOQXONA_2_NAVBATCHI_YORDAMCHISI("2-Yotoqxona bo'yicha navbatchi yordamchisi"),
    YOTOQXONA_2_POSBON("2-Yotoqxona bo'yicha posbon"),
    OQUV_BINOSI_NAVBATCHI("O'quv binosi bo'yicha navbatchi"),
    OQUV_BINOSI_POSBON("O'quv binosi bo'yicha posbon"),
    INSTITUT_PATRUL("Institut bo'yicha patrul"),
    QIZLAR_YOTOQXONASI_NAVBATCHI("Qizlar yotoqxonasi bo'yicha navbatchi"),
    QIZLAR_YOTOQXONASI_POSBON("Qizlar yotoqxonasi bo'yicha posbon");

    private final String label;

    XizmatOtashJoyi(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static XizmatOtashJoyi fromLabel(String label) {
        for (XizmatOtashJoyi x : values()) {
            if (x.label.equalsIgnoreCase(label)) return x;
        }
        throw new RuntimeException("Noto'g'ri xizmat o'tash joyi: " + label);
    }
}