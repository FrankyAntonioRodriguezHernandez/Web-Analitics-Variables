package cu.redcuba.repository;

import cu.redcuba.entity.WebsiteCSS;
import cu.redcuba.entity.WebsiteCssPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

public interface WebsiteCSSRepository extends JpaRepository<WebsiteCSS, WebsiteCssPK> {

    @Query(
            value = "SELECT MAX(wc.day) FROM WebsiteCSS wc"
    )
    Date findMaxDay();

    WebsiteCSS findByWebsiteIdAndDay(Long websiteId, Date day);

}
