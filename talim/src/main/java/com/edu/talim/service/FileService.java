package com.edu.talim.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
public class FileService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Value("${app.base-url}")
    private String baseUrl;

    // ruxsatEtilganKengaytmalar - masalan FaylTurlari.RASM yoki FaylTurlari.HUJJAT.
    // Fayl kengaytmasi shu ro'yxatda bo'lmasa - saqlanmaydi, xato qaytariladi.
    public String saveFile(MultipartFile file, List<String> ruxsatEtilganKengaytmalar) {
        try {
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // XAVFSIZLIK: asl fayl nomi (originalFilename) HECH QACHON to'g'ridan-to'g'ri
            // fayl yo'liga qo'shilmaydi - u "../" kabi katalog aylanish (path traversal)
            // ketma-ketliklarini o'z ichiga olishi mumkin. Faqat kengaytmasi (extension)
            // ehtiyotkorlik bilan ajratib olinadi, qolgan hammasi UUID bilan almashtiriladi.
            String kengaytma = xavfsizKengaytma(file.getOriginalFilename());

            // Fayl turi tekshiruvi: faqat ruxsat etilgan kengaytmalar qabul qilinadi
            String sofKengaytma = kengaytma.isEmpty() ? "" : kengaytma.substring(1);
            if (sofKengaytma.isEmpty() || !ruxsatEtilganKengaytmalar.contains(sofKengaytma)) {
                throw new RuntimeException(
                        "Ruxsat etilmagan fayl turi! Faqat quyidagilar qabul qilinadi: "
                                + String.join(", ", ruxsatEtilganKengaytmalar));
            }

            String fileName = UUID.randomUUID() + kengaytma;

            Path filePath = uploadPath.resolve(fileName).normalize();

            // Qo'shimcha himoya: natijaviy yo'l uploadPath ichida qolishini tekshirish
            if (!filePath.startsWith(uploadPath)) {
                throw new RuntimeException("Noto'g'ri fayl nomi!");
            }

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return baseUrl + "/" + uploadDir + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Fayl saqlashda xatolik: " + e.getMessage());
        }
    }

    public void deleteFile(String fileUrl) {
        try {
            if (fileUrl == null) return;

            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            String filePathStr = fileUrl.replace(baseUrl + "/", "");

            // XAVFSIZLIK: o'chirilayotgan fayl HAR DOIM uploads papkasi ichida qolishi kerak -
            // fileUrl manipulyatsiya qilingan bo'lsa ham, papkadan tashqariga chiqib
            // boshqa faylni o'chirib yubormasligi uchun tekshiriladi
            Path filePath = Paths.get(filePathStr).toAbsolutePath().normalize();
            if (!filePath.startsWith(uploadPath)) {
                return;
            }

            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Fayl o'chirishda xatolik: " + e.getMessage());
        }
    }

    // Fayl nomidan FAQAT kengaytmani (masalan ".jpg", ".pdf") xavfsiz tarzda ajratib oladi.
    // Kengaytmada faqat harf/raqam bo'lishi mumkin (aks holda umuman kengaytmasiz qoldiriladi) -
    // shu bilan "../"/null-byte/maxsus belgilar orqali hujum qilish imkonsiz bo'ladi.
    private String xavfsizKengaytma(String originalFilename) {
        if (originalFilename == null) return "";
        int nuqta = originalFilename.lastIndexOf('.');
        if (nuqta < 0 || nuqta == originalFilename.length() - 1) return "";

        String ext = originalFilename.substring(nuqta + 1);
        if (ext.length() > 10 || !ext.matches("[a-zA-Z0-9]+")) {
            return "";
        }
        return "." + ext.toLowerCase();
    }
}