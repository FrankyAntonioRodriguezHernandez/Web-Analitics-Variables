package cu.redcuba.client.notifier;

import cu.redcuba.client.notifier.model.RedCubaScriptValue;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Component
@FeignClient(name = "conNotifierClient", url = "${evw.var.notifier.url}")
public interface ConNotifierClient {

    @PostMapping(value = "analyze-indicator-value/redcuba-script")
    void analyzeIndicatorValueRedCubaScript(@RequestBody RedCubaScriptValue redCubaScriptValue);

}
