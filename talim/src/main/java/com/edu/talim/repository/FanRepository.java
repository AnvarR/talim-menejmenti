package com.edu.talim.repository;

import com.edu.talim.entity.Fan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FanRepository extends JpaRepository<Fan, UUID> {

    // Barcha fanlar — sahifalash bilan
    Page<Fan> findAllByOrderByIdDesc(Pageable pageable);

    // Kafedra bo'yicha fanlar
    List<Fan> findByKafedraId(UUID kafedraId);

    // Dublikat tekshiruvi — bir xil kafedra + fan nomi
    boolean existsByKafedraIdAndFanNomi(UUID kafedraId, String fanNomi);
}