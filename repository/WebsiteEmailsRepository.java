package cu.redcuba.repository;

import cu.redcuba.entity.WebsiteEmails;
import cu.redcuba.entity.WebsiteEmailsPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

public interface WebsiteEmailsRepository extends JpaRepository<WebsiteEmails, WebsiteEmailsPK> {

    @Query(
            value = "SELECT MAX(we.day) FROM WebsiteEmails we"
    )
    Date findMaxDay();

    WebsiteEmails findByWebsiteIdAndDay(Long websiteId, Date day);

}