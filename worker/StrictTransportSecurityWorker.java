package cu.redcuba.worker;

import cu.redcuba.factory.VariableFactory;
import cu.redcuba.helper.CastHelper;
import cu.redcuba.object.Website;
import cu.redcuba.output.StrictTransportSecurityOutput;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class StrictTransportSecurityWorker extends AbstractWorker<StrictTransportSecurityOutput> {

    @Override
    public long getVariableId() {
        return VariableFactory.VAR_STRICT_TRANSPORT_SECURITY;
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
    public StrictTransportSecurityOutput analyse(Object... args)  {

        StrictTransportSecurityOutput output = new StrictTransportSecurityOutput();

        String URL = CastHelper.cast(args[0], String.class) ;

        try {

            URL url = new URL(URL);

            HttpURLConnection.setFollowRedirects(true);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            http.setConnectTimeout(2000);
            http.setReadTimeout(2000);
            http.setInstanceFollowRedirects(true);
            http.connect();

            String hsts = http.getHeaderField("Strict-Transport-Security");

            if (hsts != null){
                String[] split = hsts.split(";");
                int pos = -1;
                for(int i=0 ; i<split.length ; i++) {
                    if(split[i].contains("max-age")) {
                        pos = i;
                    }
                }

                if(pos != -1) {
                    output.setHasParamMaxAge(true);
                    int maxAge = Integer.parseInt(split[pos].split("=")[1]);
                    if(maxAge > 15768000) {
                        output.setHasCorrectMaxAge(true);
                    }
                }
                output.setHasHeaderHSTS(true);;
                if (hsts.contains("preload")) {
                    output.setHasParamPreload(true);
                }
            }else{
                output.setHasHeaderHSTS(false);
            }

        }catch(IOException e) {

            e.printStackTrace();

        }

        //System.out.println("Tiene la cabecera Strict-Transport-Security: " + output.hasHeaderHSTS());
        //System.out.println("Tiene el parametro 'preload': " + output.hasParamPreload());

        // Actualmente
        //[Bien] si  tiene la cabecera HSTS, el parametro 'preload' y el parametro 'max-age' ademas de ser correcto
        //[A mejorar] si tiene la cabecera HSTS pero no el parametro 'preload' ni el parametro 'max-age' o estar incorrecto
        //[Mal] Si no tiene la cabecera HSTS
        if (output.hasHeaderHSTS() == true && output.hasParamPreload() == true &&  output.hasParamMaxAge() == true 
        		&& output.hasCorrectMaxAge() == true) {
            output.setValue(VALUE_CORRECT);
        } else if ((output.hasHeaderHSTS() == true && output.hasParamPreload() == false) 
        		|| (output.hasHeaderHSTS() == true && output.hasParamMaxAge() == false)
        		|| (output.hasHeaderHSTS() == true && output.hasParamMaxAge() == true && output.hasCorrectMaxAge() == false)) {
            output.setValue(VALUE_TO_IMPROVE);
        } else {
            output.setValue(VALUE_INCORRECT);
        }

        return output;
    }

    @Override
    void fullWork(Website website, Object... args)   {
        StrictTransportSecurityOutput output = analyse(args);

        // Guardar los diferentes indicadores.
        save(website, getIndicatorSlugWithPrefix(), output.getValue()); // Evaluacion g eneral.
        save(website, getIndicatorSlugWithPrefix("hasHeaderHSTS"), output.hasHeaderHSTS()); // Tiene la cabecera HSTS.
        save(website, getIndicatorSlugWithPrefix("hasParamPreload"), output.hasParamPreload()); // Tiene el parametro Preloaad.
        save(website, getIndicatorSlugWithPrefix("hasParamMaxAge"), output.hasParamMaxAge()); // Tiene el parametro 'max-age'.
        save(website, getIndicatorSlugWithPrefix("hasCorrectMaxAge"), output.hasCorrectMaxAge()); // Tiene correcto el parametro 'max-age'.
        sendEvaluation(website, output);

    }

    @Override
    boolean appliesForEvaluation(Object... args) {
        return true;
    }
}
