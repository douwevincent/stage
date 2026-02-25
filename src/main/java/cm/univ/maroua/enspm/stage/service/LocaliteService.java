package cm.univ.maroua.enspm.stage.service;

import cm.univ.maroua.enspm.stage.domain.Localite;
import cm.univ.maroua.enspm.stage.repository.LocaliteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class LocaliteService {

    private final LocaliteRepository localiteRepository;

    public LocaliteService(LocaliteRepository localiteRepository) {
        this.localiteRepository = localiteRepository;
    }

    public Page<Localite> findAll(Pageable pageable) {
        return localiteRepository.findAll(pageable);
    }

    public Optional<Localite> findOne(Long id) {
        return localiteRepository.findById(id);
    }

    public Localite save(Localite localite) {
        return localiteRepository.save(localite);
    }

    public void delete(Long id) {
        localiteRepository.deleteById(id);
    }
}
