# 🐾 PetLifeCycle - Pet Adoption & Management System

> **Sprint 3 Final Delivery**
> *Full-Stack Entegrasyonu, Egzotik Hayvan Stratejisi ve Gelişmiş Admin Paneli Tamamlandı.*

## 🚀 Proje Hakkında
PetLifeCycle; evcil ve egzotik hayvanların sahiplendirilme süreçlerini, sağlık takiplerini ve bakım gereksinimlerini yöneten kapsamlı bir Full-Stack web uygulamasıdır.

Bu proje, standart sahiplendirme sistemlerinden farklı olarak **Strategy Design Pattern** kullanarak her hayvan türüne (Kedi, Köpek, Tavşan, Sürüngen) özgü bakım kuralları ve validasyonlar içerir.

---

## 🛠️ Kullanılan Teknolojiler & Mimari

### Backend (Java Spring Boot)
* **Framework:** Spring Boot 3.2.0
* **Database:** H2 In-Memory Database (Kurulum gerektirmez, `DataLoader` ile otomatik başlar)
* **Design Patterns:**
    * **Strategy Pattern:** `ExoticPetStrategy` ve `DomesticPetStrategy` ile polimorfik bakım planları.
    * **Repository Pattern:** Veri erişim katmanı soyutlaması.
* **Security:** CORS Konfigürasyonu (`@CrossOrigin`), Idempotency Key (Mükerrer işlem koruması).

### Frontend (HTML5 / JS / Bootstrap)
* **UI/UX:** Responsive `Petsitting` teması üzerine dinamik entegrasyon.
* **Logic:** Fetch API ile asenkron veri iletişimi.
* **Modules:**
    * **Vitrin:** Canlı veritabanından hayvan listeleme.
    * **Başvuru Sihirbazı:** Dinamik form yönetimi.
    * **Admin Dashboard:** Bekleyen başvurular ve onay mekanizması.

---

## 🌟 Öne Çıkan Özellikler (Key Features)

### 1. Egzotik Hayvan Yönetimi (Advanced Logic)
Sistem, sıradan bir CRUD uygulamasının ötesindedir. Hayvan türüne göre arka planda çalışan algoritmalar devreye girer:
* 🐰 **Tavşanlar:** Otomatik "Diş Uzama Kontrolü" ve "Lifli Diyet" takvimi oluşturulur.
* 🦎 **Sürüngenler:** "UV-B Işık" ve "Isı Nem Dengesi" uyarıları Admin panelinde gösterilir.
* 🐶 **Evcil Hayvanlar:** Standart aşı takvimi (Karma, Kuduz vb.) uygulanır.

### 2. Güvenli Admin & Başvuru Süreci
* **Idempotency:** Admin panelinden yapılan onaylamalarda çift tıklama veya ağ hatası kaynaklı veri bozulmalarını önleyen benzersiz anahtar kontrolü vardır.
* **Fail-Safe:** Kullanıcı veritabanında olmasa bile başvuru anında "Otomatik Kullanıcı (Guest)" oluşturularak sistemin çökmesi engellenir.

### 3. Zero-Config Kurulum
Proje indirildiğinde veritabanı kurmaya gerek yoktur. `DataLoader` sınıfı sistem açılışında:
* Varsayılan Admin ve Kullanıcıları,
* Demo Hayvanları (Pamuk, Karabaş, Slytherin) belleğe yükler.

---

## ⚙️ Kurulum ve Çalıştırma

### 1. Backend'i Başlatma
Terminali açın ve proje dizininde şu komutu çalıştırın:
```bash
./mvnw spring-boot:run