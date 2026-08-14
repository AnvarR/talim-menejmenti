package com.edu.talim.config;

import com.edu.talim.exception.TooManyAttemptsException;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Login va parol o'zgartirish kabi joylarda "brute-force" (parolni ketma-ket taxmin
// qilib topishga urinish) hujumlarining oldini oladi. Xotirada (in-memory) ishlaydi -
// alohida baza yoki Redis talab qilmaydi, bitta server uchun yetarli.
@Component
public class KirishUrinishLimiter {

    private static final int MAX_URINISH = 5;
    private static final Duration QULF_MUDDATI = Duration.ofMinutes(15);

    private final Map<String, Holat> holatlar = new ConcurrentHashMap<>();

    // So'rov amalga oshirilishidan OLDIN chaqiriladi - agar hozircha qulflangan bo'lsa, to'xtatadi
    public void tekshir(String kalit) {
        Holat holat = holatlar.get(kalit);
        if (holat == null) return;

        synchronized (holat) {
            if (holat.qulflanganGacha != null) {
                if (Instant.now().isBefore(holat.qulflanganGacha)) {
                    long qolganDaqiqa = Duration.between(Instant.now(), holat.qulflanganGacha).toMinutes() + 1;
                    throw new TooManyAttemptsException(
                            "Juda ko'p noto'g'ri urinish! " + qolganDaqiqa + " daqiqadan keyin qayta urinib ko'ring.");
                }
                // Qulf muddati tugagan - hisobni tozalaymiz
                holat.soni = 0;
                holat.qulflanganGacha = null;
            }
        }
    }

    // Muvaffaqiyatsiz urinishdan keyin chaqiriladi (masalan parol noto'g'ri chiqsa)
    public void muvaffaqiyatsiz(String kalit) {
        Holat holat = holatlar.computeIfAbsent(kalit, k -> new Holat());
        synchronized (holat) {
            holat.soni++;
            if (holat.soni >= MAX_URINISH) {
                holat.qulflanganGacha = Instant.now().plus(QULF_MUDDATI);
            }
        }
    }

    // Muvaffaqiyatli urinishdan keyin chaqiriladi - hisobni butunlay tozalaydi
    public void muvaffaqiyatli(String kalit) {
        holatlar.remove(kalit);
    }

    private static class Holat {
        int soni = 0;
        Instant qulflanganGacha;
    }
}