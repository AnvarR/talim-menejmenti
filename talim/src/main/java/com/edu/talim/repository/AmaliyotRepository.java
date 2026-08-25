package com.edu.talim.repository;

import java.util.UUID;

import com.edu.talim.entity.Amaliyot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AmaliyotRepository extends JpaRepository<Amaliyot, UUID> {

    List<Amaliyot> findByOqituvchiFanTaqsimlashIdAndOquvYiliIdOrderByTugashSanasiAsc(
            UUID oqituvchiFanTaqsimlashId, UUID oquvYiliId);
}