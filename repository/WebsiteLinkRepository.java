package cu.redcuba.repository;

import cu.redcuba.entity.WebsiteLink;
import cu.redcuba.entity.WebsiteLinkPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface WebsiteLinkRepository extends JpaRepository<WebsiteLink, WebsiteLinkPK>{
    @Query(
            value = "SELECT MAX(wl.pk) FROM WebsiteLink wl"
    )
    String findMaxDay();

    List<WebsiteLink> findByPkWebsiteIdAndPkDay(Long websiteId, String day);
}

