package cu.redcuba.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Los endpoints quedan asegurados con autenticación básica.
        // Para mantener compatibilidad con el resto de los procesos desarrollados por el momento
        // en API, comandos y su documentación se puede acceder libremente.
        http
                .csrf().disable()
                .httpBasic()
                .and()
                // Permitir cualquier usuario en API, comandos y la documentación.
                .authorizeRequests()
                .antMatchers("/v2/api-docs", "/swagger-ui/**", "/swagger-resources/**").permitAll()
                .antMatchers("/api/**").permitAll()
                .antMatchers("/command/**").permitAll()
                // .antMatchers("/actuator**").permitAll()
                .and()
                // El resto de las peticiones será autenticado.
                .authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
        ;
    }

}
