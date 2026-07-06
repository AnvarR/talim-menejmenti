package com.edu.talim.repository;

import com.edu.talim.entity.DarsJadvali;
import com.edu.talim.entity.enums.HaftaKuni;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DarsJadvaliRepository extends JpaRepository<DarsJadvali, Long> {

    List<DarsJadvali> findByKursIdAndOquvYiliId(Long kursId, Long oquvYiliId);

    Optional<DarsJadvali> findByKursIdAndOquvYiliIdAndHaftaKuni(
            Long kursId, Long oquvYiliId, HaftaKuni haftaKuni);

    boolean existsByKursIdAndOquvYiliIdAndHaftaKuni(
            Long kursId, Long oquvYiliId, HaftaKuni haftaKuni);
}