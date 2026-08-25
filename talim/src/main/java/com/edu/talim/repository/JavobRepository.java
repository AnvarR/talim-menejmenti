package com.edu.talim.repository;

import com.edu.talim.entity.Javob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JavobRepository extends JpaRepository<Javob, UUID> {

    /** Bitta savolga tegishli barcha javoblar */
    List<Javob> findBySavolIdOrderByCreatedAtAsc(UUID savolId);

    /** Bitta savolga tegishli javoblar soni */
    int countBySavolId(UUID savolId);
}