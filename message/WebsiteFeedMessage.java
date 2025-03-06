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
public class WebsiteFeedMessage {

    private Website website;

    private String lastUrl;

    private List<Object[]> linksFeed;

    public WebsiteFeedMessage(Website website, String lastUrl, List<Object[]> linksFeed) {
        this.website = website;
        this.lastUrl = lastUrl;
        this.linksFeed = linksFeed;
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

    public List<Object[]> getLinksFeed() {
        return linksFeed;
    }

    public void setLinksFeed(List<Object[]> linksFeed) {
        this.linksFeed = linksFeed;
    }

}
