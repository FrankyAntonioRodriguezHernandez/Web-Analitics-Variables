package cu.redcuba.worker;

import cu.redcuba.entity.Visit;
import cu.redcuba.factory.VariableFactory;
import cu.redcuba.helper.CastHelper;
import cu.redcuba.helper.UrlFetchHelper;
import cu.redcuba.object.Website;
import cu.redcuba.output.FaviconOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FaviconWorker extends AbstractWorker<FaviconOutput> {

    private final UrlFetchHelper normalUrlFetchHelper;

    @Autowired
    public FaviconWorker(UrlFetchHelper normalUrlFetchHelper) {
        this.normalUrlFetchHelper = normalUrlFetchHelper;
    }

    @Override
    long getVariableId() {
        return VariableFactory.VAR_FAVICON;
    }

    /**
     * Analyses the values of a particular indicator.
     *
     * @param args Arguments needed by the worker. In this case is expected:
     * <ul>
     * <li>lastUrl</li>
     * <li>xFrameOptions</li>
     * </ul>
     * @return The analysis result.
     */
    @Override
    public FaviconOutput analyse(Object... args) {

        final String hrefLinkIcon = CastHelper.cast(args[0], String.class);
        final String hrefLinkShortcutIcon = CastHelper.cast(args[1], String.class);
        final String url = CastHelper.cast(args[2], String.class);

        FaviconOutput output = new FaviconOutput();

        output.setValidFavicon(false);
        output.setExistFavicon(false);
        if (hrefLinkIcon != null) {
            if (hrefLinkIcon.contains(".ico") || hrefLinkIcon.contains(".png") || hrefLinkIcon.contains(".jpg") || hrefLinkIcon.contains(".gif") || hrefLinkIcon.contains(".svg")) {
                if (hrefLinkIcon.contains("http")) {
                    Visit randomVisitLinkIcon = normalUrlFetchHelper.visitUnstored(hrefLinkIcon);
                    output.setExistFavicon(true);
                    if (!randomVisitLinkIcon.getLastUrl().equals("")) {
                        output.setValidFavicon(true);
                    }
                } else {
                    if (hrefLinkIcon.charAt(0) == '/') {
                        Visit randomVisitLinkIcon = normalUrlFetchHelper.visitUnstored(url.concat(hrefLinkIcon));
                        output.setExistFavicon(true);
                        if (!randomVisitLinkIcon.getLastUrl().equals("")) {
                            output.setValidFavicon(true);
                        }
                    } else {
                        Visit randomVisitLinkIcon = normalUrlFetchHelper.visitUnstored(url.concat("/") + hrefLinkIcon);
                        output.setExistFavicon(true);
                        if (!randomVisitLinkIcon.getLastUrl().equals("")) {
                            output.setValidFavicon(true);
                        }
                    }
                }
            }
        }

        if (hrefLinkShortcutIcon != null) {
            if (hrefLinkShortcutIcon.contains(".ico") || hrefLinkShortcutIcon.contains(".png") || hrefLinkShortcutIcon.contains(".jpg") || hrefLinkShortcutIcon.contains(".gif") || hrefLinkShortcutIcon.contains(".svg")) {
                if (hrefLinkShortcutIcon.contains("http")) {
                    Visit randomVisitLinkIcon = normalUrlFetchHelper.visitUnstored(hrefLinkShortcutIcon);
                    output.setExistFavicon(true);
                    if (!randomVisitLinkIcon.getLastUrl().equals("")) {
                        output.setValidFavicon(true);
                    }
                } else {
                    if (hrefLinkShortcutIcon.charAt(0) == '/') {
                        Visit randomVisitLinkIcon = normalUrlFetchHelper.visitUnstored(url.concat(hrefLinkShortcutIcon));
                        output.setExistFavicon(true);
                        if (!randomVisitLinkIcon.getLastUrl().equals("")) {
                            output.setValidFavicon(true);
                        }
                    } else {
                        Visit randomVisitLinkIcon = normalUrlFetchHelper.visitUnstored(url.concat("/") + hrefLinkShortcutIcon);
                        output.setExistFavicon(true);
                        if (!randomVisitLinkIcon.getLastUrl().equals("")) {
                            output.setValidFavicon(true);
                        }
                    }
                }
            }
        }

        if (output.ExistFavicon() && output.ValidFavicon()) {
            output.setValue(VALUE_CORRECT);
        } else if (output.ExistFavicon() && !output.ValidFavicon()) {
            output.setValue(VALUE_TO_IMPROVE);
        } else {
            output.setValue(VALUE_INCORRECT);
        }
        return output;
    }

    /**
     * Analyses the values, inserts the results into the database and sends to
     * Influx a report.
     *
     * @param website The {@link Website} instance with values related to the
     * evaluated website.
     * @param args Arguments needed by the worker.
     */
    public void fullWork(Website website, Object... args) {

        FaviconOutput output = analyse(args);

        save(website, getIndicatorSlugWithPrefix(), output.getValue());
        save(website, getIndicatorSlugWithPrefix("exist"), output.ExistFavicon());
        save(website, getIndicatorSlugWithPrefix("valid"), output.ValidFavicon());

        // Reporting the evaluation to the monitor
        sendEvaluation(website, output);
    }

    /**
     * Evaluate if the website applies for having this variable evaluation
     *
     * @param args
     * @return {@link Boolean}
     */
    @Override
    boolean appliesForEvaluation(Object... args) {
        return true;
    }

}
