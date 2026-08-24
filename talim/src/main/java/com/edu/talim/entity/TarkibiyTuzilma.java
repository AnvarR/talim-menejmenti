package com.edu.talim.entity;

import com.edu.talim.entity.enums.TarkibiyTuzilmaTuri;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "tarkibiy_tuzilmalar")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TarkibiyTuzilma {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, columnDefinition = "varchar(255)")
    private String nomi;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TarkibiyTuzilmaTuri turi; // BOLIM yoki KAFEDRA
}