package com.edu.talim.entity;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "fanlar")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Fan {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Qaysi kafedra fani ekanligi
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kafedra_id", nullable = false)
    private TarkibiyTuzilma kafedra;

    // Fan nomi
    @Column(nullable = false, columnDefinition = "varchar(255)")
    private String fanNomi;
}