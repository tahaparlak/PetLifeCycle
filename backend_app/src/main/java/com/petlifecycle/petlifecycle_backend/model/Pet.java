
package com.petlifecycle.petlifecycle_backend.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String isim;

    @Enumerated(EnumType.STRING)
    private PetType tur; 

    private Integer yas;
    private Double kilo;

    @Enumerated(EnumType.STRING)
    private PetStatus durum; 

    @Enumerated(EnumType.STRING)
    private Gender cinsiyet; // ERKEK, DISI

    private String asiDurumu;

    private String resimUrl;

    // Getterlar ve Setterlar buraya eklenecek

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIsim() {
        return isim;
    }

    public void setIsim(String isim) {
        this.isim = isim;
    }

    public PetType getTur() {
        return tur;
    }

    public void setTur(PetType tur) {
        this.tur = tur;
    }

    public Integer getYas() {
        return yas;
    }

    public void setYas(Integer yas) {
        this.yas = yas;
    }

    public Double getKilo() {
        return kilo;
    }

    public void setKilo(Double kilo) {
        this.kilo = kilo;
    }

    public Gender getCinsiyet() {
        return cinsiyet;
    }

    public void setCinsiyet(Gender cinsiyet) {
        this.cinsiyet = cinsiyet;
    }

    public String getAsiDurumu() {
        return asiDurumu;
    }

    public void setAsiDurumu(String asiDurumu) {
        this.asiDurumu = asiDurumu;
    }

    public PetStatus getDurum() {
        return durum;
    }

    public void setDurum(PetStatus durum) {
        this.durum = durum;
    }

    public String getResimUrl() {
        return resimUrl;
    }

    public void setResimUrl(String resimUrl) {
        this.resimUrl = resimUrl;
    }
    }