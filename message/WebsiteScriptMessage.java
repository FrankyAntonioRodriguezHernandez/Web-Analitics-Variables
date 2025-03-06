/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cu.redcuba.message;

import java.util.List;
import cu.redcuba.object.Website;

/**
 *
 * @author developer
 */
public class WebsiteScriptMessage {

    private Website website;

    private String lastUrl;

    private List<Object[]> scripts;

    public WebsiteScriptMessage(Website website, String lastUrl, List<Object[]> scripts) {
        this.website = website;
        this.lastUrl = lastUrl;
        this.scripts = scripts;
    }

    public Website getWebsite() {
        return website;
    }

    public void setWebsite(Website website) {
        this.website = website;
    }

    public String getLastUrl() {
        return lastUrl;
    }

    public void setLastUrl(String lastUrl) {
        this.lastUrl = lastUrl;
    }

    public List<Object[]> getScripts() {
        return scripts;
    }

    public void setScripts(List<Object[]> scripts) {
        this.scripts = scripts;
    }

}
