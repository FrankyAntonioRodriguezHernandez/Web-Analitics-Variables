package cu.redcuba;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Locale;
import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
@EnableFeignClients
@ImportResource({"classpath:configuration-consumer.xml"})
public class Application {

    public static void main(String[] args) {
        Locale.setDefault(new Locale("es", "ES"));
        TimeZone.setDefault(TimeZone.getTimeZone("America/Havana"));
        SpringApplication.run(Application.class, args);
    }

}
