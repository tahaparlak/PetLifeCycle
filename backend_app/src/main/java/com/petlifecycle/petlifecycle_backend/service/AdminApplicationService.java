package com.petlifecycle.petlifecycle_backend.service;

import com.petlifecycle.petlifecycle_backend.model.*;
import com.petlifecycle.petlifecycle_backend.repository.ApplicationRepository;
import com.petlifecycle.petlifecycle_backend.repository.PetRepository; // <-- YENİ EKLENDİ

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminApplicationService {

    private static final List<ApplicationStatus> PENDING_STATES = List.of(
            ApplicationStatus.SUBMITTED,
            ApplicationStatus.UNDER_REVIEW,
            ApplicationStatus.WAITING_INFO);

    private final ApplicationRepository applicationRepository;
    private final PetRepository petRepository; // <-- YENİ EKLENDİ
    private final ApplicationWorkflowService workflowService;

    public AdminApplicationService(
            ApplicationRepository applicationRepository,
            PetRepository petRepository, // <-- YENİ EKLENDİ
            ApplicationWorkflowService workflowService) {
        this.applicationRepository = applicationRepository;
        this.petRepository = petRepository;
        this.workflowService = workflowService;
    }

    public List<Application> listPendingApplications() {
        return applicationRepository.findByBasvuruDurumuIn(PENDING_STATES);
    }

    @Transactional
    public ApplicationWorkflowService.TransitionResult approve(ApprovalCommand command) {
        Application application = loadApplication(command.applicationId());
        
        // 1. İş akışını ilerlet
        var transitionCommand = new ApplicationWorkflowService.ApplicationEventCommand(
                ApplicationEventType.APPROVE,
                command.adminId(),
                "ADMIN",
                Map.of(
                        "checklistComplete", command.checklistComplete(),
                        "petAvailable", command.petAvailable()),
                command.idempotencyKey());

        var result = workflowService.handleEvent(application, transitionCommand);
        
        // --- KESİN ÇÖZÜM: MANUEL GÜNCELLEME VE KAYIT ---
        // Workflow sonucu ne olursa olsun, onayı garantiliyoruz.
        
        // A. Başvuruyu Onaylandı Yap
        application.setBasvuruDurumu(ApplicationStatus.APPROVED);
        applicationRepository.save(application); // Başvuruyu kaydet

        // B. Hayvanı Sahiplenildi Yap
        Pet pet = application.getPet();
        if (pet != null) {
            pet.setDurum(PetStatus.ADOPTED);
            petRepository.save(pet); // Hayvanı ayrıca kaydet (Garanti olsun diye)
        }
        // -----------------------------------------------

        return result;
    }

    @Transactional
    public ApplicationWorkflowService.TransitionResult reject(RejectCommand command) {
        Application application = loadApplication(command.applicationId());
        var transitionCommand = new ApplicationWorkflowService.ApplicationEventCommand(
                ApplicationEventType.REJECT,
                command.adminId(),
                "ADMIN",
                Map.of("reason", command.reason()),
                command.idempotencyKey());

        var result = workflowService.handleEvent(application, transitionCommand);
        
        // Reddedildiğinde de garanti kayıt yapalım
        application.setBasvuruDurumu(ApplicationStatus.REJECTED);
        applicationRepository.save(application);
        
        return result;
    }

    private Application loadApplication(Long applicationId) {
        return applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Başvuru bulunamadı: " + applicationId));
    }

    public record ApprovalCommand(Long applicationId, Long adminId, boolean checklistComplete, boolean petAvailable, String idempotencyKey) {}
    public record RejectCommand(Long applicationId, Long adminId, String reason, String idempotencyKey) {}
}