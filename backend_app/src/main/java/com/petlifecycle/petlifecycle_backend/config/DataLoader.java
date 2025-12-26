package com.petlifecycle.petlifecycle_backend.config;

import com.petlifecycle.petlifecycle_backend.model.*;
import com.petlifecycle.petlifecycle_backend.repository.*;
import com.petlifecycle.petlifecycle_backend.service.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initDatabase(
            PetRepository petRepository, 
            UserRepository userRepository,
            PetVaccineScheduler vaccineScheduler,           // Takvim için eklendi
            VaccineScheduleRepository scheduleRepository) { // Takvim için eklendi
        
        return args -> {
            // 1. ADMIN ve USER Oluşturma
            if (userRepository.findByEmail("admin@pet.com").isEmpty()) {
                User admin = new User();
                admin.setAd("Sistem Yöneticisi");
                admin.setEmail("admin@pet.com");
                admin.setSifre("admin123");
                admin.setRol(Role.ADMIN);
                userRepository.save(admin);
                System.out.println("✅ ADMIN Eklendi: admin@pet.com");
            }

            if (userRepository.findByEmail("user@pet.com").isEmpty()) {
                User user = new User();
                user.setAd("Test Kullanıcı");
                user.setEmail("user@pet.com");
                user.setSifre("user123");
                user.setRol(Role.USER);
                userRepository.save(user);
                System.out.println("✅ USER Eklendi: user@pet.com");
            }

            // 2. 10 ADET HAYVAN OLUŞTURMA
            if (petRepository.count() == 0) {
                List<Pet> pets = new ArrayList<>();

                // Kedi & Köpekler
                pets.add(createPet("Pamuk", PetType.KEDI, 2, 4.5, Gender.DISI, "Takip Ediliyor", "images/image_1.jpg"));
                pets.add(createPet("Kar Topu", PetType.KOPEK, 4, 12.0, Gender.ERKEK, "Eksik Aşıları Var", "images/image_2.jpg"));
                pets.add(createPet("Zeytin", PetType.KEDI, 1, 3.2, Gender.DISI, "Aşıları Tam", "images/image_3.jpg"));
                pets.add(createPet("Lessie", PetType.KOPEK, 3, 15.5, Gender.DISI, "Kısırlaştırılmış", "images/image_4.jpg"));
                pets.add(createPet("Tarçın ", PetType.KEDI, 5, 5.0, Gender.ERKEK, "Tedavi Görüyor", "images/image_5.jpg"));
                pets.add(createPet("Köpük", PetType.KOPEK, 1, 6.0, Gender.ERKEK, "Yavru", "images/image_6.jpg"));
                
                // Egzotik, Kuş, Tavşan
                pets.add(createPet("Slytherin", PetType.EXOTIC, 2, 0.8, Gender.ERKEK, "Diyet Kontrolü", "images/gecko.jpg"));
                pets.add(createPet("Maviş", PetType.KUS, 1, 0.5, Gender.ERKEK, "Vitamin Takviyesi", "images/mavi.jpg"));
                pets.add(createPet("Bugs", PetType.TAVSAN, 2, 1.2, Gender.ERKEK, "Diş Kontrolü Gerekli", "images/Tavsan.webp"));
                pets.add(createPet("Limon", PetType.KUS, 3, 0.6, Gender.DISI, "Sağlıklı", "images/papagan.jpg"));

                // Hepsini Kaydet ve Takvim Oluştur
                for (Pet p : pets) {
                    petRepository.save(p);
                    createSchedule(p, vaccineScheduler, scheduleRepository);
                }
                
                System.out.println("✅ 10 Adet Hayvan ve Sağlık Takvimleri Oluşturuldu.");
            }
        };
    }

    // Hayvan Nesnesi Oluşturan Yardımcı Metod
    private Pet createPet(String isim, PetType tur, int yas, double kilo, Gender cinsiyet, String asiDurumu, String img) {
        Pet p = new Pet();
        p.setIsim(isim);
        p.setTur(tur);
        p.setYas(yas);
        p.setKilo(kilo);
        p.setCinsiyet(cinsiyet);
        p.setAsiDurumu(asiDurumu);
        p.setDurum(PetStatus.AVAILABLE);
        p.setResimUrl(img);
        return p;
    }

    // Takvim Oluşturan Yardımcı Metod (Controller'daki mantığın aynısı)
    private void createSchedule(Pet pet, PetVaccineScheduler scheduler, VaccineScheduleRepository repo) {
        List<WeightRecord> weightHistory = new ArrayList<>();
        if(pet.getKilo() != null) {
            weightHistory.add(new WeightRecord(LocalDate.now(), pet.getKilo()));
        }

        PetHealthData healthData = new PetHealthData(
                pet.getTur(), 
                LocalDate.now().minusYears(pet.getYas() != null ? pet.getYas() : 1),
                pet.getKilo(), 
                weightHistory  
        );

        List<VaccineScheduleResult> results = scheduler.buildSchedule(healthData, List.of());

        for (VaccineScheduleResult res : results) {
            VaccineSchedule schedule = new VaccineSchedule();
            schedule.setPet(pet);
            schedule.setAsiAdi(res.vaccineCode()); 
            schedule.setAsiTarihi(LocalDate.now()); 
            schedule.setSonrakiTarih(res.dueDate()); 
            repo.save(schedule);
        }
    }
}