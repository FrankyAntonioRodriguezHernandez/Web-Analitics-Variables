package cu.redcuba.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.TreeSet;

@Service
public class LocalesHelper {

    private static final Logger LOG = LoggerFactory.getLogger(LocalesHelper.class);

    private static final String LOCALES_FILE_NAME = "locales";

    private TreeSet<String> locales;

    @Autowired
    public LocalesHelper(@Value("${evw.var.locales.path}") String localesFolderPath) {
        locales = new TreeSet<>();

        String localesPath = String.format("%s/%s", localesFolderPath, LOCALES_FILE_NAME);

        try (BufferedReader bufferedReader = new BufferedReader(
                new FileReader(localesPath))) {

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                //Skipping comment lines
                if (!line.startsWith("#")) {
                    locales.add(line);
                }
            }
        } catch (FileNotFoundException e) {
            LOG.error("The locales file wasn't found on: %s. Error: %s", localesPath, e.getMessage());
        } catch (IOException e) {
            LOG.error("There was an error trying for reading the file of locales. The error is %s", e.getMessage());
        }
    }

    /**
     * Checks if a given locale is a valid locale.
     *
     * @param locale Locale to check.
     * @return If a given locale is valid or not.
     */
    public boolean isLocale(String locale) {
        return locales.contains(locale);
    }

}
