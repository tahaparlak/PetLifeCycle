package com.petlifecycle.petlifecycle_backend.controller;

import com.petlifecycle.petlifecycle_backend.model.Pet;
import com.petlifecycle.petlifecycle_backend.model.PetType;
import com.petlifecycle.petlifecycle_backend.model.PetStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/pets") // Mert bu adresten ulaşacak
public class PetController {

    @GetMapping
    public List<Pet> getAllPets() {
        // Şimdilik Mert'in ekranı görebilmesi için sahte veriler oluşturuyoruz
        Pet pet1 = new Pet();
        pet1.setId(1L);
        pet1.setIsim("Pamuk");
        pet1.setTur(PetType.KEDI);
        pet1.setYas(2);
        pet1.setDurum(PetStatus.AVAILABLE);

        Pet pet2 = new Pet();
        pet2.setId(2L);
        pet2.setIsim("Kont");
        pet2.setTur(PetType.KOPEK);
        pet2.setYas(4);
        pet2.setDurum(PetStatus.AVAILABLE);

        return Arrays.asList(pet1, pet2);
    }
}