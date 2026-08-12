package com.edu.talim.dto;

import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ChetlatishRequestDTO {
    private List<Long> studentIds;
    private Long oquvYiliId;
    private String sababi;
}