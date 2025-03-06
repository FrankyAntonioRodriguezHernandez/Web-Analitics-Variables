package cu.redcuba.factory;

import cu.redcuba.entity.WebsiteKeywords;
import cu.redcuba.entity.WebsiteLink;
import cu.redcuba.repository.WebsiteLinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
public class WebsiteLinkFactory {
    private final WebsiteLinkRepository websiteLinkRepository;

    @Autowired
    public WebsiteLinkFactory(WebsiteLinkRepository websiteLinkRepository) {
        this.websiteLinkRepository = websiteLinkRepository;
    }


    public void createAndSave(long websiteId, List<String> link) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String day = dateFormat.format(date);
        try {
            WebsiteLink websiteLink = new WebsiteLink(websiteId, day, link);
            websiteLinkRepository.save(websiteLink);
        } catch (Exception ignored) {

        }
    }

}

