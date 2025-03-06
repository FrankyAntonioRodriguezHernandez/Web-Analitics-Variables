package cu.redcuba.worker;

import cu.redcuba.factory.VariableFactory;
import cu.redcuba.helper.CastHelper;
import cu.redcuba.object.Website;
import cu.redcuba.output.ContentSecurityPolicyOutput;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class ContentSecurityPolicyWorker extends AbstractWorker<ContentSecurityPolicyOutput> {

    @Override
    public long getVariableId() {
        return VariableFactory.VAR_CONTENT_SECURITY_POLICY;
    }

    /**
     * Analyses the values of a particular indicator.
     * Removed param imageGoogleAnalytic for google-analytic-image indicator.
     *
     * @param args Arguments needed by the worker. In this case is expected:
     *             <ul>
     *             <li>url</li>
     *             </ul>
     * @return The analysis result.
     */
    @Override
    public ContentSecurityPolicyOutput analyse(Object... args)  {

        ContentSecurityPolicyOutput output = new ContentSecurityPolicyOutput();

        String URL = CastHelper.cast(args[0], String.class) ;

        try {

            URL url = new URL(URL);

            HttpURLConnection.setFollowRedirects(true);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            http.setConnectTimeout(2000);
            http.setReadTimeout(2000);
            http.setInstanceFollowRedirects(true);
            http.connect();

            String csp = http.getHeaderField("Content-Security-Policy");

            if (csp != null) {
                output.setHasHeaderCSP(true);;
                if (csp.contains("unsafe") == false) {
                    output.setNotHasParamUnsafe(true);
                }
                if (!csp.contains("none")) {
                	output.setHasParamNone(false);
                }
            }

        }catch(IOException e) {

            e.printStackTrace();

        }

        //System.out.println("Tiene la cabecera Content-Security-Policy: " + output.hasHeaderCSP());
        //System.out.println("No tiene el parametro 'unsafe': " + output.notHasParamUnsafe());

        // Actualmente
        //[Bien] si  tiene la cabecera CSP y el parametro 'none' y ademas no tiene el parametro 'unsafe'
        //[A mejorar] si tiene la cabecera CSP y el parametro 'unsafe' o no tiene el parametro 'none'
        //[Mal] Si no tiene la cabecera CSP
        if (output.hasHeaderCSP() == true && output.notHasParamUnsafe() == true && output.hasParamNone() == true) {
            output.setValue(VALUE_CORRECT);
        } else if ((output.hasHeaderCSP() == true && output.notHasParamUnsafe() == false) 
        		|| (output.hasHeaderCSP() == true && output.hasParamNone() == false)) {
            output.setValue(VALUE_TO_IMPROVE);
        } else {
            output.setValue(VALUE_INCORRECT);
        }

        return output;
    }

    @Override
    void fullWork(Website website, Object... args)   {
        ContentSecurityPolicyOutput output = analyse(args);

        // Guardar los diferentes indicadores.
        save(website, getIndicatorSlugWithPrefix(), output.getValue()); // Evaluacion general.
        save(website, getIndicatorSlugWithPrefix("hasHeaderCSP"), output.hasHeaderCSP()); // Tiene la cabecera CSP.
        save(website, getIndicatorSlugWithPrefix("notHasParamUnsafe"), output.notHasParamUnsafe()); // No tiene el parametro unsafe-inline.
        save(website, getIndicatorSlugWithPrefix("hasParamNone"), output.hasParamNone()); // Tiene el parametro none.
        sendEvaluation(website, output);

    }

    @Override
    boolean appliesForEvaluation(Object... args) {
        return true;
    }
}


