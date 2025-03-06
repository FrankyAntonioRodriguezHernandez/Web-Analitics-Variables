package cu.redcuba.worker;

import cu.redcuba.factory.VariableFactory;
import cu.redcuba.helper.CastHelper;
import cu.redcuba.object.Website;
import cu.redcuba.output.ResourceLoadOutput;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@Service
public class ResourceLoadWorker extends AbstractWorker<ResourceLoadOutput> {

    @Override
    public long getVariableId() {
        return VariableFactory.VAR_RESOURCE_LOAD;
    }

    /**
     * Analyses the values of a particular indicator.
     * Removed param imageGoogleAnalytic for google-analytic-image indicator.
     *
     * @param args Arguments needed by the worker. In this case is expected:
     *             <ul>
     *             <li>script</li>
     *             </ul>
     * @return The analysis result.
     */
    @Override
    public ResourceLoadOutput analyse(Object... args)  {

        String content = CastHelper.cast(args[0], String.class) ;
        ResourceLoadOutput output = new ResourceLoadOutput();
        Document doc = Jsoup.parse(content); // Realizar solicitud http y obtener el doc html
        
        int countBadResourceLoad = 0;

		Elements scripts = doc.select("script"); //obtener todos los elementos <script> del doc si tiene

		List<String> src = scripts.eachAttr("src");
		int countExtResourceLoad = src.size();
		
		// Iterar sobre los elementos <script> y extraer los "src"
		for (Element script : scripts) {
		    String extr = script.attr("src");
		    if(extr.split(":")[0].toLowerCase().equals("http")) {
		    	countBadResourceLoad++;
		    }
		}
		
		//System.out.println("Cantidad de etiquetas <script> externas encontrados: " + countExtResourceLoad);
		//System.out.println("Cantidad de etiquetas <script> externas no seguras encontrados: " + countBadResourceLoad);
		
		output.setCountExtResourceLoad(countExtResourceLoad);
		output.setNotHasBadResourceLoad(countBadResourceLoad == 0);
        

        // Actualmente
        // [Bien] si  no tiene etiquetas <script> no seguras o no contiene contiene elementos externos
        // [Mal] si tiene etiquetas <script> no seguras
        if (output.notHasBadResourceLoad() == true || output.getCountExtResourceLoad() == 0) {
            output.setValue(VALUE_CORRECT);
        } else if(output.getCountExtResourceLoad() != 0 && output.notHasBadResourceLoad() == false){
            output.setValue(VALUE_INCORRECT);
        }
        return output;
    }
   //Prueba del comit

    @Override
    void fullWork(Website website, Object... args)   {
        ResourceLoadOutput output = analyse(args);

        // Guardar los diferentes indicadores.
        save(website, getIndicatorSlugWithPrefix(), output.getValue()); // Evaluacion general.
        save(website, getIndicatorSlugWithPrefix("countExtResourceLoad"), output.getCountExtResourceLoad()); // Cantidad de recursos externos cargados.
        save(website, getIndicatorSlugWithPrefix("notHasBadResourceLoad"), output.notHasBadResourceLoad()); // No tiene recursos externos inseguros.
        sendEvaluation(website, output);

    }

    @Override
    boolean appliesForEvaluation(Object... args) {
        return true;
    }
}
