package cu.redcuba.repository;
import cu.redcuba.entity.WebsiteDescriptionPK;
import cu.redcuba.entity.WebsiteDescription;
import cu.redcuba.entity.WebsiteLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface WebsiteDescriptionRepository {

    @Query(
            value = "SELECT MAX(wd.pk) FROM WebsiteDescription wd"
    )
    String findMaxDay();

    List<WebsiteDescription> findByPkWebsiteIdAndPkDay(Long websiteId, String day);

}