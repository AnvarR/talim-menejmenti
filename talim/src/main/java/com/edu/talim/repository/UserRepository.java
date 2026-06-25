package com.edu.talim.repository;

import com.edu.talim.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.edu.talim.entity.enums.Role;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByJshshir(String jshshir);
    Optional<User> findByUsername(String username);
    // Kafedra ID si va roli bo'yicha xodimni topish
    Optional<User> findByTarkibiyTuzilmaIdAndRole(Long tarkibiyTuzilmaId, Role role);
    // Kafedradagi barcha o'qituvchilarni topish (ro'yxat)
    List<User> findAllByTarkibiyTuzilmaIdAndRole(Long tarkibiyTuzilmaId, Role role);

}