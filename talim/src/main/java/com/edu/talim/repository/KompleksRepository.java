package com.edu.talim.repository;

import java.util.UUID;

import com.edu.talim.entity.Kompleks;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KompleksRepository extends JpaRepository<Kompleks, Long> {

    List<Kompleks> findByOquvYiliId(UUID oquvYiliId);

    List<Kompleks> findByOqituvchiFanTaqsimlash_Oqituvchi_Id(UUID oqituvchiId);
}