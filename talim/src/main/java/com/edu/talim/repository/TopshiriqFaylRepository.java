package com.edu.talim.repository;

import com.edu.talim.entity.TopshiriqFayl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TopshiriqFaylRepository extends JpaRepository<TopshiriqFayl, UUID> {

    List<TopshiriqFayl> findByTopshiriqId(UUID topshiriqId);
}