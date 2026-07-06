package com.edu.talim.repository;

import com.edu.talim.entity.OquvYili;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OquvYiliRepository extends JpaRepository<OquvYili, Long> {

    Optional<OquvYili> findByFaolTrue();

    boolean existsByNom(String nom);

    boolean existsByBoshlanishYilAndTugashYil(Integer boshlanishYil, Integer tugashYil);
}