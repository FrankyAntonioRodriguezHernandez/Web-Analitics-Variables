package cu.redcuba.worker;

import cu.redcuba.factory.VariableFactory;
import cu.redcuba.factory.WebsiteLinkFactory;
import cu.redcuba.helper.CastHelper;
import cu.redcuba.object.Website;
import cu.redcuba.output.BrokenLinkOutput;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
public class BrokenLinkWorker extends AbstractWorker<BrokenLinkOutput> {
      private static final BrokenLinkOutput NOT_EXIST_OUTPUT;
      private final WebsiteLinkFactory websiteLinkFactory;
    static {
        NOT_EXIST_OUTPUT = new BrokenLinkOutput();
        NOT_EXIST_OUTPUT.setNoBrokenLink(true);
        NOT_EXIST_OUTPUT.setPercent(100);
        NOT_EXIST_OUTPUT.setValue(VALUE_CORRECT);
    }


    public BrokenLinkWorker(WebsiteLinkFactory websiteLinkFactory) {
        this.websiteLinkFactory = websiteLinkFactory;
    }

    @Override
    public long getVariableId() {
        return VariableFactory.VAR_BROKEN_LINK;
    }

    @Override
    public BrokenLinkOutput analyse(Object... args) {
        String content = CastHelper.cast(args[0], String.class);
        BrokenLinkOutput output = new BrokenLinkOutput();

        List<String> brokenLinksList = new ArrayList<>();
        int brokenLinkCount = 0;

        Document doc = Jsoup.parse(content); // Realizar solicitud HTTP y obtener el documento HTML
        Elements links = doc.select("a[href]"); // Obtener todos los elementos <a> del documento

        // Iterar sobre los elementos <a> y extraer los enlaces
        for (Element link : links) {
            String href = link.absUrl("href"); // Obtener URL absoluta

            if (href.isEmpty()) continue; // Saltar enlaces vacíos

            try {
                new URL(href); // Validar la URL
            } catch (MalformedURLException e) {
                continue; // Saltar la URL inválida
            }

            try {
                URL urlObj = new URL(href);
                // Comprobar el protocolo de la URL antes de intentar abrir una conexión HTTP
                if ("http".equalsIgnoreCase(urlObj.getProtocol()) || "https".equalsIgnoreCase(urlObj.getProtocol())) {
                    HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();
                    con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
                    con.setInstanceFollowRedirects(true);
                    con.setConnectTimeout(3000); // Tiempo de espera de conexión en milisegundos
                    con.connect();

                    int responseCode = con.getResponseCode();
                    if (con.getResponseCode() >= 400) { // Códigos de estado 400 y superiores indican errores o enlaces rotos
                        brokenLinkCount++; // Incrementa el contador de enlaces rotos
                        brokenLinksList.add(href);
                      //  System.out.println("🔴 Enlace roto detectado: " + href + " (Código " + responseCode + ")");
                    }
                }
            } catch (IOException e) {
                brokenLinksList.add(href); // Si hay error de conexión, se asume que el enlace está roto
            }
        }

        output.setBrokenLinks(brokenLinksList);
        output.setPercent(links.isEmpty() ? 0 : (float) brokenLinkCount * 100 / links.size());
        output.setNoBrokenLink(brokenLinkCount == 0);

        if (output.getPercent() < 10) {
            output.setValue(VALUE_CORRECT);
        } else if (output.getPercent() < 50) {
            output.setValue(VALUE_TO_IMPROVE);
        } else {
            output.setValue(VALUE_INCORRECT);
        }
        return output;
    }

    @Override
    void fullWork(Website website, Object... args) {
        BrokenLinkOutput output = analyse(args);

        // Imprimir enlaces rotos antes de guardarlos
       // System.out.println("Enlaces rotos detectados: " + output.getBrokenLinks());

        // Guardar los diferentes indicadores.
        save(website, getIndicatorSlugWithPrefix(), output.getValue());
        save(website, getIndicatorSlugWithPrefix("noBrokenLink"), output.hasNoBrokenLink());
        save(website, getIndicatorSlugWithPrefix("percentage"), output.getPercent());
        sendEvaluation(website, output);

        // Reporting the evaluation to the monitor
        sendEvaluation(website, output);
        websiteLinkFactory.createAndSave(website.getId(), output.getBrokenLinks());
    }

    @Override
    boolean appliesForEvaluation(Object... args) {
        return true;
    }
}

