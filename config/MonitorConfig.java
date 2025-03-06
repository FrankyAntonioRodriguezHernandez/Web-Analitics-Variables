
package cu.redcuba.config;

import cu.redcuba.influxdb.InfluxDbClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class MonitorConfig {

    @Bean(initMethod = "init")
    public InfluxDbClient influxDbClient() {
        return new InfluxDbClient();
    }

}
