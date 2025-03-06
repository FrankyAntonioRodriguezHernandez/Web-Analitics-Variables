package cu.redcuba.worker;

import com.google.gson.Gson;
import com.google.javascript.jscomp.*;
import cu.redcuba.evaluations.reporter.EvaluationsReporter;
import cu.redcuba.output.MinifiedJSOutput;
import cu.redcuba.factory.DailyEvaluationFactory;
import cu.redcuba.factory.VariableFactory;
import cu.redcuba.helper.CastHelper;
import cu.redcuba.helper.MinifiedHelper;
import cu.redcuba.helper.UrlFetchHelper;
import cu.redcuba.object.Website;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

// TODO: 4/12/17 Ver bien lo del metodo este
@Component
public class Script2Worker extends AbstractWorker<MinifiedJSOutput> {

    private static final Logger LOG = Logger.getLogger(Script2Worker.class.getName());

    private static final Gson GSON = new Gson();

    private final Float minMinifiedPercent;

    private final boolean turnOffMinificationMethod;

    private final UrlFetchHelper normalUrlFetchHelper;

    private final MinifiedHelper minifiedHelper;

    private final DailyEvaluationFactory dailyEvaluationFactory;

    //private final EvaluationsReporter evaluationsReporter;

    @Autowired
    public Script2Worker(
            @Value("${worker.script.minMinifiedPercent}") Float minMinifiedPercent,
            @Value("${worker.script.turnOffMinificationMethod}") boolean turnOffMinificationMethod,
            UrlFetchHelper normalUrlFetchHelper,
            MinifiedHelper minifiedHelper,
            DailyEvaluationFactory dailyEvaluationFactory,
            EvaluationsReporter evaluationsReporter) {
        this.minMinifiedPercent = minMinifiedPercent;
        this.turnOffMinificationMethod = turnOffMinificationMethod;
        this.normalUrlFetchHelper = normalUrlFetchHelper;
        this.minifiedHelper = minifiedHelper;
        this.dailyEvaluationFactory = dailyEvaluationFactory;
        this.evaluationsReporter = evaluationsReporter;
    }

    /**
     * Returns the ID of the variable evaluated by this worker.
     *
     * @return The variable's ID.
     */
    @Override
    long getVariableId() {
        return VariableFactory.VAR_MINIFIED_JS2;
    }

    /**
     * Analyses the values of a particular indicator.
     *
     * @param args Arguments needed by the worker.
     *             In this case is expected:
     *             <ul>
     *             <li>last-url</li>
     *             <li>scripts</li>
     *             </ul>
     * @return The analysis result.
     */
    @Override
    MinifiedJSOutput analyse(Object... args) {
        String lastUrl = CastHelper.cast(args[0], String.class);
        @SuppressWarnings("unchecked") List<Object[]> scripts = (List<Object[]>) args[1];

        MinifiedJSOutput output = new MinifiedJSOutput();

//        Date evDate = new Date();
//        float hasScriptMinified = 0, hasScriptMinified2 = 0;
//        int scriptsCount = scripts != null ? scripts.size() : 0;
//
//        if (scriptsCount > 0) {
//            int voter = 0, voter2 = 0;
//            hasScriptMinified = 0.5f;
//            hasScriptMinified2 = 0.5f;
//            for (Object[] scriptObject : scripts) {
//                String encoded = UrlFetchHelper.getUrlEncoded((String) scriptObject[0]);
//                String script = UrlFetchHelper.getAbsolute(encoded, lastUrl);
//
//                if (script != null) {
//                    Download scriptDownload = normalUrlFetchHelper.download(script, false);
//                    if (scriptDownload != null && validCode(scriptDownload.getCode()) && validContentType(scriptDownload.getContentType()) && validContent(scriptDownload.getContent())) {
//                         // Segundo método de comprobación de minificado.
//                        if (!turnOffMinificationMethod) {
//                            try {
//                                String minifiedContent = minifyScript(scriptDownload.getContent(), scriptDownload.getUrl());
//                                // Si el texto minificado no es vacío.
//                                if (minifiedContent != null) {
//                                    // Obtener el tamaño del contenido minificado.
//                                    long minifiedContentSize = minifiedContent.length();
//                                    // Calcular el porciento que representa el tamaño del contenido minificado respesto del original.
//                                    float minifiedPercent = (float) minifiedContentSize * 100 / scriptDownload.getContentLength();
//                                    // Si el porciento es superior a X se cuenta como minificado.
//                                    if (minifiedPercent > minMinifiedPercent) {
//                                        voter2++;
//                                    }
//                                }
//                            } catch (Exception ex) {
//                                LOG.log(Level.SEVERE, "MINIFIED2: " + ex.getMessage());
//                            }
//
//                            dailyEvaluationFactory.createAndSave(website, "minified-js2", "minified-js2-value", evDate, hasScriptMinified2);
//
//                            //Reporting the evaluation to the monitor
//                            evaluationsReporter.websitesEvaluationDay(
//                                    website,
//                                    "minified-js2",
//                                    hasScriptMinified2);
//                        }
//                    }
//                }
//            }
//
//            // Determinar si todos los JS enlazados están minificados según el primer método.
//            if (scriptsCount == voter) {
//                hasScriptMinified = 1;
//            }
//            // Determinar si todos los JS enlazados están minificados según el segundo método.
//            if (scriptsCount == voter2) {
//                hasScriptMinified2 = 1;
//            }
//        }

        return output;
    }

    /**
     * Analyses the values, inserts the results into the database and sends to Influx a report.
     *
     * @param website The {@link Website} instance with values related to the evaluated website.
     * @param args    Arguments needed by the worker.
     *                In this case is expected:
     *                <ul>
     *                <li>last-url</li>
     *                <li>scripts</li>
     *                </ul>
     */
    @Override
    void fullWork(Website website, Object... args) {
//        ScriptOutput output = analyse(args[0], args[1]);
//
//        save(website, getIndicatorSlugWithPrefix(), output.getValue());
//
//        // Reporting the evaluation to the monitor
//        sendEvaluation(website, output);
    }

//    /**
//     * Callback for processing a received Rabbit message.
//     * <p>Implementors are supposed to process the given Message,
//     * typically sending reply messages through the given Session.
//     *
//     * @param message the received AMQP message (never <code>null</code>)
//     * @param channel the underlying Rabbit Channel (never <code>null</code>)
//     * @throws Exception Any.
//     */
//    @Override
//    public void onMessage(Message message, Channel channel) throws Exception {
//        try {
//            // Getting the message.
//            WebsiteScriptMessage websiteScriptMessage = GSON.fromJson(new String(message.getBody()), WebsiteScriptMessage.class);
//            Website website = websiteScriptMessage.getWebsite();
//            String lastUrl = websiteScriptMessage.getLastUrl();
//            List<Object[]> scripts = websiteScriptMessage.getScripts();
//
//            fullWork(website, lastUrl, scripts);
//        } catch (JsonSyntaxException ex) {
//            LOG.log(Level.SEVERE, ex.getMessage());
//        } finally {
//            basicAck(message, channel);
//        }
//    }

    private boolean validCode(short code) {
        return code == 200;
    }

    private boolean validContent(String content) {
        return content != null;
    }

    private boolean validContentType(String contentType) {
        return contentType != null && (contentType.contains("gzip") || contentType.contains("deflate") || contentType.contains("javascript"));
    }

    /**
     * @param sourceCode JavaScript source code to compile.
     * @param scriptUrl  JavaScript url from source code to compile.
     * @return Compiled version of the code.
     */
    private String minifyScript(String sourceCode, String scriptUrl) {
        String compiledCode = null;
        try {
            // Obtener un compilador.
            com.google.javascript.jscomp.Compiler compiler = new com.google.javascript.jscomp.Compiler();
            // Establecer las opciones para ignorar todos los errores posibles.
            CompilerOptions options = new CompilerOptions();
            options.setWarningLevel(DiagnosticGroups.AMBIGUOUS_FUNCTION_DECL, CheckLevel.OFF);
            options.setWarningLevel(DiagnosticGroups.CHECK_TYPES, CheckLevel.OFF);
            options.setWarningLevel(DiagnosticGroups.CHECK_VARIABLES, CheckLevel.OFF);
            options.setWarningLevel(DiagnosticGroups.CLOSURE_DEP_METHOD_USAGE_CHECKS, CheckLevel.OFF);
            options.setWarningLevel(DiagnosticGroups.CONFORMANCE_VIOLATIONS, CheckLevel.OFF);
            options.setWarningLevel(DiagnosticGroups.DUPLICATE_MESSAGE, CheckLevel.OFF);
            options.setWarningLevel(DiagnosticGroups.DUPLICATE_VARS, CheckLevel.OFF);
            options.setWarningLevel(DiagnosticGroups.ES3, CheckLevel.OFF);
            options.setWarningLevel(DiagnosticGroups.ES5_STRICT, CheckLevel.OFF);
            options.setWarningLevel(DiagnosticGroups.EXTERNS_VALIDATION, CheckLevel.OFF);
            options.setWarningLevel(DiagnosticGroups.FILEOVERVIEW_JSDOC, CheckLevel.OFF);
            options.setWarningLevel(DiagnosticGroups.GLOBAL_THIS, CheckLevel.OFF);
            options.setWarningLevel(DiagnosticGroups.INTERNET_EXPLORER_CHECKS, CheckLevel.OFF);
            options.setWarningLevel(DiagnosticGroups.INVALID_CASTS, CheckLevel.OFF);
            options.setWarningLevel(DiagnosticGroups.MISPLACED_TYPE_ANNOTATION, CheckLevel.OFF);
            options.setWarningLevel(DiagnosticGroups.NON_STANDARD_JSDOC, CheckLevel.OFF);
            options.setWarningLevel(DiagnosticGroups.SUSPICIOUS_CODE, CheckLevel.OFF);
            options.setWarningLevel(DiagnosticGroups.UNDEFINED_VARIABLES, CheckLevel.OFF);
            options.setWarningLevel(DiagnosticGroups.UNKNOWN_DEFINES, CheckLevel.OFF);
            options.setWarningLevel(DiagnosticGroups.VIOLATED_MODULE_DEP, CheckLevel.OFF);
            options.setWarningLevel(DiagnosticGroups.CHECK_USELESS_CODE, CheckLevel.OFF);
            options.setWarningLevel(DiagnosticGroups.CHECK_REGEXP, CheckLevel.OFF);
            // Advanced mode is used here, but additional options could be set, too.
            //CompilationLevel.ADVANCED_OPTIMIZATIONS.setOptionsForCompilationLevel(options);
            CompilationLevel.SIMPLE_OPTIMIZATIONS.setOptionsForCompilationLevel(options);
            // To get the complete set of externs, the logic in
            // CompilerRunner.getDefaultExterns() should be used here.
            SourceFile extern = SourceFile.fromCode("externs.js", "");
            // The dummy input name "input.js" is used here so that any warnings or
            // errors will cite line numbers in terms of input.js.
            SourceFile input = SourceFile.fromCode(scriptUrl, sourceCode);
            // compile() returns a Result, but it is not needed here.
            compiler.compile(extern, input, options);
            // The compiler is responsible for generating the compiled code; it is not
            // accessible via the Result.
            compiledCode = compiler.toSource();
        } catch (RuntimeException ex) {
            LOG.log(Level.WARNING, ex.getLocalizedMessage());
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getClass() + ": " + ex.getLocalizedMessage(), ex);
        }
        return compiledCode;
    }

    @Override
    public void sendEvaluation(Website website, float evaluation, Long time) {
        // For this variable the reports are not needed
    }

    @Override
    public void sendFailedEvaluation(Website website, Long time) {
        // For this variable the reports are not needed
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
