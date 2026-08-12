package com.edu.talim.service;

import com.edu.talim.exception.ConflictException;

import com.edu.talim.exception.NotFoundException;

import com.edu.talim.dto.OquvYiliDTO;
import com.edu.talim.entity.OquvYili;
import com.edu.talim.repository.OquvYiliRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OquvYiliService {

    // Yangi o'quv yili boshlangandan (1-sentabrdan) keyin, eski o'quv yilini
    // tahrirlash uchun ochiq turadigan kunlar soni - shundan keyin avtomatik yopiladi
    private static final int TAHRIR_OCHIQ_KUN = 10;

    private final OquvYiliRepository oquvYiliRepository;

    public List<OquvYiliDTO> getAll() {
        return oquvYiliRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public OquvYiliDTO getFaol() {
        return oquvYiliRepository.findByFaolTrue()
                .map(this::toDTO)
                .orElseThrow(() -> new NotFoundException("Faol o'quv yili topilmadi"));
    }

    @Transactional
    public OquvYiliDTO create(OquvYiliDTO dto) {
        if (oquvYiliRepository.existsByNom(dto.getNom())) {
            throw new ConflictException("Bu o'quv yili allaqachon mavjud: " + dto.getNom());
        }
        if (oquvYiliRepository.existsByBoshlanishYilAndTugashYil(
                dto.getBoshlanishYil(), dto.getTugashYil())) {
            throw new ConflictException("Bu yillar uchun o'quv yili allaqachon mavjud");
        }

        OquvYili entity = OquvYili.builder()
                .nom(dto.getNom())
                .boshlanishYil(dto.getBoshlanishYil())
                .tugashYil(dto.getTugashYil())
                .faol(false)
                .build();

        return toDTO(oquvYiliRepository.save(entity));
    }

    @Transactional
    public OquvYiliDTO faolQilish(Long id) {
        // Avval barcha yillarni faolsizlashtirish
        oquvYiliRepository.findByFaolTrue().ifPresent(y -> {
            y.setFaol(false);
            oquvYiliRepository.save(y);
        });

        OquvYili oquvYili = oquvYiliRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("O'quv yili topilmadi: " + id));

        oquvYili.setFaol(true);
        return toDTO(oquvYiliRepository.save(oquvYili));
    }

    @Transactional
    public void delete(Long id) {
        OquvYili oquvYili = oquvYiliRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("O'quv yili topilmadi: " + id));
        if (oquvYili.getFaol()) {
            throw new RuntimeException("Faol o'quv yilini o'chirib bo'lmaydi");
        }
        oquvYiliRepository.deleteById(id);
    }

    // ====================== Tahrirlash ruxsati ======================

    // Shu o'quv yili ma'lumotlarini hozir tahrirlash mumkinmi?
    // - Faol (joriy) o'quv yili - har doim tahrirlash mumkin
    // - Faol bo'lmagan o'quv yil - faqat 1-sentabrdan TAHRIR_OCHIQ_KUN kun ichida
    //   (joriy faol yilning boshlanishYili asosida), YOKI fakultet boshlig'i qo'shimcha ruxsat bergan bo'lsa
    public boolean tahririshMumkinmi(Long oquvYiliId) {
        OquvYili oquvYili = oquvYiliRepository.findById(oquvYiliId)
                .orElseThrow(() -> new NotFoundException("O'quv yili topilmadi: " + oquvYiliId));

        if (Boolean.TRUE.equals(oquvYili.getFaol())) {
            return true;
        }
        if (Boolean.TRUE.equals(oquvYili.getQoshimchaTahrirRuxsati())) {
            return true;
        }

        // Joriy faol o'quv yilining 1-sentabridan hisoblab TAHRIR_OCHIQ_KUN kun ichidami?
        return oquvYiliRepository.findByFaolTrue()
                .map(faolYil -> {
                    LocalDate yangiYilBoshlanishi = LocalDate.of(faolYil.getBoshlanishYil(), 9, 1);
                    LocalDate tahrirOchiqOxirgiSana = yangiYilBoshlanishi.plusDays(TAHRIR_OCHIQ_KUN);
                    return !LocalDate.now().isAfter(tahrirOchiqOxirgiSana);
                })
                .orElse(true); // Faol yil umuman belgilanmagan bo'lsa - cheklov qo'llanilmaydi
    }

    // Boshqa servislar chaqiradigan tekshiruv - ruxsat yo'q bo'lsa xatolik beradi
    public void tahririshniTekshir(Long oquvYiliId) {
        if (!tahririshMumkinmi(oquvYiliId)) {
            throw new RuntimeException(
                    "Bu o'quv yili ma'lumotlarini tahrirlash muddati tugagan! "
                            + "Qayta ochish uchun fakultet boshlig'i yoki o'rinbosariga murojaat qiling.");
        }
    }

    // Fakultet boshlig'i/o'rinbosari - eski o'quv yilini butunlay qayta ochadi
    @Transactional
    public OquvYiliDTO tahrirgaRuxsatBerish(Long id) {
        OquvYili oquvYili = oquvYiliRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("O'quv yili topilmadi: " + id));
        oquvYili.setQoshimchaTahrirRuxsati(true);
        return toDTO(oquvYiliRepository.save(oquvYili));
    }

    // Berilgan ruxsatni qaytarib yopish
    @Transactional
    public OquvYiliDTO tahrirRuxsatiniYopish(Long id) {
        OquvYili oquvYili = oquvYiliRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("O'quv yili topilmadi: " + id));
        oquvYili.setQoshimchaTahrirRuxsati(false);
        return toDTO(oquvYiliRepository.save(oquvYili));
    }

    private OquvYiliDTO toDTO(OquvYili entity) {
        return OquvYiliDTO.builder()
                .id(entity.getId())
                .nom(entity.getNom())
                .boshlanishYil(entity.getBoshlanishYil())
                .tugashYil(entity.getTugashYil())
                .faol(entity.getFaol())
                .qoshimchaTahrirRuxsati(entity.getQoshimchaTahrirRuxsati())
                .build();
    }
}