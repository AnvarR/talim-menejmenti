package com.edu.talim.repository;

import com.edu.talim.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByJshshir(String jshshir);
    Optional<User> findByUsername(String username);
}