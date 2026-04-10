package cm.univ.maroua.enspm.stage.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Contr&ocirc;leur de fallback pour les SPA (Single Page Application) Vue.js en mode history.
 *
 * <p>Lorsqu'un navigateur demande directement une route g&eacute;r&eacute;e par Vue Router
 * (ex: /evaluation-encadreur/a1QaO2swEe3I), cette classe redirige l'int&eacute;rieur
 * de la requ&ecirc;te vers /index.html pour que la SPA prenne le relais.
 *
 * <p>Sont exclus de ce fallback :
 * <ul>
 *   <li>Les routes API REST (/api/)</li>
 *   <li>L'interface Swagger (/swagger-ui, /v3/api-docs)</li>
 *   <li>La console H2 (/h2-console)</li>
 *   <li>Les ressources statiques (toute URL contenant une extension de fichier)</li>
 * </ul>
 *
 * <p>Les routes REST enregistr&eacute;es dans les autres contr&ocirc;leurs prennent toujours
 * priorit&eacute; sur ce fallback gr&acirc;ce au m&eacute;canisme de sp&eacute;cificit&eacute; de Spring MVC.
 * La garde sur les pr&eacute;fixes exclus couvre uniquement les routes non-enregistr&eacute;es
 * (ex: /api/v1/route-inexistante) afin de retourner un vrai 404 plut&ocirc;t qu'index.html.
 */
@Controller
public class SpaForwardingController {

    /**
     * Pr&eacute;fixes de chemin qui ne doivent jamais &ecirc;tre forward&eacute;s vers la SPA.
     * Ces valeurs sont v&eacute;rifi&eacute;es apr&egrave;s retrait du context path Tomcat.
     */
    private static final List<String> EXCLUDED_PREFIXES = List.of(
            "/api/",
            "/swagger-ui",
            "/v3/api-docs",
            "/h2-console"
    );

    /**
     * Capture toute route GET qui :
     * <ul>
     *   <li>est la racine (/)</li>
     *   <li>est un segment unique sans extension (/{segment})</li>
     *   <li>est un chemin multi-niveaux dont le premier segment n'a pas d'extension
     *       (/{segment}/...)</li>
     * </ul>
     * et la forward en interne vers /index.html pour que Vue Router la traite.
     *
     * @param request la requ&ecirc;te HTTP entrante
     * @return directive de forward vers index.html
     * @throws ResponseStatusException 404 si la route ressemble &agrave; un endpoint backend manquant
     */
    @GetMapping(value = {
            "/",
            "/{segment:[^\\.]*}",
            "/{segment:[^\\.]*}/**"
    })
    public String forwardToIndex(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        String relativeUri = (contextPath != null && uri.startsWith(contextPath))
                ? uri.substring(contextPath.length())
                : uri;

        for (String prefix : EXCLUDED_PREFIXES) {
            if (relativeUri.startsWith(prefix)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
        }

        return "forward:/index.html";
    }
}
