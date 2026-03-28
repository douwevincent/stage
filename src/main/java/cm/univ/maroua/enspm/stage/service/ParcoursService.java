package cm.univ.maroua.enspm.stage.service;

import cm.univ.maroua.enspm.stage.domain.Parcours;
import cm.univ.maroua.enspm.stage.repository.ParcoursRepository;
import cm.univ.maroua.enspm.stage.service.dto.ParcoursDTO;
import cm.univ.maroua.enspm.stage.service.mapper.ParcoursMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class ParcoursService {

    private final ParcoursRepository parcoursRepository;
    private final ParcoursMapper parcoursMapper;

    public ParcoursService(ParcoursRepository parcoursRepository, ParcoursMapper parcoursMapper) {
        this.parcoursRepository = parcoursRepository;
        this.parcoursMapper = parcoursMapper;
    }

    public Page<ParcoursDTO> findAll(Pageable pageable) {
        return parcoursRepository.findAll(pageable).map(parcoursMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<ParcoursDTO> findAll(Pageable pageable, Long departementId, Long specialiteId, Long niveauId, String q) {
        Specification<Parcours> spec = (root, query, cb) -> cb.conjunction();

        if (departementId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("specialite").get("departement").get("id"), departementId));
        }

        if (specialiteId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("specialite").get("id"), specialiteId));
        }

        if (niveauId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("niveau").get("id"), niveauId));
        }

        if (q != null && !q.isBlank()) {
            String search = "%" + q.trim().toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("specialite").get("departement").get("code")), search),
                    cb.like(cb.lower(root.get("specialite").get("departement").get("intitule")), search),
                    cb.like(cb.lower(root.get("specialite").get("code")), search),
                    cb.like(cb.lower(root.get("specialite").get("intitule")), search),
                    cb.like(cb.lower(root.get("niveau").get("libelle")), search)));
        }

        return parcoursRepository.findAll(spec, pageable).map(parcoursMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<ParcoursDTO> findOne(Long id) {
        return parcoursRepository.findById(id).map(parcoursMapper::toDto);
    }

    public ParcoursDTO save(ParcoursDTO parcoursDTO) {
        Parcours parcours = parcoursMapper.toEntity(parcoursDTO);
        parcours = parcoursRepository.save(parcours);
        return parcoursMapper.toDto(parcours);
    }

    public void delete(Long id) {
        parcoursRepository.deleteById(id);
    }
}
