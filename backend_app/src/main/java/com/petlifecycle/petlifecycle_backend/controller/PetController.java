package com.petlifecycle.petlifecycle_backend.controller;

import com.petlifecycle.petlifecycle_backend.model.*;
import com.petlifecycle.petlifecycle_backend.repository.*;
import com.petlifecycle.petlifecycle_backend.service.*; // PetVaccineScheduler vb. için
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pets")
@CrossOrigin(origins = "*")
public class PetController {

    @Autowired 
    private VaccineScheduleRepository vaccineScheduleRepository;

    @Autowired
    private PetRepository petRepository;
    
    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private PetVaccineScheduler vaccineScheduler; // <-- EKLENDİ: Takvim hesaplayıcı

    // 1. TÜM HAYVANLARI LİSTELE
    @GetMapping
    public List<Pet> getAllPets() {
        return petRepository.findAll();
    }

    // 2. TEK BİR HAYVANIN DETAYINI GETİR
    @GetMapping("/{id}")
    public ResponseEntity<Pet> getPetById(@PathVariable Long id) {
        return petRepository.findById(id)
                .map(pet -> ResponseEntity.ok(pet))
                .orElse(ResponseEntity.notFound().build());
    }

    // 3. YENİ HAYVAN EKLE (ARTIK OTOMATİK TAKVİM OLUŞTURUYOR)
    @PostMapping
    public Pet createPet(@RequestBody Pet pet) {
        if(pet.getDurum() == null) {
            pet.setDurum(PetStatus.AVAILABLE);
        }
        
        // Önce hayvanı kaydet (ID oluşması için)
        Pet savedPet = petRepository.save(pet);
        
        // --- KRİTİK EKLENTİ: TAKVİM OLUŞTUR ---
        try {
            createScheduleForPet(savedPet);
            System.out.println("✅ " + savedPet.getIsim() + " için sağlık takvimi oluşturuldu.");
        } catch (Exception e) {
            System.err.println("❌ Takvim oluşturulurken hata: " + e.getMessage());
        }
        // ---------------------------------------

        return savedPet;
    }

    // 4. AŞI TAKVİMİNİ GETİR
    @GetMapping("/{petId}/schedule")
    public ResponseEntity<List<VaccineSchedule>> getPetSchedule(@PathVariable Long petId) {
        List<VaccineSchedule> schedule = vaccineScheduleRepository.findByPetId(petId);
        return ResponseEntity.ok(schedule);
    }

    // 5. HAYVAN SİL (DELETE)
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> deletePet(@PathVariable Long id) {
        if(!petRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        System.out.println("Hayvan siliniyor ID: " + id);

        List<VaccineSchedule> asilar = vaccineScheduleRepository.findByPetId(id);
        vaccineScheduleRepository.deleteAll(asilar);

        List<Application> basvurular = applicationRepository.findByPetId(id);
        applicationRepository.deleteAll(basvurular);

        petRepository.deleteById(id);
        return ResponseEntity.ok("Hayvan ve ilgili tüm veriler silindi.");
    }

    // 6. STATÜ GÜNCELLE (PUT)
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updatePetStatus(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        return petRepository.findById(id)
                .map(pet -> {
                    String newStatus = payload.get("status");
                    try {
                        pet.setDurum(PetStatus.valueOf(newStatus)); 
                        petRepository.save(pet);
                        return ResponseEntity.ok(pet);
                    } catch (Exception e) {
                        return ResponseEntity.badRequest().body("Hatalı statü değeri: " + newStatus);
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // --- YENİ YARDIMCI METOD (AdminService'den Taşındı) ---
    private void createScheduleForPet(Pet pet) {
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

        List<VaccineScheduleResult> results = vaccineScheduler.buildSchedule(healthData, List.of());

        for (VaccineScheduleResult res : results) {
            VaccineSchedule schedule = new VaccineSchedule();
            schedule.setPet(pet);
            schedule.setAsiAdi(res.vaccineCode()); 
            schedule.setAsiTarihi(LocalDate.now()); 
            schedule.setSonrakiTarih(res.dueDate()); 
            
            vaccineScheduleRepository.save(schedule);
        }
    }
}