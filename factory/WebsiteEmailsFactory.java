package cu.redcuba.factory;

import cu.redcuba.entity.WebsiteEmails;
import cu.redcuba.repository.WebsiteEmailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class WebsiteEmailsFactory {

    private final WebsiteEmailsRepository websiteEmailsRepository;

    @Autowired
    public WebsiteEmailsFactory(WebsiteEmailsRepository websiteEmailsRepository) {
        this.websiteEmailsRepository = websiteEmailsRepository;
    }

    public void createAndSave(long websiteId, List<String> emails) {
        Date date = new Date();
        WebsiteEmails websiteEmails = new WebsiteEmails(websiteId, date, emails);

        websiteEmailsRepository.save(websiteEmails);
    }

}