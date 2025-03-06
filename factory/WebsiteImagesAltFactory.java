package cu.redcuba.factory;

import cu.redcuba.entity.WebsiteImagesAlt;
import cu.redcuba.model.ImageItem;
import cu.redcuba.repository.WebsiteImagesAltRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class WebsiteImagesAltFactory {

    private final WebsiteImagesAltRepository websiteImagesAltRepository;

    @Autowired
    public WebsiteImagesAltFactory(WebsiteImagesAltRepository websiteImagesAltRepository) {
        this.websiteImagesAltRepository = websiteImagesAltRepository;
    }

    public void createAndSave(long websiteId, List<ImageItem> images) {
        Date date = new Date();
        WebsiteImagesAlt websiteImagesAlt = new WebsiteImagesAlt(websiteId, date, images);

        websiteImagesAltRepository.save(websiteImagesAlt);
    }

}