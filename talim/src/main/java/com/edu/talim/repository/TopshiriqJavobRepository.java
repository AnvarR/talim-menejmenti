package com.edu.talim.repository;

import com.edu.talim.entity.TopshiriqJavob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopshiriqJavobRepository extends JpaRepository<TopshiriqJavob, Long> {

    // Bitta kursantning shu topshiriq bo'yicha barcha javoblari (urinishlari), eng yangisi birinchi
    List<TopshiriqJavob> findByTopshiriqYuborishIdOrderByBerilganSanaDesc(Long topshiriqYuborishId);
}