package cu.redcuba.helper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author developer
 */
public class HtmlContentHelper {

    private final Document doc;

    private String firstLine;

    private final Pattern cssImportDirectivePattern = Pattern.compile("@import\\s+url\\(['\"](.+?)['\"]\\)", Pattern.MULTILINE | Pattern.DOTALL);

    private final Pattern gaTrackingIdScriptGtagPattern = Pattern.compile("//www\\.googletagmanager\\.com/gtag/js\\?id=(UA\\-\\d+\\-\\d)", Pattern.MULTILINE | Pattern.DOTALL);

    private final Pattern emailPattern = Pattern.compile("([a-z0-9_.-]+)@([a-z0-9_.-]+[a-z])");

    public HtmlContentHelper(String content) {
        doc = Jsoup.parse(content);
        try {
            firstLine = content.split("\n", 2)[0];
        } catch (Exception ignored) {
            // Nothing to do here
        }
    }

    public String getFlash() {
        Element element = doc.select("object[type=application/x-shockwave-flash]").first();
        return element != null ? element.text().trim() : null;
    }

    public String getSilverlight() {
        Element element = doc.select("object[type=application/x-silverlight]").first();
        return element != null ? element.text().trim() : null;
    }

    public String getJava() {
        Element element = doc.select("object[type=application/x-java-vm]").first();
        return element != null ? element.text().trim() : null;
    }

    public String getFirstLine() {
        return firstLine;
    }

    public String getHtmlLang() {
        Element element = doc.select("html").first();
        return (element != null && !element.attr("lang").trim().isEmpty()) ? element.attr("lang").trim() : null;
    }

    public String getTitle() {
        Element element = doc.select("title").first();
        return element != null ? element.text().trim() : null;
    }

    public String getMetaCharset() {
        Element element = doc.select("meta[charset]").first();
        return (element != null && !element.attr("charset").trim().isEmpty()) ? element.attr("charset").trim() : null;
    }

    public String getMetaHttpEquiv() {
        Element element = doc.select("meta[http-equiv=content-type]").first();
        return (element != null && !element.attr("content").trim().isEmpty()) ? element.attr("content").trim() : null;
    }

    public String getMetaDescription() {
        Element element = doc.select("meta[name=description]").first();
        return (element != null && !element.attr("content").trim().isEmpty()) ? element.attr("content").trim() : null;
    }

    public String getMetaKeywords() {
        Element element = doc.select("meta[name=keywords]").first();
        return (element != null && !element.attr("content").trim().isEmpty()) ? element.attr("content").trim() : null;
    }

    public String getMetaViewport() {
        Element element = doc.select("meta[name=viewport]").first();
        return (element != null && !element.attr("content").trim().isEmpty()) ? element.attr("content").trim() : null;
    }

    public String getLinkCanonical() {
        Element element = doc.select("link[rel=canonical]").first();
        return (element != null && !element.attr("href").trim().isEmpty()) ? element.attr("href").trim() : null;
    }

    public String getLinkIcon() {
        Element element = doc.select("link[rel=icon]").first();
        return (element != null && !element.attr("href").trim().isEmpty()) ? element.attr("href").trim() : null;
    }

    public String getLinkShortcutIcon() {
        Element element = doc.select("link[rel=shortcut icon]").first();
        return (element != null && !element.attr("href").trim().isEmpty()) ? element.attr("href").trim() : null;
    }

    public String getScriptGoogleAnalytics() {
        Elements elements = doc.select("script");
        for (Element element : elements) {
            if (element.html().contains("//www.google-analytics.com/analytics.js")) {
                return element.html().trim();
            }
        }
        // TODO: En algunos casos no se detecta bien con el selector script.
        if (doc.html().contains("www.google-analytics.com/analytics.js")) {
            return doc.html();
        }
        return null;
    }

    @Deprecated(since = "2019-03-19 Deja de emplearse argumentado que ya no se genera, se mantiene para que esté documentado su uso.")
    public String getImageGoogleAnalytics() {
        Element element = doc.select("img[src*=//www.google-analytics.com/__utm.gif]").first();
        return (element != null && !element.attr("src").trim().isEmpty()) ? element.attr("src").trim() : null;
    }

    public String getGaTrackingIdScriptGoogleGtag() {
        Elements elements = doc.select("script");
        for (Element element : elements) {
            String aux = element.attr("src");
            Matcher matcher = gaTrackingIdScriptGtagPattern.matcher(aux);
            if (matcher.find()) {
                return matcher.group(1);
            } else {
                if (element.html().contains("//www.googletagmanager.com/gtm")) {
                    return element.html();
                }
            }
        }
        return null;
    }

    public String getScriptPiwik() {
        Elements elements = doc.select("script");
        for (Element element : elements) {
            if (element.html().contains("piwik.js")) {
                return element.html().trim();
            }
        }
        return null;
    }

    public String getImagePiwik() {
        Element element = doc.select("img[src*=piwik.php]").first();
        return (element != null && !element.attr("src").trim().isEmpty()) ? element.attr("src").trim() : null;
    }

    public String getScriptMatomo() {
        Elements elements = doc.select("script");
        for (Element element : elements) {
            if (element.html().contains("matomo.js")) {
                return element.html().trim();
            }
        }
        return null;
    }

    public String getScriptRedCuba() {
        Elements elements = doc.select("script");
        for (Element element : elements) {
            if (element.html().contains("telus.min.js")) {
                return element.html().trim();
            }
        }
        // TODO: En algunos casos no se detecta bien con el selector script.
        if (doc.html().contains("telus.min.js")) {
            return doc.html();
        }
        return null;
    }

    public String getImageMatomo() {
        Element element = doc.select("img[src*=matomo.php]").first();
        return (element != null && !element.attr("src").trim().isEmpty()) ? element.attr("src").trim() : null;
    }

    public List<Object[]> getLinkFeed() {
        List<Object[]> linksFeed = new ArrayList<>();
        Elements linkTags = doc.select("link[rel=alternate][type*=xml][href]");
        linkTags.forEach((linkTag) -> {
            linksFeed.add(new Object[]{
                    linkTag.attr("href").trim(),
                    linkTag.attr("type").trim(),
                    linkTag.attr("title").trim()
            });
        });
        return linksFeed;
    }

    public List<Object[]> getLinkCss() {
        List<Object[]> linksCss = new ArrayList<>();
        Elements linkTags = doc.select("link[rel=stylesheet][href]");
        linkTags.forEach((linkTag) -> {
            linksCss.add(new Object[]{
                    linkTag.attr("href").trim(),
                    linkTag.attr("type").trim(),
                    linkTag.attr("media").trim()
            });
        });
        return linksCss;
    }

    public List<Object[]> getStyleImportCss() {
        List<Object[]> styleImportCss = new ArrayList<>();
        Elements styleTags = doc.select("style");
        styleTags.forEach((styleTag) -> {
            String html = styleTag.html();
            Matcher matcher = cssImportDirectivePattern.matcher(html);
            while (matcher.find()) {
                styleImportCss.add(new Object[]{
                        matcher.group(1).trim(),
                        "style/import"
                });
            }
        });
        return styleImportCss;
    }

    public List<Object[]> getScriptJs() {
        List<Object[]> scriptsJs = new ArrayList<>();
        Elements scriptTags = doc.select("script[src]");
        scriptTags.forEach((linkTag) -> {
            scriptsJs.add(new Object[]{
                    linkTag.attr("src").trim(),
                    linkTag.attr("type").trim()
            });
        });
        return scriptsJs;
    }

    public Map<String, String> getMetasTwitter() {
        Map<String, String> metas = new HashMap<>();
        Elements metaTags = doc.select("meta[name^=twitter:]");
        metaTags.forEach(metaTag -> {
            String name = metaTag.attr("name").trim();
            String content = metaTag.attr("content").trim();
            metas.put(name, content);
        });
        return metas;
    }

    public Map<String, String> getMetasOg() {
        Map<String, String> metas = new HashMap<>();
        Elements metaTags = doc.select("meta[property^=og:]");
        metaTags.forEach(metaTag -> {
            String property = metaTag.attr("property").trim();
            String content = metaTag.attr("content").trim();
            metas.put(property, content);
        });
        return metas;
    }

    public Map<String, String> getMetasArticle() {
        Map<String, String> metas = new HashMap<>();
        Elements metaTags = doc.select("meta[property^=article:]");
        metaTags.forEach((metaTag) -> {
            String property = metaTag.attr("property").trim();
            String content = metaTag.attr("content").trim();
            metas.put(property, content);
        });
        return metas;
    }

    public Map<String, String> getMetasFb() {
        Map<String, String> metas = new HashMap<>();
        Elements metaTags = doc.select("meta[property^=fb:]");
        metaTags.forEach((metaTag) -> {
            String property = metaTag.attr("property").trim();
            String content = metaTag.attr("content").trim();
            metas.put(property, content);
        });
        return metas;
    }

    public int getHeaderAmount() {
        Elements headerTags = doc.select("header");
        return headerTags.size();
    }

    public int getFooterAmount() {
        Elements footerTags = doc.select("footer");
        return footerTags.size();
    }

    public int getNavAmount() {
        Elements navTags = doc.select("nav");
        return navTags.size();
    }

    public int getArticleAmount() {
        Elements articleTags = doc.select("article");
        return articleTags.size();
    }

    public int getSectionAmount() {
        Elements sectionTags = doc.select("section");
        return sectionTags.size();
    }

    public List<String> getPublicEmails() {
        Matcher matcher = emailPattern.matcher(doc.html());

        List<String> matches = new ArrayList<>();
        boolean found = matcher.find();
        while (found) {
            String match = matcher.group().trim();
            matches.add(match);
            found = matcher.find();
        }

        // Filtrar solo correos distintos.
        return new ArrayList<>(new HashSet<>(matches));
    }

    public List<Object[]> getImages() {
        List<Object[]> imagesData = new ArrayList<>();
        Elements imgTags = doc.select("img");
        imgTags.forEach((imgTag) -> {
            imagesData.add(new Object[]{
                    imgTag.attr("src").trim(),
                    imgTag.attr("alt").trim()
            });
        });
        return imagesData;
    }
}
