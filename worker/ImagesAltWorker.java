package cu.redcuba.worker;

import cu.redcuba.factory.VariableFactory;
import cu.redcuba.factory.WebsiteImagesAltFactory;
import cu.redcuba.model.ImageItem;
import cu.redcuba.object.Website;
import cu.redcuba.output.ImagesAltOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ImagesAltWorker extends AbstractWorker<ImagesAltOutput> {

    private static final int CORRECT_LIMIT = 100;

    private static final int TO_IMPROVE_LIMIT = 90;

    private static final ImagesAltOutput NOT_EXIST_OUTPUT;

    static {
        // TODO: 2020-11-24 Consultar por qué se toma como 100% y Correcto cuando no tiene elementos.
        NOT_EXIST_OUTPUT = new ImagesAltOutput();
        NOT_EXIST_OUTPUT.setHas(false);
        NOT_EXIST_OUTPUT.setImageItems(new ArrayList<>());
        NOT_EXIST_OUTPUT.setPercent(100);
        NOT_EXIST_OUTPUT.setValue(VALUE_CORRECT);
    }

    private final WebsiteImagesAltFactory websiteImagesAltFactory;

    @Autowired
    public ImagesAltWorker(WebsiteImagesAltFactory websiteImagesAltFactory) {
        this.websiteImagesAltFactory = websiteImagesAltFactory;
    }

    @Override
    long getVariableId() {
        return VariableFactory.VAR_IMAGES_ALT;
    }

    @Override
    ImagesAltOutput analyse(Object... args) {
        if (args[0] == null) {
            return NOT_EXIST_OUTPUT;
        }

        @SuppressWarnings("unchecked") List<Object[]> imagesData = (List<Object[]>) args[0];

        if (imagesData.isEmpty()) {
            return NOT_EXIST_OUTPUT;
        }

        ImagesAltOutput output = new ImagesAltOutput();

        output.setHas(true);

        List<ImageItem> imageItems = new ArrayList<>();

        int validAltAmount = 0;
        for (Object[] imgData : imagesData) {
            String src = (String) imgData[0];
            String alt = (String) imgData[1];

            ImageItem imageItem = new ImageItem();
            imageItem.setSrc(src);
            imageItem.setAlt(alt);

            imageItems.add(imageItem);

            if (!alt.equals("")) {
                validAltAmount++;
            }
        }

        output.setImageItems(imageItems);

        output.setPercent(validAltAmount * 100f / imagesData.size());

        if (output.getPercent() >= CORRECT_LIMIT) {
            output.setValue(VALUE_CORRECT);
        } else if (output.getPercent() >= TO_IMPROVE_LIMIT) {
            output.setValue(VALUE_TO_IMPROVE);
        } else {
            output.setValue(VALUE_INCORRECT);
        }

        return output;
    }

    @Override
    void fullWork(Website website, Object... args) {
        ImagesAltOutput output = analyse(args);

        // Guardar los diferentes indicadores.
        save(website, getIndicatorSlugWithPrefix(), output.getValue());
        save(website, getIndicatorSlugWithPrefix("percent"), output.getPercent());
        save(website, getIndicatorSlugWithPrefix("has"), output.has());

        // Reportar la evaluación al Monitor.
        sendEvaluation(website, output);

        // Almacenar datos adicionales.
        websiteImagesAltFactory.createAndSave(website.getId(), output.getImageItems());
    }

    @Override
    boolean appliesForEvaluation(Object... args) {
        return true;
    }
}
