
package cu.redcuba.repository;

import cu.redcuba.entity.WebsiteKeywords;
import cu.redcuba.entity.WebsiteKeywordsPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;


public interface WebsiteKeywordsRepository extends JpaRepository<WebsiteKeywords, WebsiteKeywordsPK> {

    @Query(
            value = "SELECT MAX(wk.pk.day) FROM WebsiteKeywords wk"
    )
    String findMaxDay();

    List<WebsiteKeywords> findByPkWebsiteIdAndPkDay(Long websiteId, String day);

}
