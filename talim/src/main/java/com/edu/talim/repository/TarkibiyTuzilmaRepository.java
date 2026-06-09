package com.edu.talim.repository;

import com.edu.talim.entity.TarkibiyTuzilma;
import com.edu.talim.entity.enums.TarkibiyTuzilmaTuri;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TarkibiyTuzilmaRepository extends JpaRepository<TarkibiyTuzilma, Long> {
    List<TarkibiyTuzilma> findByTuri(TarkibiyTuzilmaTuri turi);
}