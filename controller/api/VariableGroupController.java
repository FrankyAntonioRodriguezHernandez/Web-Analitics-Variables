package cu.redcuba.controller.api;

import com.fasterxml.jackson.annotation.JsonView;
import cu.redcuba.controller.api.views.ApiView;
import cu.redcuba.entity.VariableGroup;
import cu.redcuba.repository.VariableGroupRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Profile("api")
@Api(tags = "Variables")
@RestController
@RequestMapping("/api/groups")
public class VariableGroupController {

    private final VariableGroupRepository variableGroupRepository;

    @Autowired
    public VariableGroupController(VariableGroupRepository variableGroupRepository) {
        this.variableGroupRepository = variableGroupRepository;
    }

    @ApiOperation(value = "Obtener todos los grupos de variables y las variables dentro de ellos", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list")
            , @ApiResponse(code = 401, message = "You are not authorized to view the resource")
            , @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")
            , @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @GetMapping(value = "/all", produces = "application/json")
    @JsonView(ApiView.class)
    public List<VariableGroup> getAll() {
        return variableGroupRepository.findAll();
    }

}
