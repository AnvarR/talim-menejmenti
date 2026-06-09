package com.edu.talim.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class UserDetailDTO {

    private Long id;
    private String photoUrl;
    private String jshshir;
    private String passportMalumotlari;
    private LocalDate tugilganSana;
    private String fuqaroligi;
    private String fio;
    private LocalDate hujjatBerilganSana;
    private String millati;
    private String telefon1;
    private String telefon2;
    private String malumoti;
    private String jinsi;
    private String hujjatBerganTashkilot;
    private String pochtaManzili;

    // Ish joyi va lavozimi
    private Long tarkibiyTuzilmaId;
    private String tarkibiyTuzilmaNomi;
    private String lavozimi;
    private String ilmiyUnvoni;
    private String ilmiyDarajasi;
    private String guvohnomaNomeri;
    private String harbiyUnvoni;

    private LocalDateTime createdAt;
}