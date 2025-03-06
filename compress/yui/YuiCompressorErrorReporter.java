/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cu.redcuba.compress.yui;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;

/**
 *
 * @author developer
 */
public class YuiCompressorErrorReporter implements ErrorReporter {

    private static final Logger logger = Logger.getLogger(YuiCompressorErrorReporter.class.getName());

    public void warning(String message, String sourceName, int line, String lineSource, int lineOffset) {
        if (line < 0) {
            logger.log(Level.WARNING, message);
        } else {
            logger.log(Level.WARNING, line + ':' + lineOffset + ':' + message);
        }
    }

    public void error(String message, String sourceName, int line, String lineSource, int lineOffset) {
        if (line < 0) {
            logger.log(Level.SEVERE, message);
        } else {
            logger.log(Level.SEVERE, line + ':' + lineOffset + ':' + message);
        }
    }

    public EvaluatorException runtimeError(String message, String sourceName, int line, String lineSource, int lineOffset) {
        error(message, sourceName, line, lineSource, lineOffset);
        return new EvaluatorException(message);
    }

}
