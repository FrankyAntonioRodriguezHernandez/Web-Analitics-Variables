/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cu.redcuba.config;

import cu.redcuba.client.WebsitesDirectoryClient;
import cu.redcuba.factory.RoundControlFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

// TODO: Crear un bean para el objecto GSON que es empleado en helpers, schedulers y workers.
// TODO: Crear un bean para los formatos de fecha y fecha con hora.
/**
 *
 * @author developer
 */
@Configuration
@EnableScheduling
@EnableJpaRepositories(basePackages = "cu.redcuba.repository")
public class ApplicationConfig {
    
    @Bean
    public RoundControlFactory roundControlFactory() {
        return new RoundControlFactory();
    }
    
    @Bean
    public WebsitesDirectoryClient websitesDirectoryClient() {
        return new WebsitesDirectoryClient();
    }

}
