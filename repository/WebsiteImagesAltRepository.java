package cu.redcuba.repository;

import cu.redcuba.entity.WebsiteImagesAlt;
import cu.redcuba.entity.WebsiteImagesAltPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

public interface WebsiteImagesAltRepository extends JpaRepository<WebsiteImagesAlt, WebsiteImagesAltPK> {

    @Query(
            value = "SELECT MAX(wia.day) FROM WebsiteImagesAlt wia"
    )
    Date findMaxDay();

    WebsiteImagesAlt findByWebsiteIdAndDay(Long websiteId, Date day);

}