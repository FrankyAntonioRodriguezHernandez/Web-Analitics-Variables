package cu.redcuba.factory;

import cu.redcuba.entity.WebsiteLanguage;
import cu.redcuba.entity.WebsiteLanguagePK;
import cu.redcuba.repository.WebsiteLanguageRepository;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WebsiteLanguageFactory {

    private final WebsiteLanguageRepository websiteLanguageRepository;

    @Autowired
    public WebsiteLanguageFactory(WebsiteLanguageRepository websiteLanguageRepository) {
        this.websiteLanguageRepository = websiteLanguageRepository;
    }

    public void createAndSave(long websiteId, String declaredLanguage, String detectedLanguage) {
        Date day = new Date();
        try {
            WebsiteLanguage websiteLanguage = new WebsiteLanguage(websiteId, day, declaredLanguage, detectedLanguage);
            websiteLanguageRepository.save(websiteLanguage);
        } catch (Exception ignored) {

        }
    }

}
