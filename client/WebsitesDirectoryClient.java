/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cu.redcuba.client;

import cu.redcuba.helper.SSLHelper;
import cu.redcuba.object.Website;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author developer
 */
public class WebsitesDirectoryClient {

    private static final Logger LOG = Logger.getLogger(WebsitesDirectoryClient.class.getName());

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${client.websitesDirectory.url}")
    private String url;

    @Value("${client.websitesDirectory.apiKey}")
    private String apiKey;

    @Value("${client.websitesDirectory.turnOffCertificateValidation}")
    private Boolean turnOffCertificateValidation;

    private void determineCertificateValidation() {
        if (turnOffCertificateValidation) {
            try {
                SSLHelper.turnOffCertificateValidation();
            } catch (NoSuchAlgorithmException | KeyManagementException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Obtener los datos de un sitio web por su hostname.
     *
     * @return Sitio web.
     */
    public Website getWebsiteByHostname(String hostname) {
        determineCertificateValidation();
        String endpoint = url + "/api/v1/website/show?api_key={api_key}&hostname={hostname}";
        Map<String, String> params = new HashMap<>();
        params.put("api_key", apiKey);
        params.put("hostname", hostname);
        ResponseEntity<Website> response = restTemplate.getForEntity(endpoint, Website.class, params);
        return response.getBody();
    }

    /**
     * Obtener todos los sitios web habilitados y categorizados.
     *
     * @return Listado de sitios web.
     */
    public List<Website> getWebsitesEnabledCategorized() {
        determineCertificateValidation();
        String endpoint = url + "/api/v1/website/enabled_categorized?api_key={api_key}";
        ResponseEntity<Website[]> response = restTemplate.getForEntity(endpoint, Website[].class, apiKey);
        return Arrays.asList(response.getBody());
    }

    /**
     * Obtener la cantidad de sitios web habilitados y categorizados en una fecha específica.
     *
     * @param date La fecha máxima.
     * @return Listado de sitios web.
     */
    public int getWebsitesEnabledCategorizedCount(String date) {
        determineCertificateValidation();
        String endpoint = url + "/api/v1/website/enabled_categorized_count?api_key={api_key}&date={date}";
        Map<String, String> params = new HashMap<>();
        params.put("api_key", apiKey);
        params.put("date", date);
        ResponseEntity<Integer> response = restTemplate.getForEntity(endpoint, Integer.class, params);
        return response.getBody();
    }
}
