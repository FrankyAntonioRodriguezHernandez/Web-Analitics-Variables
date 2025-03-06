package cu.redcuba.worker;

import cu.redcuba.factory.VariableFactory;
import cu.redcuba.helper.CastHelper;
import cu.redcuba.object.Website;
import cu.redcuba.output.SubresourceIntegrityOutput;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubresourceIntegrityWorker extends AbstractWorker<SubresourceIntegrityOutput> {

    @Override
    public long getVariableId() {
        return VariableFactory.VAR_SUBRESOURCE_INTEGRITY;
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
    public SubresourceIntegrityOutput analyse(Object... args)  {

        String content = CastHelper.cast(args[0], String.class) ;
        SubresourceIntegrityOutput output = new SubresourceIntegrityOutput();

        Document doc = Jsoup.parse(content); //realizar solicitud http y obtener el doc html

        Elements scripts = doc.select("script"); //obtener todos los elementos <script> del doc si tiene

        // Guardo en una lista el atributo "integrity" de todos los <script> que lo contengan
        List<String> integrity = scripts.eachAttr("integrity");

        // Guardo en una lista el atributo "src" de todos los <script> que lo contengan
        List<String> src = scripts.eachAttr("src");

        int cantExtScript = src.size(); // Cantidad de etiquetas <script> externas
        int cantNoInyegrityScript = src.size() - integrity.size(); // Cantidad de etiquetas <script> externas no integras

        //System.out.println("Cantidad de etiquetas <script> externas encontradas: " + cantExtScript);
        //System.out.println("Cantidad de etiquetas <script> externas no integras encontradas: " + cantNoInyegrityScript);

        output.setCountExtScript(cantExtScript);
        output.setNotHasIntegrityScript(cantNoInyegrityScript == 0);
        

        // Actualmente
        // [Bien] si  no tiene etiquetas <script> externas no integras
        // [Mal] si tiene etiquetas <script> externas no integras
        if (output.notHasIntegrityScript() == true || output.getCountExtScript() == 0) {
            output.setValue(VALUE_CORRECT);
        } else if (output.getCountExtScript() != 0 && output.notHasIntegrityScript() == false){
            output.setValue(VALUE_INCORRECT);
        }
        return output;
    }

    @Override
    void fullWork(Website website, Object... args)   {
        SubresourceIntegrityOutput output = analyse(args);

        // Guardar los diferentes indicadores.
        save(website, getIndicatorSlugWithPrefix(), output.getValue()); //Evaluacion general
        save(website, getIndicatorSlugWithPrefix("countExtScript"), output.getCountExtScript()); //Cantidad de <script> externo
        save(website, getIndicatorSlugWithPrefix("notHasIntegrityScript"), output.notHasIntegrityScript()); //No tiene <script> no integros
        
        sendEvaluation(website, output);

    }

    @Override
    boolean appliesForEvaluation(Object... args) {
        return true;
    }
}

