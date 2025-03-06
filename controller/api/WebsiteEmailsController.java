package cu.redcuba.controller.api;

import com.fasterxml.jackson.annotation.JsonView;
import cu.redcuba.controller.api.views.ApiView;
import cu.redcuba.entity.WebsiteEmails;
import cu.redcuba.repository.WebsiteEmailsRepository;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@Profile("api")
@Api(tags = "Datos de Variables")
@RestController
@RequestMapping("/api/emails")
public class WebsiteEmailsController {

    private final WebsiteEmailsRepository websiteEmailsRepository;

    @Autowired
    public WebsiteEmailsController(WebsiteEmailsRepository websiteEmailsRepository) {
        this.websiteEmailsRepository = websiteEmailsRepository;
    }

    @ApiOperation(value = "View last website emails", response = WebsiteEmails.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list")
            , @ApiResponse(code = 401, message = "You are not authorized to view the resource")
            , @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")
            , @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @RequestMapping(value = "/last", method = RequestMethod.GET, produces = "application/json")
    @JsonView(ApiView.class)
    public WebsiteEmails getLast(
            @ApiParam(value = "The website identifier.")
            @RequestParam(value = "website_id") Long websiteId
    ) {
        Date lastDay = websiteEmailsRepository.findMaxDay();
        return websiteEmailsRepository.findByWebsiteIdAndDay(websiteId, lastDay);
    }

}
