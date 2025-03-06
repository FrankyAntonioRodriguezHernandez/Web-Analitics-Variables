package cu.redcuba.controller.api;

import com.fasterxml.jackson.annotation.JsonView;
import cu.redcuba.controller.api.views.ApiView;
import cu.redcuba.entity.WebsiteCSS;
import cu.redcuba.repository.WebsiteCSSRepository;
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
@RequestMapping("/api/css-minified")
public class WebsiteCSSController {

    private final WebsiteCSSRepository websiteCssRepository;

    @Autowired
    public WebsiteCSSController(WebsiteCSSRepository websiteCssRepository) {
        this.websiteCssRepository = websiteCssRepository;
    }

    @ApiOperation(value = "View last website CSS data")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list", response = WebsiteCSS.class)
            , @ApiResponse(code = 401, message = "You are not authorized to view the resource")
            , @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")
            , @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @GetMapping(value = "/last", produces = "application/json")
    @JsonView(ApiView.class)
    public WebsiteCSS getLast(
            @ApiParam(value = "The website identifier.")
            @RequestParam(value = "website_id") Long websiteId
    ) {
        Date lastDay = websiteCssRepository.findMaxDay();
        return websiteCssRepository.findByWebsiteIdAndDay(websiteId, lastDay);
    }

}
