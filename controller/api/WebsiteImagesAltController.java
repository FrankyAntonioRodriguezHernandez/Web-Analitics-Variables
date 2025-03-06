package cu.redcuba.controller.api;

import com.fasterxml.jackson.annotation.JsonView;
import cu.redcuba.controller.api.views.ApiView;
import cu.redcuba.entity.WebsiteImagesAlt;
import cu.redcuba.repository.WebsiteImagesAltRepository;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@Profile("api")
@Api(tags = "Datos de Variables")
@RestController
@RequestMapping("/api/images-alt")
public class WebsiteImagesAltController {

    private final WebsiteImagesAltRepository websiteImagesAltRepository;

    @Autowired
    public WebsiteImagesAltController(WebsiteImagesAltRepository websiteImagesAltRepository) {
        this.websiteImagesAltRepository = websiteImagesAltRepository;
    }

    @ApiOperation(value = "View last website Images Alt data", response = WebsiteImagesAlt.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list")
            , @ApiResponse(code = 401, message = "You are not authorized to view the resource")
            , @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")
            , @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @GetMapping(value = "/last", produces = "application/json")
    @JsonView(ApiView.class)
    public WebsiteImagesAlt getLast(
            @ApiParam(value = "The website identifier.")
            @RequestParam(value = "website_id") Long websiteId
    ) {
        Date lastDay = websiteImagesAltRepository.findMaxDay();
        return websiteImagesAltRepository.findByWebsiteIdAndDay(websiteId, lastDay);
    }

}
