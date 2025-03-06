package cu.redcuba.worker;

import cu.redcuba.factory.VariableFactory;
import cu.redcuba.helper.CastHelper;
import cu.redcuba.object.Website;
import cu.redcuba.output.CookiesSecurityOutput;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Set;

@Service
public class CookiesSecurityWorker extends AbstractWorker<CookiesSecurityOutput> {

    @Override
    public long getVariableId() {
        return VariableFactory.VAR_SEGURIDAD_COOKIES;
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
    public CookiesSecurityOutput analyse(Object... args)  {

        CookiesSecurityOutput output = new CookiesSecurityOutput();

        String URL = CastHelper.cast(args[0], String.class) ;

        try {

            URL url = new URL(URL);

            HttpURLConnection.setFollowRedirects(true);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            http.setConnectTimeout(2000);
            http.setReadTimeout(2000);
            http.setInstanceFollowRedirects(true);
            http.connect();

            Set<String> KeyHeaders = http.getHeaderFields().keySet();
            int contador = 0;
            int posicion = 0;
            for (String i : KeyHeaders) {
                if (i != null && i.toLowerCase().equals("set-cookie")) {
                    posicion = contador;
                    output.setHasHeaderSetCookies(true);
                }
                contador++;
            }
            if (output.hasHeaderSetCookies()) {
                String[] ValuesSetCookiesHeader = http.getHeaderFields().values().toArray()[posicion].toString().split(";");
                for (String i : ValuesSetCookiesHeader) {
                    if (i.toLowerCase().contains("httponly")) {
                        output.setHasParamHttpOnly(true);
                    }
                    if (i.toLowerCase().contains("secure")) {
                        output.setHasParamSecure(true);
                    }
                }
            }

            String cookiesHeader = http.getHeaderField("set-cookie");
            if (cookiesHeader != null){
                String[] split = cookiesHeader.split(";");
                int pos = -1;
                for (int i = 0; i < split.length; i++) {
                    if (split[i].toLowerCase().contains("max-age")) {
                        pos = i;
                    }
                }

                if (pos != -1) {
                    output.setHasParamMaxAge(true);
                    int maxAge = Integer.parseInt(split[pos].split("=")[1]);
                    if (maxAge < 3600) {
                        output.setHasCorrectMaxAge(true);
                    }
                }
            }else{
                output.setHasHeaderSetCookies(false);
            }

        }catch(IOException e) {

            e.printStackTrace();

        }

        //System.out.println("Tiene la cabecera Set-Cookies: " + output.hasHeaderSetCookies());
        //System.out.println("Tiene el parametro 'httpOnly': " + output.hasParamHttpOnly());
        //System.out.println("Tiene el parametro 'secure': " + output.hasParamSecure());

        // Actualmente
        //[Bien] si  tiene la cabecera Set-Cookies y los parametros 'httpOnly', 'secure', 'max-age' y este ultimo tiene un valor correcto
        //[A mejorar] si tiene la cabecera Set-Cookies pero no los parametros 'httpOnly', 'secure', 'max-age' o el valor de este ultimo es incorrecto
        //[Mal] Si no tiene la cabecera Set-Cookies
        if (output.hasHeaderSetCookies() == true && output.hasParamHttpOnly() == true && output.hasParamSecure() == true
                && output.hasParamMaxAge() == true && output.hasCorrectMaxAge() == true) {
            output.setValue(VALUE_CORRECT);
        } else if ((output.hasHeaderSetCookies() == true && output.hasParamHttpOnly() == false)
                || (output.hasHeaderSetCookies() == true && output.hasParamSecure() == false)
                || (output.hasHeaderSetCookies() == true && output.hasParamMaxAge() == false)
                || (output.hasHeaderSetCookies() == true && output.hasParamMaxAge() == true && output.hasCorrectMaxAge() == false)) {
            output.setValue(VALUE_TO_IMPROVE);
        } else if (output.hasHeaderSetCookies() == false) {
            output.setValue(VALUE_INCORRECT);
        }

        return output;
    }

    @Override
    void fullWork(Website website, Object... args)   {
        CookiesSecurityOutput output = analyse(args);

        // Guardar los diferentes indicadores.
        save(website, getIndicatorSlugWithPrefix(), output.getValue()); // Evaluacion general.
        save(website, getIndicatorSlugWithPrefix("hasHeaderSetCookies"), output.hasHeaderSetCookies()); // Tiene la cabecera Set-Cookies.
        save(website, getIndicatorSlugWithPrefix("hasParamHttpOnly"), output.hasParamHttpOnly()); // Tiene el parámetro HttpOnly.
        save(website, getIndicatorSlugWithPrefix("hasParamSecure"), output.hasParamSecure()); // Tiene el parámetro Secure.
        save(website, getIndicatorSlugWithPrefix("hasParamMaxAge"), output.hasParamMaxAge()); // Tiene el parametro 'max-age'.
        save(website, getIndicatorSlugWithPrefix("hasCorrectMaxAge"), output.hasCorrectMaxAge()); // Tiene correcto el parametro 'max-age'.
        sendEvaluation(website, output);

    }

    @Override
    boolean appliesForEvaluation(Object... args) {
        return true;
    }
}

