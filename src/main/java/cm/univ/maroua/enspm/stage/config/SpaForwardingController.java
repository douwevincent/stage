package cm.univ.maroua.enspm.stage.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Fallback SPA pour Vue Router (history mode).
 *
 * Important:
 * - Le context path (/stage2) est gere par Spring/Tomcat.
 * - Les routes backend (api, swagger, etc.) et ressources statiques
 *   ne doivent pas etre capturees par ce fallback.
 */
@Controller
public class SpaForwardingController {

    @GetMapping({
            "/",
            "/{segment:^(?!api$|swagger-ui$|v3$|h2-console$|assets$|css$|js$|woff2$|webjars$)[^\\.]*}",
            "/{segment:^(?!api$|swagger-ui$|v3$|h2-console$|assets$|css$|js$|woff2$|webjars$)[^\\.]*}/**"
    })
    public String forwardToIndex() {
        return "forward:/index.html";
    }
}