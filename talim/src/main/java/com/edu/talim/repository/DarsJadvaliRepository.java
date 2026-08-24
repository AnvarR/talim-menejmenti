package com.edu.talim.repository;

import java.util.UUID;

import com.edu.talim.entity.DarsJadvali;
import com.edu.talim.entity.enums.HaftaKuni;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DarsJadvaliRepository extends JpaRepository<DarsJadvali, Long> {

    List<DarsJadvali> findByKursIdAndOquvYiliId(UUID kursId, Long oquvYiliId);

    Optional<DarsJadvali> findByKursIdAndOquvYiliIdAndHaftaKuni(
            UUID kursId, Long oquvYiliId, HaftaKuni haftaKuni);

    boolean existsByKursIdAndOquvYiliIdAndHaftaKuni(
            UUID kursId, Long oquvYiliId, HaftaKuni haftaKuni);
}