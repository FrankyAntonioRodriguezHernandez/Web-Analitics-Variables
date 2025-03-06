package cu.redcuba.client.availability;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Component
@FeignClient(name = "availabilityClient", url = "${evw.var.availability.url}")
public interface AvailabilityClient {

    @RequestMapping(
            method = RequestMethod.GET,
            value = "${evw.var.availability.response-time.path}/{id}/24",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    Response getResponseTime(@PathVariable("id") Long id);

    @RequestMapping(
            method = RequestMethod.GET,
            value = "${evw.var.availability.availability.path}/{id}/24",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    Response getAvailability(@PathVariable("id") Long id);

    class Response {
        private Float value;

        public Float getValue() {
            return value;
        }

        public void setValue(Float value) {
            this.value = value;
        }
    }
}
