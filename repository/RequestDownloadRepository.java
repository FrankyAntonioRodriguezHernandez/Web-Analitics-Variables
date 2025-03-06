/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cu.redcuba.repository;

import cu.redcuba.entity.RequestDownload;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author developer
 */
public interface RequestDownloadRepository extends JpaRepository<RequestDownload, Long> {

}
