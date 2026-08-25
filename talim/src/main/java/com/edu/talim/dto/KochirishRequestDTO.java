package com.edu.talim.dto;

import java.util.UUID;

import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class KochirishRequestDTO {
    private List<UUID> studentIds;
    private UUID oquvYiliId;
}