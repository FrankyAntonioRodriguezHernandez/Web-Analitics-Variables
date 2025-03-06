package cu.redcuba.factory;

import cu.redcuba.entity.WebsiteCSS;
import cu.redcuba.model.CssItem;
import cu.redcuba.repository.WebsiteCSSRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class WebsiteCSSFactory {

    private final WebsiteCSSRepository websiteCSSRepository;

    @Autowired
    public WebsiteCSSFactory(WebsiteCSSRepository websiteCSSRepository) {
        this.websiteCSSRepository = websiteCSSRepository;
    }

    public void createAndSave(long websiteId, List<CssItem> css) {
        try {
            Date date = new Date();
            WebsiteCSS websiteCss = new WebsiteCSS(websiteId, date, css);
            websiteCSSRepository.save(websiteCss);
        } catch (Exception ignored) {

        }
    }

}
