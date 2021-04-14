package br.com.preventsr.logs.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfig {
    @Value(value = "${swagger.project.name-api}")
    private String nameApi;

    @Value(value = "${swagger.project.description-api}")
    private String descriptionApi;

    @Value(value = "${swagger.project.version-api}")
    private String versionApi;

    @Value(value = "${swagger.project.license-api}")
    private String licenseApi;

    @Value(value = "${swagger.project.license-url-api}")
    private String licenseUrlApi;

    @Value(value = "${swagger.project.name-contacts}")
    private String nameContact;

    @Value(value = "${swagger.project.mail-contacts}")
    private String mailContact;

    @Value(value = "${swagger.project.mail-support-contacts}")
    private String mailSupportContact;

    @Value(value = "${swagger.project.site-contacts}")
    private String siteContact;

    @Value(value = "${swagger.project.description-base-url}")
    private String descriptionBaseUrl;

    @Value(value = "${swagger.project.base-url}")
    private String baseUrl;

    @Bean
    public Docket api() {

        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("br.com.preventsr.logs.resources.v1.controller"))
                .paths(PathSelectors.any())
                .build()
                .useDefaultResponseMessages(false)
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(nameApi)
                .description(descriptionApi)
                .version(versionApi)
                .license(licenseApi)
                .contact(new Contact(descriptionBaseUrl, baseUrl, mailSupportContact))
                .licenseUrl(licenseUrlApi)
                .contact(new Contact(nameContact, siteContact, mailContact))
                .build();
    }
}
