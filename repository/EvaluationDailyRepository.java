package cu.redcuba.repository;

import cu.redcuba.entity.EvaluationDaily;
import cu.redcuba.entity.EvaluationDailyPK;
import cu.redcuba.object.EvaluationValueInterval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface EvaluationDailyRepository extends JpaRepository<EvaluationDaily, EvaluationDailyPK> {

    @Query(
            value = "SELECT MAX(ed.pk.day) FROM EvaluationDaily ed"
    )
    Date findMaxDay();


    @Query(value = "SELECT ed.pk.websiteId FROM EvaluationDaily ed where  ed.pk.day = :day and ed.pk.indicatorId = :indicator and ed.evaluation = :status")
    List<Object[]> getAllWebsiteByStatusDateAndIndicator(@Param("day") Date day, @Param("indicator") long indicator, @Param("status") float status);

    @Query(
            value = "SELECT MAX(ed.pk.day) FROM EvaluationDaily ed WHERE ed.pk.websiteId = :websiteId AND ed.evaluation = 200 AND ed.pk.indicatorId = 12 AND ed.pk.day > :day "
    )
    Date findMaxDayByWebSiteIdANDDay(@Param("websiteId") Long websiteId, @Param("day") Date day);

    /**
     * Returns all the evaluations for a given date.
     *
     * @return The evaluations.
     */
    @Query(
            value = "SELECT website_id, variable_id, evaluation, created FROM " +
                    "   ( " +
                    "    SELECT DISTINCT T.website_id, v.id AS variable_id FROM  " +
                    "       ( " +
                    "        SELECT * FROM d_evaluation_daily ev WHERE ev.day = :date " +
                    "        ) T " +
                    "   INNER JOIN d_variable v " +
                    "    ) T1 " +
                    "LEFT JOIN " +
                    "   ( " +
                    "    SELECT ev.website_id, ev.variable_id, ev.evaluation, ev.created FROM d_evaluation_daily ev " +
                    "   INNER JOIN d_variable_indicator vi ON (ev.indicator_id = vi.id) " +
                    "   WHERE ev.day = :date AND vi.slug LIKE '%-value' " +
                    "    ) T2 " +
                    "USING (website_id, variable_id) " +
                    "ORDER BY website_id DESC, variable_id ASC",
            nativeQuery = true
    )
    List<Object[]> findAllEvaluations(@Param("date") String date);

    /**
     * Calculates and gets the totals associated to the National WS evaluations.
     *
     * @return The totals related to the evaluations.
     */
    @Query(
            value = "SELECT day, name, websites_ok, websites_to_improve, websites_bad  " +
                    "FROM (  " +
                    "   SELECT day, variable_id,  " +
                    "       COUNT(CASE WHEN  " +
                    "           (variable_id = 12 AND evaluation = 200) OR " +
                    "           (variable_id NOT IN (12, 21) AND evaluation = 1) " +
                    "           THEN 1 END) AS websites_ok,  " +
                    "       COUNT(CASE WHEN  " +
                    "           variable_id NOT IN (12, 21) AND evaluation = 0.5 " +
                    "           THEN 1 END) AS websites_to_improve,  " +
                    "       COUNT(CASE WHEN  " +
                    "           (variable_id = 12 AND evaluation != 200) OR  " +
                    "           (variable_id NOT IN (12, 21) AND evaluation = 0) " +
                    "           THEN 1 END) AS websites_bad  " +
                    "   FROM d_evaluation_daily  " +
                    "   WHERE international = FALSE AND indicator_id IN  " +
                    "       (SELECT id FROM d_variable_indicator WHERE slug LIKE '%-value') " +
                    "   GROUP BY day, variable_id " +
                    ") de INNER JOIN d_variable v ON v.id = de.variable_id  " +
                    "ORDER BY day DESC, variable_id ASC",
            nativeQuery = true
    )
    List<Object[]> findNationalTotals();

    /**
     * Calculates and gets the totals associated to the National WS evaluations for a given day.
     *
     * @param date The given day.
     * @return The totals related to the evaluations.
     */
    @Query(
            value = "SELECT name, websites_ok, websites_to_improve, websites_bad  " +
                    "FROM (  " +
                    "   SELECT variable_id,  " +
                    "       COUNT(CASE WHEN  " +
                    "           (variable_id = 12 AND evaluation = 200) OR " +
                    "           (variable_id NOT IN (12, 21) AND evaluation = 1) " +
                    "           THEN 1 END) AS websites_ok,  " +
                    "       COUNT(CASE WHEN  " +
                    "           variable_id NOT IN (12, 21) AND evaluation = 0.5 " +
                    "           THEN 1 END) AS websites_to_improve,  " +
                    "       COUNT(CASE WHEN  " +
                    "           (variable_id = 12 AND evaluation != 200) OR  " +
                    "           (variable_id NOT IN (12, 21) AND evaluation = 0) " +
                    "           THEN 1 END) AS websites_bad  " +
                    "   FROM d_evaluation_daily  " +
                    "   WHERE day = :date AND international = FALSE AND indicator_id IN  " +
                    "       (SELECT id FROM d_variable_indicator WHERE slug LIKE '%-value') " +
                    "   GROUP BY variable_id " +
                    ") de INNER JOIN d_variable v ON v.id = de.variable_id " +
                    "ORDER BY variable_id ASC",
            nativeQuery = true
    )
    List<Object[]> findNationalTotalsForDay(@Param("date") String date);

    List<EvaluationDaily> findByPkWebsiteIdAndPkDay(Long websiteId, Date day);

    @Query(
            value = "SELECT evaluation, day FROM d_evaluation_daily " +
                    "WHERE created > DATE_SUB(now(), INTERVAL 7 DAY) AND website_id = :websiteId " +
                    "AND variable_id = :variableId AND indicator_id = :indicatorId ORDER BY created DESC LIMIT 2",
            nativeQuery = true
    )
    List<Object[]> findLastEvaluationsBySiteAndVariableAndIndicator(
            @Param("websiteId") Long websiteId,
            @Param("variableId") Integer variableId,
            @Param("indicatorId") Integer indicatorId
    );

    List<EvaluationDaily> findByPk_WebsiteIdAndPk_VariableIdAndPk_IndicatorIdAndPk_DayBetweenOrderByPk_DayDesc(
            @Param("websiteId") Long websiteId,
            @Param("variableId") Long variableId,
            @Param("indicatorId") Long indicatorId,
            @Param("dayMin") Date dayMin,
            @Param("dayMax") Date dayMax
    );

    @Query(nativeQuery = true)
    EvaluationValueInterval lastEvaluationValueInterval(
            @Param("websiteId") Long websiteId,
            @Param("variableId") Long variableId,
            @Param("indicatorId") Long indicatorId
    );

}