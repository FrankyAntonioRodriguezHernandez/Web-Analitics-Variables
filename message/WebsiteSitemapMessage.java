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
public class WebsiteSitemapMessage {

    private Website website;

    private String lastUrl;

    private List<String> sitemaps;

    public WebsiteSitemapMessage(Website website, String lastUrl, List<String> sitemaps) {
        this.website = website;
        this.lastUrl = lastUrl;
        this.sitemaps = sitemaps;
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

    public List<String> getSitemaps() {
        return sitemaps;
    }

    public void setSitemaps(List<String> sitemaps) {
        this.sitemaps = sitemaps;
    }

}
