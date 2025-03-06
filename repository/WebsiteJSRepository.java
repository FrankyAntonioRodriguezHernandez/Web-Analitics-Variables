package cu.redcuba.repository;

import cu.redcuba.entity.WebsiteJS;
import cu.redcuba.entity.WebsiteJSPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

public interface WebsiteJSRepository extends JpaRepository<WebsiteJS, WebsiteJSPK> {

    @Query(
            value = "SELECT MAX(wj.day) FROM WebsiteJS wj"
    )
    Date findMaxDay();

    WebsiteJS findByWebsiteIdAndDay(Long websiteId, Date day);
}
