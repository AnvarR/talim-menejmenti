package com.edu.talim.repository;

import com.edu.talim.entity.KompleksFayl;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface KompleksFaylRepository extends JpaRepository<KompleksFayl, UUID> {
}