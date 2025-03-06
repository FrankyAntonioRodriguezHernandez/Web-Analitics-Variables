/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cu.redcuba.repository;

import cu.redcuba.entity.RoundControl;
import cu.redcuba.entity.RoundControlPK;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author developer
 */
public interface RoundControlRepository extends JpaRepository<RoundControl, RoundControlPK> {

    /**
     *
     * @param round
     * @param type
     * @param items
     * @return
     */
    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE RoundControl rc SET"
            + " rc.itemsProcessed = rc.itemsProcessed + :items"
            + " , rc.modified = CURRENT_TIMESTAMP"
            + " WHERE rc.roundControlPK.round = :round AND rc.roundControlPK.type = :type")
    int incItemsProcessed(
            @Param("round") String round, 
            @Param("type") String type, 
            @Param("items") int items
    );
    
}
