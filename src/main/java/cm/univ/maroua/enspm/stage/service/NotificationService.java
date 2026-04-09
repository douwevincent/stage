package cm.univ.maroua.enspm.stage.service;

import cm.univ.maroua.enspm.stage.domain.Notification;
import cm.univ.maroua.enspm.stage.domain.TypeStage;
import cm.univ.maroua.enspm.stage.repository.NotificationRepository;
import cm.univ.maroua.enspm.stage.repository.TypeStageRepository;
import cm.univ.maroua.enspm.stage.service.dto.NotificationDTO;
import cm.univ.maroua.enspm.stage.service.mapper.NotificationMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
/**
 * Service metier NotificationService.
 */
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final TypeStageRepository typeStageRepository;

    public NotificationService(NotificationRepository notificationRepository, 
                               NotificationMapper notificationMapper,
                               TypeStageRepository typeStageRepository) {
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
        this.typeStageRepository = typeStageRepository;
    }

    public Page<NotificationDTO> findAll(Pageable pageable) {
        return notificationRepository.findAll(pageable).map(notificationMapper::toDto);
    }

    public Page<NotificationDTO> findAllByTypeStageId(Long typeStageId, Pageable pageable) {
        return notificationRepository.findByTypeStageId(typeStageId, pageable)
                .map(notificationMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<NotificationDTO> findOne(Long id) {
        return notificationRepository.findById(id).map(notificationMapper::toDto);
    }

    public NotificationDTO save(NotificationDTO notificationDTO) {
        // Validate basic constraints
        if (notificationDTO.typeStageId() == null) {
            throw new IllegalArgumentException("TypeStage ID is required");
        }
        if (notificationDTO.referenceDateType() == null) {
            throw new IllegalArgumentException("Reference date type is required");
        }
        if (notificationDTO.offsetDays() == null) {
            throw new IllegalArgumentException("Offset days is required");
        }
        
        // Validate offsetDays range
        if (notificationDTO.offsetDays() < -365 || notificationDTO.offsetDays() > 365) {
            throw new IllegalArgumentException("Offset days must be between -365 and 365");
        }
        
        // Validate that TypeStage exists
        TypeStage typeStage = typeStageRepository.findById(notificationDTO.typeStageId())
                .orElseThrow(() -> new IllegalArgumentException("TypeStage with ID " + notificationDTO.typeStageId() + " not found"));
        
        Notification notification = notificationMapper.toEntity(notificationDTO);
        notification.setTypeStage(typeStage);
        if (notification.getActif() == null) {
            notification.setActif(true);
        }
        
        notification = notificationRepository.save(notification);
        return notificationMapper.toDto(notification);
    }

    public void delete(Long id) {
        notificationRepository.deleteById(id);
    }
}
