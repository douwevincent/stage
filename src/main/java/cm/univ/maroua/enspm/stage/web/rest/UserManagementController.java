package cm.univ.maroua.enspm.stage.web.rest;

import cm.univ.maroua.enspm.stage.service.UserAccountService;
import cm.univ.maroua.enspm.stage.service.dto.UserAccountDTO;
import cm.univ.maroua.enspm.stage.service.dto.UserCreateRequestDTO;
import cm.univ.maroua.enspm.stage.service.dto.UserResetPasswordRequestDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserManagementController {

    private final UserAccountService userAccountService;

    public UserManagementController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @GetMapping
    public Page<UserAccountDTO> getAll(Pageable pageable) {
        return userAccountService.findAll(pageable);
    }

    @PostMapping
    public ResponseEntity<UserAccountDTO> create(@Valid @RequestBody UserCreateRequestDTO request) {
        return ResponseEntity.ok(userAccountService.create(request));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<UserAccountDTO> activate(@PathVariable Long id) {
        return ResponseEntity.ok(userAccountService.activate(id));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<UserAccountDTO> deactivate(@PathVariable Long id) {
        return ResponseEntity.ok(userAccountService.deactivate(id));
    }

    @PatchMapping("/{id}/reset-password")
    public ResponseEntity<UserAccountDTO> resetPassword(
            @PathVariable Long id,
            @Valid @RequestBody UserResetPasswordRequestDTO request
    ) {
        return ResponseEntity.ok(userAccountService.resetPassword(id, request.newPassword()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userAccountService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
