package cu.redcuba.controller.api;

import com.fasterxml.jackson.annotation.JsonView;
import cu.redcuba.controller.api.views.ApiView;
import cu.redcuba.entity.WebsiteLink;
import cu.redcuba.repository.WebsiteLinkRepository;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Profile("api")
@Api(tags = "Datos de Enlaces")
@RestController
@RequestMapping("/api/enlaces")
public class WebsiteLinkController {

    private final WebsiteLinkRepository websiteLinkRepository;

    @Autowired
    public WebsiteLinkController(WebsiteLinkRepository websiteLinkRepository) {
        this.websiteLinkRepository = websiteLinkRepository;
    }

    @ApiOperation(value = "Obtener los enlaces de la última evaluación de un sitio web")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list", response = WebsiteLink.class, responseContainer = "List")
            , @ApiResponse(code = 401, message = "You are not authorized to view the resource")
            , @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")
            , @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @GetMapping(value = "/last", produces = "application/json")
    @JsonView(ApiView.class)
    public List<WebsiteLink> getLast(
            @ApiParam(value = "The website identifier.")
            @RequestParam(value = "website_id") Long websiteId
    ) {
        String lastDay = websiteLinkRepository.findMaxDay();
        return websiteLinkRepository.findByPkWebsiteIdAndPkDay(websiteId, lastDay);
    }

}

