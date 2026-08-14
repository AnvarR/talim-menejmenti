package com.edu.talim.config;

import java.util.List;

// Fayl yuklash joylarida ruxsat etilgan kengaytmalar - bitta joyda saqlanadi,
// shunda barcha servislar bir xil qoidaga amal qiladi
public final class FaylTurlari {

    private FaylTurlari() {}

    // Foto (profil rasm) uchun - faqat rasm formatlari
    public static final List<String> RASM = List.of("jpg", "jpeg", "png", "webp");

    // Ta'lim materiallari / topshiriqlar / hujjatlar uchun
    public static final List<String> HUJJAT = List.of(
            "pdf", "doc", "docx", "xlsx", "xls", "ppt", "pptx", "zip", "rar"
    );

    // Savol-javob bo'limi uchun (hujjat + rasm birga bo'lishi mumkin)
    public static final List<String> HUJJAT_VA_RASM = List.of(
            "pdf", "doc", "docx", "xlsx", "xls", "ppt", "pptx", "jpg", "jpeg", "png", "webp"
    );
}