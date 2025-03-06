package cu.redcuba.config;

import cu.redcuba.helper.MinifiedHelper;
import cu.redcuba.helper.UrlFetchHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("consumer")
@Configuration
public class WorkerConfig {

    @Bean
    public UrlFetchHelper normalUrlFetchHelper() {
        return new UrlFetchHelper();
    }

    @Bean
    public UrlFetchHelper compressUrlFetchHelper() {
        UrlFetchHelper urlFetchHelper = new UrlFetchHelper();
        urlFetchHelper.setAcceptCompression(true);
        return urlFetchHelper;
    }

    @Bean
    public MinifiedHelper minifiedHelper() {
        return new MinifiedHelper();
    }

}
