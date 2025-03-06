package cu.redcuba.worker;

import cu.redcuba.factory.VariableFactory;
import cu.redcuba.helper.CastHelper;
import cu.redcuba.object.Website;
import cu.redcuba.output.ReferrerPolicyOutput;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class ReferrerPolicyWorker extends AbstractWorker<ReferrerPolicyOutput> {

    @Override
    public long getVariableId() {
        return VariableFactory.VAR_REFERRER_POLICY;
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
    public ReferrerPolicyOutput analyse(Object... args)  {

        ReferrerPolicyOutput output = new ReferrerPolicyOutput();

        String URL = CastHelper.cast(args[0], String.class) ;

        try {

            URL url = new URL(URL);

            HttpURLConnection.setFollowRedirects(true);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            http.setConnectTimeout(2000);
            http.setReadTimeout(2000);
            http.setInstanceFollowRedirects(true);
            http.connect();

            String rp = http.getHeaderField("Referrer-Policy");

            if (rp != null) {
                output.setHasHeaderRP(true);;
                if (rp.contains("same-origin")) {
                    output.setHasParamSameOrigin(true);
                }
                if (rp.contains("no-referrer")) {
                    output.setHasParamNoReferrer(true);
                }
                if (rp.contains("strict-origin")) {
                    output.setHasParamNoReferrer(true);
                }
                if (rp.contains("no-referrer-when-downgrade")) {
                    output.setHasParamNoRWD(true);
                }
            }

        }catch(IOException e) {

            e.printStackTrace();

        }

        //System.out.println("Tiene la cabecera Referrer-Policy: " + output.hasHeaderRP());
        //System.out.println("Tiene el parametro 'same-origin': " + output.hasParamSameOrigin());
        //System.out.println("Tiene el parametro 'no-referrer': " + output.hasParamNoReferrer());

        // Actualmente
        //[Bien] si  tiene la cabecera RP y alguno de los parametros 'no-referrer', 'same-origin','strict-origin' o  'no-referrer-when-downgrade'
        //[A mejorar] si tiene la cabecera RP pero no tiene ninguno de los parametros 'no-referrer', 'same-origin','strict-origin' o  'no-referrer-when-downgrade'
        //[Mal] Si no tiene la cabecera RP
        if (output.hasHeaderRP() == true && output.hasParamSameOrigin() == true || output.hasParamNoReferrer() == true
        		|| output.hasParamNoRWD() == true || output.hasParamStrictOrigin() == true) {
            output.setValue(VALUE_CORRECT);
        } else if (output.hasHeaderRP() == true && output.hasParamSameOrigin() == false && output.hasParamNoReferrer() == false
        		&& output.hasParamNoRWD() == false && output.hasParamStrictOrigin() == true) {
            output.setValue(VALUE_TO_IMPROVE);
        } else {
            output.setValue(VALUE_INCORRECT);
        }

        return output;
    }

    @Override
    void fullWork(Website website, Object... args)   {
        ReferrerPolicyOutput output = analyse(args);

        // Guardar los diferentes indicadores.
        save(website, getIndicatorSlugWithPrefix(), output.getValue()); // Evaluacion general.
        save(website, getIndicatorSlugWithPrefix("hasHeaderRP"), output.hasHeaderRP()); // Tiene la cabecera Referrer-Policy.
        save(website, getIndicatorSlugWithPrefix("hasParamSameOrigin"), output.hasParamSameOrigin()); // Tiene el parametro 'same-origin'.
        save(website, getIndicatorSlugWithPrefix("hasParamNoReferrer"), output.hasParamNoReferrer()); // Tiene el parametro 'no-referrer'.
        save(website, getIndicatorSlugWithPrefix("hasParamStrictOrigin"), output.hasParamStrictOrigin()); // Tiene el parametro 'strict-origin'.
        save(website, getIndicatorSlugWithPrefix("hasParamNoRWD"), output.hasParamNoRWD()); // Tiene el parametro 'no-referrer-when-downgrade'.
        sendEvaluation(website, output);

    }

    @Override
    boolean appliesForEvaluation(Object... args) {
        return true;
    }
}
