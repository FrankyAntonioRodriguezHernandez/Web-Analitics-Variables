/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cu.redcuba.repository;

import cu.redcuba.entity.Visit;
import java.util.Date;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author developer
 */
public interface VisitRepository extends JpaRepository<Visit, Long> {

    /**
     *
     * @param visited
     * @param urlHash
     * @return
     */
    @Query(
            value = "SELECT v.* FROM d_visit v WHERE DATE(v.visited) = DATE(:visited) AND v.url_hash = :urlHash ORDER BY v.visited DESC LIMIT 1",
            nativeQuery = true
    )
    Visit findByVisitedDateAndUrlHash(
            @Param("visited") Date visited,
            @Param("urlHash") String urlHash
    );
    
}
