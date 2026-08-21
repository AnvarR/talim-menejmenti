package com.edu.talim.config;

import com.edu.talim.security.JwtAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Map;

// Butun ilova bo'yicha avtorizatsiya qoidalari shu yerda belgilanadi:
// har bir endpoint (metod + yo'l) uchun qaysi rol(lar) kira olishi.
// SUPER_ADMIN har doim, barcha qoidalarga qo'shimcha ravishda, hamma narsaga kira oladi.
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CorsConfigurationSource corsConfigurationSource;
    private final ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Autentifikatsiya/avtorizatsiya muvaffaqiyatsiz bo'lganda ham JSON
                // ({"message": "..."}) formatida javob qaytariladi - boshqa xatolar bilan bir xil
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.setContentType("application/json;charset=UTF-8");
                            objectMapper.writeValue(response.getWriter(),
                                    Map.of("message", "Tizimga kirish talab qilinadi!"));
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpStatus.FORBIDDEN.value());
                            response.setContentType("application/json;charset=UTF-8");
                            objectMapper.writeValue(response.getWriter(),
                                    Map.of("message", "Bu amal uchun sizda ruxsat yo'q!"));
                        })
                )

                .authorizeHttpRequests(auth -> auth
                        // Login va CORS preflight - hamma uchun ochiq
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Swagger (hujjatlar) - ochiq
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs", "/v3/api-docs/**").permitAll()
                        // Yuklangan fayllar (rasm/hujjat ko'rsatish) - ochiq
                        .requestMatchers("/uploads/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/javoblar/savol/{savolId}", "/api/savollar", "/api/savollar/statistika", "/api/savollar/{id}", "/api/users/{id}", "/api/xabarlar/conversation", "/api/xabarlar/inbox").hasAnyRole("BATALYON", "FAKULTET_BOSHLIGHI", "FAKULTET_BOSHLIGHI_ORINBOSARI", "INSON_RESURSLARI", "KAFEDRA_BOSHLIGHI", "KURSANT", "MAGISTRATURA_BOSHLIGHI", "MAGISTRATURA_BOSHLIGHI_ORINBOSARI", "NAVBATCHILIK_QISMI", "OQITUVCHI", "OQUV_BOLIMI_BOSHLIGHI", "RAHBARIYAT", "SHAXSIY_XAVFSIZLIK", "SUPER_ADMIN", "TALIM_SIFATI_NAZORAT", "TIBBIY_XIZMAT")
                        .requestMatchers(HttpMethod.GET, "/api/amaliyot-jurnal", "/api/dars-jurnali", "/api/kurs-ishi-jurnal", "/api/mustaqil-talim-jurnal", "/api/seminar-jurnal").hasAnyRole("BATALYON", "FAKULTET_BOSHLIGHI", "FAKULTET_BOSHLIGHI_ORINBOSARI", "KAFEDRA_BOSHLIGHI", "KURSANT", "OQITUVCHI", "OQUV_BOLIMI_BOSHLIGHI", "SUPER_ADMIN", "TALIM_SIFATI_NAZORAT")
                        .requestMatchers(HttpMethod.GET, "/api/dars-jurnali/mavzular", "/api/mustaqil-talim-topshiriqlar", "/api/mustaqil-talim-topshiriqlar/holati", "/api/mustaqil-talim-topshiriqlar/yuborish/{topshiriqYuborishId}/javoblar").hasAnyRole("KAFEDRA_BOSHLIGHI", "OQITUVCHI", "SUPER_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/baholash-hisobotlari/fan-boyicha", "/api/baholash-hisobotlari/individual", "/api/baholash-hisobotlari/kurs-guruh-boyicha", "/api/baholash-hisobotlari/past-ozlashtiruvchilar").hasAnyRole("FAKULTET_BOSHLIGHI", "FAKULTET_BOSHLIGHI_ORINBOSARI", "RAHBARIYAT", "SHAXSIY_XAVFSIZLIK", "SUPER_ADMIN", "TALIM_SIFATI_NAZORAT")
                        .requestMatchers(HttpMethod.GET, "/api/groups/biriktirilmagan-kursantlar", "/api/groups/kursant-guruhlari", "/api/kursdan-kursga-kochirish", "/api/reyting-daftarchasi").hasAnyRole("FAKULTET_BOSHLIGHI", "FAKULTET_BOSHLIGHI_ORINBOSARI", "SUPER_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/students", "/api/students/{id}", "/api/users").hasAnyRole("BATALYON", "FAKULTET_BOSHLIGHI", "FAKULTET_BOSHLIGHI_ORINBOSARI", "INSON_RESURSLARI", "KAFEDRA_BOSHLIGHI", "KURSANT", "NAVBATCHILIK_QISMI", "OQITUVCHI", "OQUV_BOLIMI_BOSHLIGHI", "RAHBARIYAT", "SHAXSIY_XAVFSIZLIK", "SUPER_ADMIN", "TALIM_SIFATI_NAZORAT", "TIBBIY_XIZMAT")
                        .requestMatchers(HttpMethod.GET, "/api/courses", "/api/dars-jadvali", "/api/dars-jadvali/{id}/fayl").hasAnyRole("FAKULTET_BOSHLIGHI", "FAKULTET_BOSHLIGHI_ORINBOSARI", "INSON_RESURSLARI", "KAFEDRA_BOSHLIGHI", "KURSANT", "OQITUVCHI", "OQUV_BOLIMI_BOSHLIGHI", "RAHBARIYAT", "SHAXSIY_XAVFSIZLIK", "SUPER_ADMIN", "TALIM_SIFATI_NAZORAT")
                        .requestMatchers(HttpMethod.GET, "/api/komplekslar", "/api/komplekslar/{id}").hasAnyRole("FAKULTET_BOSHLIGHI", "FAKULTET_BOSHLIGHI_ORINBOSARI", "KAFEDRA_BOSHLIGHI", "KURSANT", "NAVBATCHILIK_QISMI", "OQITUVCHI", "OQUV_BOLIMI_BOSHLIGHI", "RAHBARIYAT", "SHAXSIY_XAVFSIZLIK", "SUPER_ADMIN", "TALIM_SIFATI_NAZORAT", "TIBBIY_XIZMAT")
                        .requestMatchers(HttpMethod.GET, "/api/fanlar", "/api/groups").hasAnyRole("FAKULTET_BOSHLIGHI", "FAKULTET_BOSHLIGHI_ORINBOSARI", "KAFEDRA_BOSHLIGHI", "OQUV_BOLIMI_BOSHLIGHI", "RAHBARIYAT", "SHAXSIY_XAVFSIZLIK", "SUPER_ADMIN", "TALIM_SIFATI_NAZORAT")
                        .requestMatchers(HttpMethod.GET, "/api/groups/by-course/{courseId}", "/api/oqituvchi-fan-taqsimlash/oraliq-yakuniy-ruxsat").hasAnyRole("FAKULTET_BOSHLIGHI", "FAKULTET_BOSHLIGHI_ORINBOSARI", "KAFEDRA_BOSHLIGHI", "SUPER_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/mustaqil-talim-topshiriqlar/kursant", "/api/mustaqil-talim-topshiriqlar/kursant/fanlar").hasAnyRole("KURSANT", "SUPER_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/institutdan-chiqishlar").hasAnyRole("BATALYON", "FAKULTET_BOSHLIGHI", "FAKULTET_BOSHLIGHI_ORINBOSARI", "INSON_RESURSLARI", "NAVBATCHILIK_QISMI", "OQUV_BOLIMI_BOSHLIGHI", "RAHBARIYAT", "SHAXSIY_XAVFSIZLIK", "SUPER_ADMIN", "TALIM_SIFATI_NAZORAT")
                        .requestMatchers(HttpMethod.GET, "/api/oquv-yillari").hasAnyRole("BATALYON", "FAKULTET_BOSHLIGHI", "FAKULTET_BOSHLIGHI_ORINBOSARI", "INSON_RESURSLARI", "KAFEDRA_BOSHLIGHI", "KURSANT", "OQITUVCHI", "OQUV_BOLIMI_BOSHLIGHI", "RAHBARIYAT", "SHAXSIY_XAVFSIZLIK", "SUPER_ADMIN", "TALIM_SIFATI_NAZORAT")
                        .requestMatchers(HttpMethod.GET, "/api/sutkalik-naryadlar").hasAnyRole("BATALYON", "FAKULTET_BOSHLIGHI", "FAKULTET_BOSHLIGHI_ORINBOSARI", "INSON_RESURSLARI", "KAFEDRA_BOSHLIGHI", "KURSANT", "NAVBATCHILIK_QISMI", "OQITUVCHI", "OQUV_BOLIMI_BOSHLIGHI", "RAHBARIYAT", "SHAXSIY_XAVFSIZLIK", "SUPER_ADMIN", "TALIM_SIFATI_NAZORAT")
                        .requestMatchers(HttpMethod.GET, "/api/kasallar").hasAnyRole("BATALYON", "FAKULTET_BOSHLIGHI", "FAKULTET_BOSHLIGHI_ORINBOSARI", "INSON_RESURSLARI", "KAFEDRA_BOSHLIGHI", "KURSANT", "OQITUVCHI", "OQUV_BOLIMI_BOSHLIGHI", "RAHBARIYAT", "SHAXSIY_XAVFSIZLIK", "SUPER_ADMIN", "TALIM_SIFATI_NAZORAT", "TIBBIY_XIZMAT")
                        .requestMatchers(HttpMethod.GET, "/api/oqituvchi-fan-taqsimlash").hasAnyRole("BATALYON", "FAKULTET_BOSHLIGHI", "FAKULTET_BOSHLIGHI_ORINBOSARI", "KAFEDRA_BOSHLIGHI", "KURSANT", "NAVBATCHILIK_QISMI", "OQITUVCHI", "OQUV_BOLIMI_BOSHLIGHI", "RAHBARIYAT", "SHAXSIY_XAVFSIZLIK", "SUPER_ADMIN", "TALIM_SIFATI_NAZORAT", "TIBBIY_XIZMAT")
                        .requestMatchers(HttpMethod.GET, "/api/fan-taqsimlash").hasAnyRole("BATALYON", "FAKULTET_BOSHLIGHI", "FAKULTET_BOSHLIGHI_ORINBOSARI", "KAFEDRA_BOSHLIGHI", "KURSANT", "OQUV_BOLIMI_BOSHLIGHI", "RAHBARIYAT", "SHAXSIY_XAVFSIZLIK", "SUPER_ADMIN", "TALIM_SIFATI_NAZORAT")
                        .requestMatchers(HttpMethod.GET, "/api/users/oqituvchilar").hasAnyRole("KAFEDRA_BOSHLIGHI", "SUPER_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/tarkibiy-tuzilmalar/kafedralar").hasAnyRole("FAKULTET_BOSHLIGHI", "FAKULTET_BOSHLIGHI_ORINBOSARI", "KAFEDRA_BOSHLIGHI", "OQUV_BOLIMI_BOSHLIGHI", "SUPER_ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/amaliyot-jurnal", "/api/dars-jurnali", "/api/dars-jurnali/{id}/topshiriq", "/api/kurs-ishi-jurnal", "/api/mustaqil-talim-topshiriqlar", "/api/mustaqil-talim-topshiriqlar/javob/{javobId}/qaytarish", "/api/mustaqil-talim-topshiriqlar/yuborish", "/api/mustaqil-talim-topshiriqlar/{id}/fayl", "/api/seminar-jurnal/dars").hasAnyRole("KAFEDRA_BOSHLIGHI", "OQITUVCHI", "SUPER_ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/groups", "/api/kursdan-kursga-kochirish/arxivlash", "/api/kursdan-kursga-kochirish/chetlatish", "/api/kursdan-kursga-kochirish/kochirish", "/api/oquv-yillari").hasAnyRole("FAKULTET_BOSHLIGHI", "FAKULTET_BOSHLIGHI_ORINBOSARI", "SUPER_ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/javoblar", "/api/savollar", "/api/savollar/{id}/fayl", "/api/xabarlar").hasAnyRole("BATALYON", "FAKULTET_BOSHLIGHI", "FAKULTET_BOSHLIGHI_ORINBOSARI", "INSON_RESURSLARI", "KAFEDRA_BOSHLIGHI", "KURSANT", "MAGISTRATURA_BOSHLIGHI", "MAGISTRATURA_BOSHLIGHI_ORINBOSARI", "NAVBATCHILIK_QISMI", "OQITUVCHI", "OQUV_BOLIMI_BOSHLIGHI", "RAHBARIYAT", "SHAXSIY_XAVFSIZLIK", "SUPER_ADMIN", "TALIM_SIFATI_NAZORAT", "TIBBIY_XIZMAT")
                        .requestMatchers(HttpMethod.POST, "/api/students", "/api/students/{id}/photo").hasAnyRole("INSON_RESURSLARI", "SUPER_ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/institutdan-chiqishlar", "/api/sutkalik-naryadlar").hasAnyRole("BATALYON", "NAVBATCHILIK_QISMI", "SUPER_ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/fan-taqsimlash", "/api/fanlar").hasAnyRole("OQUV_BOLIMI_BOSHLIGHI", "SUPER_ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/dars-jadvali").hasAnyRole("FAKULTET_BOSHLIGHI", "FAKULTET_BOSHLIGHI_ORINBOSARI", "INSON_RESURSLARI", "KAFEDRA_BOSHLIGHI", "KURSANT", "OQITUVCHI", "OQUV_BOLIMI_BOSHLIGHI", "RAHBARIYAT", "SHAXSIY_XAVFSIZLIK", "SUPER_ADMIN", "TALIM_SIFATI_NAZORAT")
                        .requestMatchers(HttpMethod.POST, "/api/komplekslar").hasAnyRole("FAKULTET_BOSHLIGHI", "FAKULTET_BOSHLIGHI_ORINBOSARI", "KAFEDRA_BOSHLIGHI", "KURSANT", "NAVBATCHILIK_QISMI", "OQITUVCHI", "OQUV_BOLIMI_BOSHLIGHI", "RAHBARIYAT", "SHAXSIY_XAVFSIZLIK", "SUPER_ADMIN", "TALIM_SIFATI_NAZORAT", "TIBBIY_XIZMAT")
                        .requestMatchers(HttpMethod.POST, "/api/kasallar").hasAnyRole("SUPER_ADMIN", "TIBBIY_XIZMAT")
                        .requestMatchers(HttpMethod.POST, "/api/baholash-hisobotlari/ogohlantirish-yuborish").hasAnyRole("FAKULTET_BOSHLIGHI", "FAKULTET_BOSHLIGHI_ORINBOSARI", "RAHBARIYAT", "SHAXSIY_XAVFSIZLIK", "SUPER_ADMIN", "TALIM_SIFATI_NAZORAT")
                        .requestMatchers(HttpMethod.POST, "/api/oqituvchi-fan-taqsimlash").hasAnyRole("KAFEDRA_BOSHLIGHI", "SUPER_ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/mustaqil-talim-topshiriqlar/yuborish/{topshiriqYuborishId}/javob").hasAnyRole("KURSANT", "SUPER_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/amaliyot-jurnal/baho/{amaliyotBahoId}", "/api/amaliyot-jurnal/{amaliyotId}", "/api/dars-jurnali/davomat/{davomatId}", "/api/dars-jurnali/{id}", "/api/dars-jurnali/{id}/sana", "/api/kurs-ishi-jurnal/baho/{kursIshiBahoId}", "/api/kurs-ishi-jurnal/{kursIshiId}", "/api/mustaqil-talim-topshiriqlar/javob/{javobId}/baholash", "/api/mustaqil-talim-topshiriqlar/{id}", "/api/seminar-jurnal/davomat/{davomatId}", "/api/seminar-jurnal/oraliq-nazorat", "/api/seminar-jurnal/yakuniy-nazorat").hasAnyRole("KAFEDRA_BOSHLIGHI", "OQITUVCHI", "SUPER_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/groups/{guruhId}/kursant-biriktirish/{studentId}", "/api/groups/{id}", "/api/oquv-yillari/{id}/faol-qilish", "/api/oquv-yillari/{id}/tahrir-ruxsatini-yopish", "/api/oquv-yillari/{id}/tahrirga-ruxsat-berish").hasAnyRole("FAKULTET_BOSHLIGHI", "FAKULTET_BOSHLIGHI_ORINBOSARI", "SUPER_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/oqituvchi-fan-taqsimlash/{id}/oraliq-ruxsat-bekor-qilish", "/api/oqituvchi-fan-taqsimlash/{id}/oraliq-ruxsat-berish", "/api/oqituvchi-fan-taqsimlash/{id}/yakuniy-ruxsat-bekor-qilish", "/api/oqituvchi-fan-taqsimlash/{id}/yakuniy-ruxsat-berish").hasAnyRole("FAKULTET_BOSHLIGHI", "FAKULTET_BOSHLIGHI_ORINBOSARI", "KAFEDRA_BOSHLIGHI", "SUPER_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/users/{id}/update-contacts", "/api/xabarlar/{id}/read").hasAnyRole("BATALYON", "FAKULTET_BOSHLIGHI", "FAKULTET_BOSHLIGHI_ORINBOSARI", "INSON_RESURSLARI", "KAFEDRA_BOSHLIGHI", "KURSANT", "MAGISTRATURA_BOSHLIGHI", "MAGISTRATURA_BOSHLIGHI_ORINBOSARI", "NAVBATCHILIK_QISMI", "OQITUVCHI", "OQUV_BOLIMI_BOSHLIGHI", "RAHBARIYAT", "SHAXSIY_XAVFSIZLIK", "SUPER_ADMIN", "TALIM_SIFATI_NAZORAT", "TIBBIY_XIZMAT")
                        .requestMatchers(HttpMethod.PUT, "/api/institutdan-chiqishlar/{id}", "/api/sutkalik-naryadlar/{id}").hasAnyRole("BATALYON", "NAVBATCHILIK_QISMI", "SUPER_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/fan-taqsimlash/{id}", "/api/fanlar/{id}").hasAnyRole("OQUV_BOLIMI_BOSHLIGHI", "SUPER_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/students/{id}").hasAnyRole("INSON_RESURSLARI", "SUPER_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/komplekslar/{id}").hasAnyRole("FAKULTET_BOSHLIGHI", "FAKULTET_BOSHLIGHI_ORINBOSARI", "KAFEDRA_BOSHLIGHI", "KURSANT", "NAVBATCHILIK_QISMI", "OQITUVCHI", "OQUV_BOLIMI_BOSHLIGHI", "RAHBARIYAT", "SHAXSIY_XAVFSIZLIK", "SUPER_ADMIN", "TALIM_SIFATI_NAZORAT", "TIBBIY_XIZMAT")
                        .requestMatchers(HttpMethod.PUT, "/api/kasallar/{id}").hasAnyRole("SUPER_ADMIN", "TIBBIY_XIZMAT")
                        .requestMatchers(HttpMethod.PUT, "/api/oqituvchi-fan-taqsimlash/{id}").hasAnyRole("KAFEDRA_BOSHLIGHI", "SUPER_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/amaliyot-jurnal/{amaliyotId}", "/api/dars-jurnali/{id}", "/api/kurs-ishi-jurnal/{kursIshiId}", "/api/mustaqil-talim-topshiriqlar/fayl/{faylId}", "/api/mustaqil-talim-topshiriqlar/{id}", "/api/seminar-jurnal/dars/{darsJurnaliId}").hasAnyRole("KAFEDRA_BOSHLIGHI", "OQITUVCHI", "SUPER_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/javoblar/{id}", "/api/savollar/{id}", "/api/xabarlar/{id}").hasAnyRole("BATALYON", "FAKULTET_BOSHLIGHI", "FAKULTET_BOSHLIGHI_ORINBOSARI", "INSON_RESURSLARI", "KAFEDRA_BOSHLIGHI", "KURSANT", "MAGISTRATURA_BOSHLIGHI", "MAGISTRATURA_BOSHLIGHI_ORINBOSARI", "NAVBATCHILIK_QISMI", "OQITUVCHI", "OQUV_BOLIMI_BOSHLIGHI", "RAHBARIYAT", "SHAXSIY_XAVFSIZLIK", "SUPER_ADMIN", "TALIM_SIFATI_NAZORAT", "TIBBIY_XIZMAT")
                        .requestMatchers(HttpMethod.DELETE, "/api/students/{id}", "/api/users/{id}").hasAnyRole("INSON_RESURSLARI", "SUPER_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/komplekslar/{id}", "/api/komplekslar/{kompleksId}/fayl/{faylId}").hasAnyRole("FAKULTET_BOSHLIGHI", "FAKULTET_BOSHLIGHI_ORINBOSARI", "KAFEDRA_BOSHLIGHI", "KURSANT", "NAVBATCHILIK_QISMI", "OQITUVCHI", "OQUV_BOLIMI_BOSHLIGHI", "RAHBARIYAT", "SHAXSIY_XAVFSIZLIK", "SUPER_ADMIN", "TALIM_SIFATI_NAZORAT", "TIBBIY_XIZMAT")
                        .requestMatchers(HttpMethod.DELETE, "/api/fan-taqsimlash/{id}", "/api/fanlar/{id}").hasAnyRole("OQUV_BOLIMI_BOSHLIGHI", "SUPER_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/groups/{guruhId}/kursant-chiqarish/{studentId}", "/api/groups/{id}").hasAnyRole("FAKULTET_BOSHLIGHI", "FAKULTET_BOSHLIGHI_ORINBOSARI", "SUPER_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/dars-jadvali/{id}").hasAnyRole("FAKULTET_BOSHLIGHI", "FAKULTET_BOSHLIGHI_ORINBOSARI", "INSON_RESURSLARI", "KAFEDRA_BOSHLIGHI", "KURSANT", "OQITUVCHI", "OQUV_BOLIMI_BOSHLIGHI", "RAHBARIYAT", "SHAXSIY_XAVFSIZLIK", "SUPER_ADMIN", "TALIM_SIFATI_NAZORAT")
                        .requestMatchers(HttpMethod.DELETE, "/api/institutdan-chiqishlar/{id}").hasAnyRole("BATALYON", "NAVBATCHILIK_QISMI", "SUPER_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/oqituvchi-fan-taqsimlash/{id}").hasAnyRole("KAFEDRA_BOSHLIGHI", "SUPER_ADMIN")
                        // Ro'yxatda aniq ko'rsatilmagan qolgan endpointlar -
                        // hozircha faqat "tizimga kirgan bo'lishi" talab qilinadi
                        .anyRequest().authenticated()
                )

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}