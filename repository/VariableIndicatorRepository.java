package cu.redcuba.repository;

import cu.redcuba.entity.VariableIndicator;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VariableIndicatorRepository extends JpaRepository<VariableIndicator, Long> {

    VariableIndicator findBySlug(String slug);

}
