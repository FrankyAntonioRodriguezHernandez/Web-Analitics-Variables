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
public class WebsiteStyleMessage {

    private Website website;

    private String lastUrl;

    private List<Object[]> styles;

    public WebsiteStyleMessage(Website website, String lastUrl, List<Object[]> styles) {
        this.website = website;
        this.lastUrl = lastUrl;
        this.styles = styles;
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

    public List<Object[]> getStyles() {
        return styles;
    }

    public void setStyles(List<Object[]> styles) {
        this.styles = styles;
    }

}
