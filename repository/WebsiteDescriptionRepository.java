package cu.redcuba.repository;
import cu.redcuba.entity.WebsiteDescriptionPK;
import cu.redcuba.entity.WebsiteDescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface WebsiteDescriptionRepository extends JpaRepository<WebsiteDescriptionPK, WebsiteDescription>{

    @Query(
            value = "SELECT MAX(wd.pk) FROM WebsiteDescription wd"
    )
    String findMaxDay();

    List<WebsiteDescription> findByPkWebsiteIdAndPkDay(Long websiteId, String day);

}