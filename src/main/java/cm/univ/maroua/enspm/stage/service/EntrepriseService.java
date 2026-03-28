package cm.univ.maroua.enspm.stage.service;

import cm.univ.maroua.enspm.stage.domain.Entreprise;
import cm.univ.maroua.enspm.stage.repository.EntrepriseRepository;
import cm.univ.maroua.enspm.stage.service.dto.EntrepriseDTO;
import cm.univ.maroua.enspm.stage.service.mapper.EntrepriseMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EntrepriseService {

    private final EntrepriseRepository entrepriseRepository;
    private final EntrepriseMapper entrepriseMapper;

    public EntrepriseService(EntrepriseRepository entrepriseRepository, EntrepriseMapper entrepriseMapper) {
        this.entrepriseRepository = entrepriseRepository;
        this.entrepriseMapper = entrepriseMapper;
    }

    public Page<EntrepriseDTO> findAll(Pageable pageable) {
        Pageable pg = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("nom"));
        return entrepriseRepository.findAll(pg).map(entrepriseMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<EntrepriseDTO> findOne(Long id) {
        return entrepriseRepository.findById(id).map(entrepriseMapper::toDto);
    }

    public EntrepriseDTO save(EntrepriseDTO entrepriseDTO) {
        Entreprise entreprise = entrepriseMapper.toEntity(entrepriseDTO);
        entreprise = entrepriseRepository.save(entreprise);
        return entrepriseMapper.toDto(entreprise);
    }

    public void delete(Long id) {
        entrepriseRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<EntrepriseDTO> rechercheParNom(String q) {
        return entrepriseRepository.findByNomContainingIgnoreCase(q)
                .stream().map(entrepriseMapper::toDto).toList();
    }

    public EntrepriseDTO rechercheOuCree(String nom, String secteur) {
        return entrepriseRepository.findByNomIgnoreCase(nom.trim())
                .map(entrepriseMapper::toDto)
                .orElseGet(() -> {
                    Entreprise e = new Entreprise();
                    e.setNom(nom.trim());
                    e.setSecteur(secteur);
                    return entrepriseMapper.toDto(entrepriseRepository.save(e));
                });
    }
}
