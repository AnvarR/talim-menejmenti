package com.edu.talim.repository;

import com.edu.talim.entity.TopshiriqFayl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopshiriqFaylRepository extends JpaRepository<TopshiriqFayl, Long> {

    List<TopshiriqFayl> findByTopshiriqId(Long topshiriqId);
}