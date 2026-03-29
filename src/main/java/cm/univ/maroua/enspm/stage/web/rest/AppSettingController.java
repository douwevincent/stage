package cm.univ.maroua.enspm.stage.web.rest;

import cm.univ.maroua.enspm.stage.service.AppSettingService;
import cm.univ.maroua.enspm.stage.service.dto.AppSettingDTO;
import cm.univ.maroua.enspm.stage.service.dto.AppSettingUpdateDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/parametres")
public class AppSettingController {

    private final AppSettingService appSettingService;

    public AppSettingController(AppSettingService appSettingService) {
        this.appSettingService = appSettingService;
    }

    @GetMapping
    public List<AppSettingDTO> getAll() {
        return appSettingService.findAll();
    }

    @GetMapping("/{cle}")
    public ResponseEntity<AppSettingDTO> getOne(@PathVariable String cle) {
        return ResponseEntity.ok(appSettingService.findOne(cle));
    }

    @PutMapping("/{cle}")
    public ResponseEntity<AppSettingDTO> update(
            @PathVariable String cle,
            @Valid @RequestBody AppSettingUpdateDTO updateDTO) {
        return ResponseEntity.ok(appSettingService.update(cle, updateDTO));
    }
}