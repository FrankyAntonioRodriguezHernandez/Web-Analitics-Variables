package cu.redcuba.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

public class WebsiteTitlePK implements Serializable{
    private static final long serialVersionUID = 1L;

    @Column(name = "website_id", nullable = false)
    protected Long websiteId;

    @Column(name = "day", nullable = false)
    protected String day;

    public WebsiteTitlePK() {}

    public WebsiteTitlePK(Long websiteId, String day) {
        this.websiteId = websiteId;
        this.day = day;
    }
}
