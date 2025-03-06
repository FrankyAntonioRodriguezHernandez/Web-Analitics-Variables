package cu.redcuba.repository;

import cu.redcuba.entity.WebsiteLanguage;
import cu.redcuba.entity.WebsiteLanguagePK;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface WebsiteLanguageRepository extends JpaRepository<WebsiteLanguage, WebsiteLanguagePK> {
}
