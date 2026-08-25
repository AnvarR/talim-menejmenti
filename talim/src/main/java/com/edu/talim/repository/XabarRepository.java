package com.edu.talim.repository;

import com.edu.talim.entity.Xabar;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface XabarRepository extends JpaRepository<Xabar, UUID> {

    /**
     * Ikki foydalanuvchi o'rtasidagi xabarlarni qaytaradi.
     * Ikki tomonlama — A→B va B→A xabarlar birgalikda ko'rinadi.
     */
    @Query("""
        SELECT x FROM Xabar x
        WHERE (x.senderId = :userId AND x.senderType = :userType
               AND x.receiverId = :otherId AND x.receiverType = :otherType)
        OR    (x.senderId = :otherId AND x.senderType = :otherType
               AND x.receiverId = :userId AND x.receiverType = :userType)
        ORDER BY x.createdAt ASC
    """)
    List<Xabar> findConversation(
            @Param("userId") String userId,
            @Param("userType") String userType,
            @Param("otherId") String otherId,
            @Param("otherType") String otherType
    );

    /**
     * Foydalanuvchining barcha kiruvchi xabarlarini qaytaradi.
     */
    Page<Xabar> findByReceiverIdAndReceiverTypeOrderByCreatedAtDesc(
            String receiverId, String receiverType, Pageable pageable
    );

    /**
     * O'qilmagan xabarlar sonini qaytaradi.
     */
    long countByReceiverIdAndReceiverTypeAndOqilgan(
            String receiverId, String receiverType, Boolean oqilgan
    );
}