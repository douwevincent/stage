package cm.univ.maroua.enspm.stage.service;

import cm.univ.maroua.enspm.stage.domain.Localite;
import cm.univ.maroua.enspm.stage.repository.LocaliteRepository;
import cm.univ.maroua.enspm.stage.service.dto.LocaliteDTO;
import cm.univ.maroua.enspm.stage.service.mapper.LocaliteMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class LocaliteService {

    private final LocaliteRepository localiteRepository;
    private final LocaliteMapper localiteMapper;

    public LocaliteService(LocaliteRepository localiteRepository, LocaliteMapper localiteMapper) {
        this.localiteRepository = localiteRepository;
        this.localiteMapper = localiteMapper;
    }

    public Page<LocaliteDTO> findAll(Pageable pageable) {
        return localiteRepository.findAll(pageable).map(localiteMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<LocaliteDTO> findOne(Long id) {
        return localiteRepository.findById(id).map(localiteMapper::toDto);
    }

    public LocaliteDTO save(LocaliteDTO localiteDTO) {
        Localite localite = localiteMapper.toEntity(localiteDTO);
        localite = localiteRepository.save(localite);
        return localiteMapper.toDto(localite);
    }

    public void delete(Long id) {
        localiteRepository.deleteById(id);
    }
}
