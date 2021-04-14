package br.com.preventsr.logs.utils.functions;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TemplatesFunctions {

    @Value("${spring.profiles.active}")
    public String profile;

    @Value("${base.url}")
    public String baseUrl;

    @Value(value = "${swagger.project.name-api}")
    private String nameApi;

    @Value(value = "${swagger.project.description-api}")
    private String descriptionApi;

    @Value(value = "${swagger.project.version-api}")
    private String versionApi;

    @Value(value = "${project.url.logo}")
    private String urlLogo;

    public String initalPageHtml() {
        try {
            return "<html lang='pt-BR'>" +
                    "<body>" +
                    "<div style='padding=50px;text-align=justify;font-family: Tahoma, sans-serif;'>" +
                    "<h1>" +
                    "Bem vindo ao " + nameApi + " - " + versionApi + "." +
                    "</h1>" +
                    "<br/>" +
                    "<p>" + descriptionApi + "</p>" +
                    "<br/>" +
                    "<strong>Para ter acesso a nossa documentação de endpoints do swagger, <a href='" + baseUrl + "/swagger-ui/index.html'>clique aqui</a>.</strong>" +
                    checkAccessH2() +
                    "<br/>" +
                    "<br/>" +
                    "<hr/>" +
                    "<br/>" +
                    "<br/>" +
                    "<center>" +
                    "<img src='" + urlLogo + "' alt='logo'/>" +
                    "<br/>" +
                    "<br/>" +
                    "<hr/>" +
                    "<br/>" +
                    "<br/>" +
                    "</center>" +
                    "</div>" +
                    "</body>" +
                    /*"<script src=\"https://gist.github.com/rafaelspaesleme-ads/c88cc2b87291572ce249ab94b183b733.js\"></script>" +*/
                    "</html>";

        } catch (Exception e) {
            return null;
        }
    }

    private String checkAccessH2() {
        return profile.equals("test") ? "<br/>-<br/><strong>Para ter acesso ao banco de dados de testes (embarcado), <a href='" + baseUrl + "/h2-console'>clique aqui</a>.</strong>" : "";
    }
}
