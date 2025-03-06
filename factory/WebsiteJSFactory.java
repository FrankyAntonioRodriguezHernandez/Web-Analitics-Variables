package cu.redcuba.factory;

import cu.redcuba.entity.WebsiteJS;
import cu.redcuba.model.JsItem;
import cu.redcuba.repository.WebsiteJSRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class WebsiteJSFactory {

    private final WebsiteJSRepository websiteJsRepository;

    @Autowired
    public WebsiteJSFactory(WebsiteJSRepository websiteJsRepository) {
        this.websiteJsRepository = websiteJsRepository;
    }

    public void createAndSave(long websiteId, List<JsItem> js) {
        try {
            Date date = new Date();
            WebsiteJS websiteJs = new WebsiteJS(websiteId, date, js);
            websiteJsRepository.save(websiteJs);
        } catch (Exception ignored) {

        }
    }

}
