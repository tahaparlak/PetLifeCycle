package com.petlifecycle.petlifecycle_backend.service.strategy;

import com.petlifecycle.petlifecycle_backend.model.PetType;
import com.petlifecycle.petlifecycle_backend.service.PetHealthData;
import com.petlifecycle.petlifecycle_backend.service.VaccineEvent;
import com.petlifecycle.petlifecycle_backend.service.VaccineScheduleResult;
import com.petlifecycle.petlifecycle_backend.service.VaccineStatus;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ExoticPetStrategy implements PetHealthStrategy {

    @Override
    public List<VaccineScheduleResult> analyze(PetHealthData pet, List<VaccineEvent> history) {
        List<VaccineScheduleResult> results = new ArrayList<>();
        LocalDate today = LocalDate.now();

        // TÜR BAZLI AYRIŞTIRMA
        if (pet.species() == PetType.KUS) {
            // 1. GAGA BAKIMI (6 Ayda Bir)
            results.add(createRoutineCheck("GAGA-BAKIMI", 180, today));
            
            // 2. TÜY VE PARAZİT KONTROLÜ (3 Ayda Bir)
            results.add(createRoutineCheck("TUY-PARAZIT-KONTROL", 90, today));
            
            // 3. VİTAMİN DESTEĞİ (Aylık)
            results.add(createRoutineCheck("VITAMIN-DESTEGI", 30, today));

        } else if (pet.species() == PetType.TAVSAN) {
            // 1. DİŞ KONTROLÜ (Çok Kritik - 2 Ayda Bir)
            // Tavşanların dişleri sürekli uzar, kontrol şart.
            results.add(createRoutineCheck("DIS-KONTROLU", 60, today));
            
            // 2. SİNDİRİM SİSTEMİ KONTROLÜ (6 Ayda Bir)
            results.add(createRoutineCheck("SINDIRIM-KONTROLU", 180, today));

        } else {
            // DİĞER EGZOTİKLER (Sürüngen vb.)
            // 1. DİYET VE ISI KONTROLÜ (Aylık)
            results.add(createRoutineCheck("DIYET-ISI-NEM-KONTROLU", 30, today));
            
            // 2. GENEL VETERİNER MUAYENESİ (Yıllık)
            results.add(createRoutineCheck("GENEL-MUAYENE", 365, today));
        }

        return results;
    }

    // Yardımcı Metod: Basit periyodik bakım oluşturur
    private VaccineScheduleResult createRoutineCheck(String code, int intervalDays, LocalDate today) {
        // Varsayım: Daha önce hiç yapılmadıysa bugün yapılmalı.
        // Geliştirme: history listesine bakılıp en son ne zaman yapıldığına göre tarih ileri atılabilir.
        // Şimdilik "Başlangıç Paketi" olarak bugüne atıyoruz.
        
        return new VaccineScheduleResult(
                code,
                VaccineStatus.SCHEDULED,
                today.plusDays(intervalDays), // İlkini hemen değil, bir periyot sonrasına da atabiliriz ama
                                              // genelde sahiplenme anında bir kontrol istenir.
                                              // Biz şimdilik "Bugün + Periyot" yapıyoruz.
                "Periyodik Bakım (" + intervalDays + " günde bir)",
                null // Rule objesi egzotiklerde opsiyonel
        );
    }
}