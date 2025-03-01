package cu.redcuba.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Embeddable
public class WebsiteDescriptionPK implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "website_id", nullable = false)
        protected Long websiteId;

        @Column(name = "day", nullable = false)
        protected String day;

    public WebsiteDescriptionPK() {}

    public WebsiteDescriptionPK(Long websiteId, String day) {
            this.websiteId = websiteId;
            this.day = day;
        }

    }
