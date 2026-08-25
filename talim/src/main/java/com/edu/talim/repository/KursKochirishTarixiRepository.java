package com.edu.talim.repository;

import java.util.UUID;

import com.edu.talim.entity.KursKochirishTarixi;
import com.edu.talim.entity.enums.KochirishTuri;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface KursKochirishTarixiRepository extends JpaRepository<KursKochirishTarixi, UUID> {

    // Shu kursant shu o'quv yilida allaqachon shu turdagi hodisani boshidan kechirganmi?
    // (masalan: bir xil o'quv yilida ikki marta "keyingi kursga ko'chirilmasin" uchun)
    boolean existsByStudentIdAndOquvYiliIdAndTuri(UUID studentId, UUID oquvYiliId, KochirishTuri turi);

    // Ko'p kursantlar uchun "kim allaqachon ko'chirilgan" ni bitta so'rovda bilish (N+1 oldini olish)
    @Query("""
        SELECT k.student.id FROM KursKochirishTarixi k
        WHERE k.student.id IN :studentIds AND k.oquvYili.id = :oquvYiliId AND k.turi = :turi
    """)
    Set<UUID> findKochirilganStudentIdlar(@Param("studentIds") List<UUID> studentIds,
                                          @Param("oquvYiliId") UUID oquvYiliId,
                                          @Param("turi") KochirishTuri turi);

    // Bitta kursantning butun tarixi (eng yangisidan boshlab)
    List<KursKochirishTarixi> findByStudentIdOrderBySanaDesc(UUID studentId);
}