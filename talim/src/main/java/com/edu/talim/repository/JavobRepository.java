package com.edu.talim.repository;

import com.edu.talim.entity.Javob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JavobRepository extends JpaRepository<Javob, Long> {

    /** Bitta savolga tegishli barcha javoblar */
    List<Javob> findBySavolIdOrderByCreatedAtAsc(Long savolId);

    /** Bitta savolga tegishli javoblar soni */
    int countBySavolId(Long savolId);
}