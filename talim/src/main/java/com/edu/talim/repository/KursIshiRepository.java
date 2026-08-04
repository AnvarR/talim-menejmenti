package com.edu.talim.repository;

import com.edu.talim.entity.KursIshi;
import com.edu.talim.entity.enums.Semestr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KursIshiRepository extends JpaRepository<KursIshi, Long> {

    List<KursIshi> findByOqituvchiFanTaqsimlashIdAndOquvYiliIdAndSemestrOrderByMuddatAsc(
            Long oqituvchiFanTaqsimlashId, Long oquvYiliId, Semestr semestr);
}