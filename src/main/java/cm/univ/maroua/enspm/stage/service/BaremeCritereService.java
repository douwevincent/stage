package cm.univ.maroua.enspm.stage.service;

import cm.univ.maroua.enspm.stage.domain.BaremeCritere;
import cm.univ.maroua.enspm.stage.repository.BaremeCritereRepository;
import cm.univ.maroua.enspm.stage.service.dto.BaremeCritereDTO;
import cm.univ.maroua.enspm.stage.service.mapper.BaremeCritereMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class BaremeCritereService {

    private final BaremeCritereRepository baremeCritereRepository;
    private final BaremeCritereMapper baremeCritereMapper;

    public BaremeCritereService(BaremeCritereRepository baremeCritereRepository, BaremeCritereMapper baremeCritereMapper) {
        this.baremeCritereRepository = baremeCritereRepository;
        this.baremeCritereMapper = baremeCritereMapper;
    }

    public Page<BaremeCritereDTO> findAll(Pageable pageable, Long baremeId) {
        if (baremeId != null) {
            return baremeCritereRepository.findAllByBaremeId(baremeId, pageable).map(baremeCritereMapper::toDto);
        }
        return baremeCritereRepository.findAll(pageable).map(baremeCritereMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<BaremeCritereDTO> findOne(Long id) {
        return baremeCritereRepository.findById(id).map(baremeCritereMapper::toDto);
    }

    public BaremeCritereDTO save(BaremeCritereDTO baremeCritereDTO) {
        BaremeCritere baremeCritere = baremeCritereMapper.toEntity(baremeCritereDTO);
        baremeCritere = baremeCritereRepository.save(baremeCritere);
        return baremeCritereMapper.toDto(baremeCritere);
    }

    public void delete(Long id) {
        baremeCritereRepository.deleteById(id);
    }
}
