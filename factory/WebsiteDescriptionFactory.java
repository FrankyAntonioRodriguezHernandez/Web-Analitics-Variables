package cu.redcuba.factory;
import cu.redcuba.entity.WebsiteDescription;
import cu.redcuba.entity.WebsiteDescriptionPK;
import cu.redcuba.repository.WebsiteDescriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
public class WebsiteDescriptionFactory {

    private final WebsiteDescriptionRepository websiteDescriptionRepository;

    @Autowired
    public WebsiteDescriptionFactory(WebsiteDescriptionRepository websiteDescriptionRepository) {
        this.websiteDescriptionRepository = websiteDescriptionRepository;
    }

    public void createAndSave(long websiteId, String description, int length) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String day = dateFormat.format(date);
        try {
            WebsiteDescription websiteDescription = new WebsiteDescription(websiteId, day, description, length);
            websiteDescriptionRepository.save(websiteDescription);
        } catch (Exception ignored) {
        }
    }
}
