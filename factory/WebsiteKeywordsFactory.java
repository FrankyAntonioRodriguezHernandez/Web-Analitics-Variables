package cu.redcuba.factory;

import cu.redcuba.entity.WebsiteKeywords;
import cu.redcuba.entity.WebsiteKeywordsPK;
import cu.redcuba.repository.WebsiteKeywordsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
public class WebsiteKeywordsFactory {

    private final WebsiteKeywordsRepository websiteKeywordsRepository;

    @Autowired
    public WebsiteKeywordsFactory(WebsiteKeywordsRepository websiteKeywordsRepository) {
        this.websiteKeywordsRepository = websiteKeywordsRepository;
    }

    public void createAndSave(long websiteId, List<String> keywords) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String day = dateFormat.format(date);
        try {
            WebsiteKeywords websiteKeywords = new WebsiteKeywords(websiteId, day, keywords);
            websiteKeywordsRepository.save(websiteKeywords);
        } catch (Exception ignored) {

        }
    }

}
