package cm.univ.maroua.enspm.stage.service;

import cm.univ.maroua.enspm.stage.domain.Bareme;
import cm.univ.maroua.enspm.stage.repository.BaremeRepository;
import cm.univ.maroua.enspm.stage.service.dto.BaremeDTO;
import cm.univ.maroua.enspm.stage.service.mapper.BaremeMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class BaremeService {

    private final BaremeRepository baremeRepository;
    private final BaremeMapper baremeMapper;

    public BaremeService(BaremeRepository baremeRepository, BaremeMapper baremeMapper) {
        this.baremeRepository = baremeRepository;
        this.baremeMapper = baremeMapper;
    }

    public Page<BaremeDTO> findAll(Pageable pageable) {
        return baremeRepository.findAll(pageable).map(baremeMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<BaremeDTO> findOne(Long id) {
        return baremeRepository.findById(id).map(baremeMapper::toDto);
    }

    public BaremeDTO save(BaremeDTO baremeDTO) {
        Bareme bareme = baremeMapper.toEntity(baremeDTO);
        if (bareme.getActif() == null) {
            bareme.setActif(Boolean.TRUE);
        }
        if (bareme.getParDefaut() == null) {
            bareme.setParDefaut(Boolean.FALSE);
        }
        if (Boolean.TRUE.equals(bareme.getParDefaut())) {
            baremeRepository.clearDefaultBareme();
        }
        bareme = baremeRepository.save(bareme);
        return baremeMapper.toDto(bareme);
    }

    public void delete(Long id) {
        baremeRepository.deleteById(id);
    }
}
