package cu.redcuba.factory;

import cu.redcuba.entity.Variable;
import cu.redcuba.entity.VariableIndicator;
import cu.redcuba.repository.VariableIndicatorRepository;
import cu.redcuba.repository.VariableRepository;
import cu.redcuba.worker.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Profile({"consumer", "command", "producer"})
public class VariableFactory {

    public static final int VAR_WWW = 1;
    public static final int VAR_ROBOTS = 2;
    public static final int VAR_SITEMAP = 3;
    public static final int VAR_FEEDS = 4;
    public static final int VAR_HTML_LANG = 5;
    public static final int VAR_META_CHARSET = 6;
    public static final int VAR_LINK_CANONICAL = 7;
    public static final int VAR_TWITTER_CARD = 8;
    public static final int VAR_OPEN_GRAPH = 9;
    public static final int VAR_ANALYTIC_TOOLS = 10;
    public static final int VAR_TITLE = 11;
    public static final int VAR_STATUS_CODE = 12;
    public static final int VAR_DESCRIPTION = 13;
    public static final int VAR_KEYWORDS = 14;
    public static final int VAR_COMPRESSION = 15;
    public static final int VAR_CACHE_HEADER = 16;
    public static final int VAR_MINIFIED_CSS = 17;
    public static final int VAR_MINIFIED_JS = 18;
    public static final int VAR_MINIFIED_HTML = 19;
    public static final int VAR_HTML5 = 20;
    public static final int VAR_WEIGHT = 21;
    public static final int VAR_HIDING_404 = 22;
    public static final int VAR_MINIFIED_CSS2 = 23;
    public static final int VAR_MINIFIED_JS2 = 24;
    public static final int VAR_AVAILABILITY = 25;
    public static final int VAR_RESPONSE_TIME = 26;
    public static final int VAR_SECURE_PROTOCOL = 27;
    public static final int VAR_META_VIEW_PORT = 28;
    public static final int VAR_X_FRAME_OPTIONS = 29;
    public static final int VAR_SCREEN_RESOLUTION = 30;
    public static final int VAR_FAVICON = 31;
    public static final int VAR_EMAIL_PRIVACY = 32;
    public static final int VAR_X_CONTENT_TYPE_OPTIONS = 33;
    public static final int VAR_X_XSS_PROTECTION = 34;
    public static final int VAR_PLUGIN = 35;
    public static final int VAR_IMAGES_ALT = 36;
    public static final int VAR_BROKEN_LINK = 37;
    public static final int VAR_RESOURCE_LOAD = 38;
    public static final int VAR_SUBRESOURCE_INTEGRITY = 39;

    public static final int VAR_STRICT_TRANSPORT_SECURITY = 40;

    public static final int VAR_SEGURIDAD_COOKIES = 41;
    public static final int VAR_CONTENT_SECURITY_POLICY = 42;
    public static final int VAR_REFERRER_POLICY = 43;

    private static final Map<Integer, AbstractWorker> WORKERS = new HashMap<>();
    private final VariableRepository variableRepository;
    private final VariableIndicatorRepository variableIndicatorRepository;

    @Autowired
    public VariableFactory(
            VariableRepository variableRepository,
            VariableIndicatorRepository variableIndicatorRepository) {
        this.variableRepository = variableRepository;
        this.variableIndicatorRepository = variableIndicatorRepository;
    }

    public Variable getVariable(long id) {
        return variableRepository.findById(id).get();
    }

    public VariableIndicator getIndicator(long id) {
        return variableIndicatorRepository.findById(id).get();
    }

    public VariableIndicator getIndicator(String slug) {
        return variableIndicatorRepository.findBySlug(slug);
    }

    public static AbstractWorker getWorker(int id) {
        return WORKERS.get(id);
    }

    @Autowired
    public void addWorker(WwwRedirectWorker worker) {
        WORKERS.put(VAR_WWW, worker);
    }

    @Autowired
    public void addWorker(RobotsWorker worker) {
        WORKERS.put(VAR_ROBOTS, worker);
    }

    @Autowired
    public void addWorker(SitemapWorker worker) {
        WORKERS.put(VAR_SITEMAP, worker);
    }

    @Autowired
    public void addWorker(FeedWorker worker) {
        WORKERS.put(VAR_FEEDS, worker);
    }

    @Autowired
    public void addWorker(LanguageWorker worker) {
        WORKERS.put(VAR_HTML_LANG, worker);
    }

    @Autowired
    public void addWorker(CharsetWorker worker) {
        WORKERS.put(VAR_META_CHARSET, worker);
    }

    @Autowired
    public void addWorker(CanonicalURLWorker worker) {
        WORKERS.put(VAR_LINK_CANONICAL, worker);
    }

    @Autowired
    public void addWorker(TwitterCardWorker worker) {
        WORKERS.put(VAR_TWITTER_CARD, worker);
    }

    @Autowired
    public void addWorker(OpenGraphWorker worker) {
        WORKERS.put(VAR_OPEN_GRAPH, worker);
    }

    @Autowired
    public void addWorker(AnalyticToolsWorker worker) {
        WORKERS.put(VAR_ANALYTIC_TOOLS, worker);
    }

    @Autowired
    public void addWorker(TitleWorker worker) {
        WORKERS.put(VAR_TITLE, worker);
    }

    @Autowired
    public void addWorker(StatusCodeWorker worker) {
        WORKERS.put(VAR_STATUS_CODE, worker);
    }

    @Autowired
    public void addWorker(DescriptionWorker worker) {
        WORKERS.put(VAR_DESCRIPTION, worker);
    }

    @Autowired
    public void addWorker(KeywordsWorker worker) {
        WORKERS.put(VAR_KEYWORDS, worker);
    }

    @Autowired
    public void addWorker(CompressWorker worker) {
        WORKERS.put(VAR_COMPRESSION, worker);
    }

    @Autowired
    public void addWorker(CacheWorker worker) {
        WORKERS.put(VAR_CACHE_HEADER, worker);
    }

    @Autowired
    public void addWorker(MinifiedCSSWorker worker) {
        WORKERS.put(VAR_MINIFIED_CSS, worker);
    }

    @Autowired
    public void addWorker(MinifiedJSWorker worker) {
        WORKERS.put(VAR_MINIFIED_JS, worker);
    }

    @Autowired
    public void addWorker(MinifiedHTMLWorker worker) {
        WORKERS.put(VAR_MINIFIED_HTML, worker);
    }

    @Autowired
    public void addWorker(HTML5Worker worker) {
        WORKERS.put(VAR_HTML5, worker);
    }

    @Autowired
    public void addWorker(WeightWorker worker) {
        WORKERS.put(VAR_WEIGHT, worker);
    }

    @Autowired
    public void addWorker(Hiding404Worker worker) {
        WORKERS.put(VAR_HIDING_404, worker);
    }

    @Autowired
    public void addWorker(Style2Worker worker) {
        WORKERS.put(VAR_MINIFIED_CSS2, worker);
    }

    @Autowired
    public void addWorker(Script2Worker worker) {
        WORKERS.put(VAR_MINIFIED_JS2, worker);
    }

    @Autowired
    public void addWorker(AvailabilityWorker worker) {
        WORKERS.put(VAR_AVAILABILITY, worker);
    }

    @Autowired
    public void addWorker(ResponseTimeWorker worker) {
        WORKERS.put(VAR_RESPONSE_TIME, worker);
    }

    @Autowired
    public void addWorker(SecureProtocolWorker worker) {
        WORKERS.put(VAR_SECURE_PROTOCOL, worker);
    }

    @Autowired
    public void addWorker(MetaViewPortWorker worker) {
        WORKERS.put(VAR_META_VIEW_PORT, worker);
    }

    @Autowired
    public void addWorker(XFrameOptionsWorker worker) {
        WORKERS.put(VAR_X_FRAME_OPTIONS, worker);
    }

    @Autowired
    public void addWorker(FaviconWorker worker) {
        WORKERS.put(VAR_FAVICON, worker);
    }

    @Autowired
    public void addWorker(XContentTypeOptionsWorker worker) {
        WORKERS.put(VAR_X_CONTENT_TYPE_OPTIONS, worker);
    }

    @Autowired
    public void addWorker(XXssProtectionWorker worker) {
        WORKERS.put(VAR_X_XSS_PROTECTION, worker);
    }

//    @Autowired
//    public void addWorker(ScreenResolutionWorker worker) {
//        WORKERS.put(VAR_SCREEN_RESOLUTION, worker);
//    }

    @Autowired
    public void addWorker(ImagesAltWorker worker) {
        WORKERS.put(VAR_IMAGES_ALT, worker);
    }

    @Autowired
    public void addWorker(PluginWorker worker) {
        WORKERS.put(VAR_PLUGIN, worker);
    }

    @Autowired
    public void addWorker(EmailPrivacyWorker worker) {
        WORKERS.put(VAR_EMAIL_PRIVACY, worker);
    }

    @Autowired
    public void addWorker(BrokenLinkWorker worker) {
        WORKERS.put(VAR_BROKEN_LINK, worker);
    }
    
    @Autowired
    public void addWorker(ResourceLoadWorker worker) {
        WORKERS.put(VAR_RESOURCE_LOAD, worker);
    }

    @Autowired
    public void addWorker(SubresourceIntegrityWorker worker) {
        WORKERS.put(VAR_SUBRESOURCE_INTEGRITY, worker);
    }

    @Autowired
    public void addWorker(StrictTransportSecurityWorker worker) {
        WORKERS.put(VAR_STRICT_TRANSPORT_SECURITY, worker);
    }

    @Autowired
    public void addWorker(CookiesSecurityWorker worker) {
        WORKERS.put(VAR_SEGURIDAD_COOKIES, worker);
    }

    @Autowired
    public void addWorker(ContentSecurityPolicyWorker worker) {
        WORKERS.put(VAR_CONTENT_SECURITY_POLICY, worker);
    }

    @Autowired
    public void addWorker(ReferrerPolicyWorker worker) {
        WORKERS.put(VAR_REFERRER_POLICY, worker);
    }
}
