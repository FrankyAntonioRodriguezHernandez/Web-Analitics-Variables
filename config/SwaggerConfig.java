package cu.redcuba.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;

@Configuration
public class SwaggerConfig {

    @Bean
    public UiConfiguration uiConfig() {
        return UiConfigurationBuilder.builder()
                .supportedSubmitMethods(UiConfiguration.Constants.NO_SUBMIT_METHODS)
                .build();
    }

    private ApiInfo restApiV1Info() {
        return new ApiInfoBuilder()
                .version("1.0")
                .title("Evaluador de Variables - API")
                .description("Documentación del API de rc-evw-variables v1.0")
                .build();
    }

    @Profile("api")
    @Bean
    public Docket restApiV1() {
        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .groupName("rc-evw-variables-api-1.0")
                .select()
                .apis(RequestHandlerSelectors.basePackage("cu.redcuba.controller.api"))
                .build()
                .apiInfo(restApiV1Info())
                .tags(
                        new Tag("Evaluación", "Evaluación de sitios web"),
                        new Tag("Variables", "Variables, grupos e indicarores de evaluación"),
                        new Tag("Datos de Variables", "Datos específicos por variables")
                );
    }

    private ApiInfo restCommandV1Info() {
        return new ApiInfoBuilder()
                .version("1.0")
                .title("Evaluador de Variables - Comandos")
                .description("Documentación de los comandos de mantenimiento de rc-evw-variables v1.0")
                .build();
    }

    @Profile("producer")
    @Bean
    public Docket restCommandV1() {
        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .groupName("rc-evw-variables-command-1.0")
                .select()
                .apis(RequestHandlerSelectors.basePackage("cu.redcuba.controller.command"))
                .build()
                .apiInfo(restCommandV1Info())
                .tags(
                        new Tag("Comandos de Mantenimiento", "Comandos que posibilitan realizar tareas de mantenimiento"),
                        new Tag("Rellenar Medición", "Comandos que posibilitan rellenar o reconstruir mediciones")
                );
    }

}
