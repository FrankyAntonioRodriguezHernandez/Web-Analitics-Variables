/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cu.redcuba.repository;

import cu.redcuba.entity.Download;
import java.util.Date;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author developer
 */
public interface DownloadRepository extends JpaRepository<Download, Long> {
    
    /**
     *
     * @param downloaded
     * @param urlHash
     * @return 
     */
    @Query(
            value = "SELECT d.* FROM d_download d WHERE d.downloaded >= DATE(:downloaded) AND d.downloaded < DATE(DATE_ADD(:downloaded,INTERVAL 1 DAY)) AND d.url_hash = :urlHash ORDER BY d.downloaded DESC LIMIT 1",
            nativeQuery = true
    )
    Download findByDownloadedDateAndUrlHash(
            @Param("downloaded") Date downloaded,
            @Param("urlHash") String urlHash
    );
    
}
