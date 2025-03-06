package cu.redcuba.worker;

import cu.redcuba.factory.VariableFactory;
import cu.redcuba.helper.CastHelper;
import cu.redcuba.object.Website;
import cu.redcuba.output.CharsetOutput;

import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

@Service
public class CharsetWorker extends AbstractWorker<CharsetOutput> {

    private final Pattern metaHttpEquivContentTypePattern = Pattern.compile(".*charset=(.*)");

    /**
     * Returns the ID of the variable evaluated by this worker.
     *
     * @return The variable's ID.
     */
    @Override
    long getVariableId() {
        return VariableFactory.VAR_META_CHARSET;
    }

    /**
     * Analyses the values of a particular indicator.
     *
     * @param args Arguments needed by the worker.
     *             In this case is expected:
     *             <ul>
     *             <li>charset</li>
     *             <li>http-equiv-content-type</li>
     *             </ul>
     * @return The analysis result.
     */
    @Override
    public CharsetOutput analyse(Object... args) {
        final String metaCharset = CastHelper.cast(args[0], String.class);
        final String metaHttpEquivContentType = CastHelper.cast(args[1], String.class);

        CharsetOutput output = new CharsetOutput();

        output.setValue(VALUE_INCORRECT);

        if (metaCharset != null && !metaCharset.isEmpty()) {
            output.setExist(true);

            output.setRecommended(true);

            output.setValue(VALUE_TO_IMPROVE);

            try {
                output.setValid(Charset.isSupported(metaCharset));
            } catch (IllegalCharsetNameException ignored) {
                output.setValid(false);
            }

            if (output.isValid()) {
                output.setValue(VALUE_CORRECT);
            }

            return output;
        }

        if (metaHttpEquivContentType != null && !metaHttpEquivContentType.isEmpty()) {
            output.setExist(true);

            output.setRecommended(false);

            Matcher matcher = metaHttpEquivContentTypePattern.matcher(metaHttpEquivContentType);

            if (matcher.find()) {
                String metaHttpEquivContentTypeCharset = matcher.group(1).trim();

                try {
                    output.setValid(Charset.isSupported(metaHttpEquivContentTypeCharset));
                } catch (IllegalCharsetNameException ignored) {
                    output.setValid(false);
                }
            } else {
                output.setValid(false);
            }

            if (output.isValid()) {
                output.setValue(VALUE_TO_IMPROVE);
            }

            return output;
        }

//        if (metaCharset == null || metaCharset.isEmpty()) {
//            output.setExist(false);
//        } else {
//            output.setExist(true);
//            output.setValue(VALUE_TO_IMPROVE);
//
//            try {
//                output.setValid(Charset.isSupported(metaCharset));
//            } catch (IllegalCharsetNameException ignored) {
//                output.setValid(false);
//            }
//        }
//
//        if (metaHttpEquivContentType == null || metaHttpEquivContentType.isEmpty()) {
//            output.setExistMetaHttpEquivContentType(false);
//        } else {
//            output.setExistMetaHttpEquivContentType(true);
//
//            Matcher matcher = metaHttpEquivContentTypePattern.matcher(metaHttpEquivContentType);
//
//            if (matcher.find()) {
//                String metaHttpEquivContentTypeCharset = matcher.group(1).trim();
//
//                try {
//                    output.setValidMetaHttpEquivContentType(Charset.isSupported(metaHttpEquivContentTypeCharset));
//                } catch (IllegalCharsetNameException ignored) {
//                    output.setValidMetaHttpEquivContentType(false);
//                }
//            } else {
//                output.setValidMetaHttpEquivContentType(false);
//            }
//        }
//
//        if (output.isValid()) {
//            output.setValue(VALUE_CORRECT);
//        } else if (output.isValidMetaHttpEquivContentType()) {
//            output.setValue(VALUE_TO_IMPROVE);
//        }

        return output;
    }

    /**
     * Analyses the language value, inserts the result into the database and sends to Influx a report.
     *
     * @param website The {@link Website} instance with values related to the evaluated website.
     * @param args    Arguments needed by the worker.
     *                In this case is expected:
     *                <ul>
     *                <li>charset</li>
     *                <li>http-equiv-content-type</li>
     *                </ul>
     */
    public void fullWork(Website website, Object... args) {
        final String metaCharset = CastHelper.cast(args[0], String.class);
        final String metaHttpEquivContentType = CastHelper.cast(args[1], String.class);

        CharsetOutput output = analyse(metaCharset, metaHttpEquivContentType);

        save(website, getIndicatorSlugWithPrefix(), output.getValue());
        save(website, getIndicatorSlugWithPrefix("exist"), output.exist());
        save(website, getIndicatorSlugWithPrefix("valid"), output.isValid());
        save(website, getIndicatorSlugWithPrefix("recommended"), output.isRecommended());
//        save(website, getIndicatorSlugWithPrefix("exist-http-equiv-content-type"), output.existMetaHttpEquivContentType());
//        save(website, getIndicatorSlugWithPrefix("valid-http-equiv-content-type"), output.isValidMetaHttpEquivContentType());

        //Reporting the evaluation to the monitor
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
