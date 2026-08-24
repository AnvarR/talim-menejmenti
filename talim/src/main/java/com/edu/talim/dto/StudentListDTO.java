package com.edu.talim.dto;

import java.util.UUID;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentListDTO {

    private UUID id;
    private String oquvYili;
    private String kursi;
    private String guruhi;
    private String fio;
    private String jinsi;
    private String type;
}
