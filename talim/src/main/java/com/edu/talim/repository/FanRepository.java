package com.edu.talim.repository;

import com.edu.talim.entity.Fan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FanRepository extends JpaRepository<Fan, Long> {

    // Barcha fanlar — sahifalash bilan
    Page<Fan> findAllByOrderByIdDesc(Pageable pageable);

    // Kafedra bo'yicha fanlar (FanTaqsimlash da ishlatiladi)
    List<Fan> findByKafedraId(Long kafedraId);
}