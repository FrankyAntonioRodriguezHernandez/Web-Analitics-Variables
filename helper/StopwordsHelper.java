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
import java.util.EnumMap;
import java.util.TreeSet;

@Service
public class StopwordsHelper {

    private static final Logger LOG = LoggerFactory.getLogger(StopwordsHelper.class);

    private static final String STOPWORD_FILE_NAME = "stopwords";

    private EnumMap<Language, TreeSet<String>> languageStopwords;

    @Autowired
    public StopwordsHelper(@Value("${evw.var.stopwords.path}") String stopwordPath) {
        languageStopwords = new EnumMap<>(Language.class);
        
        for (Language language : Language.values()) {
            String stopwordsPath = String.format("%s/%s_%s", stopwordPath, STOPWORD_FILE_NAME, language);
            
            TreeSet<String> stopwords = new TreeSet<>();

            try (BufferedReader bufferedReader = new BufferedReader(
                    new FileReader(stopwordsPath))) {

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    //Skipping comment lines
                    if (!line.startsWith("#")) {
                        stopwords.add(line);
                    }
                }
            } catch (FileNotFoundException e) {
                LOG.error("The stopwords file wasn't found on: %s. Error: %s", stopwordsPath, e.getMessage());
            } catch (IOException e) {
                LOG.error("There was an error trying for reading the file stopwords. The error is %s", e.getMessage());
            }
            
            languageStopwords.put(language, stopwords);
        }
    }
    
    public boolean isLanguageSupported(Language language) {
        return languageStopwords.get(language) != null;
    }

    /**
     * Checks if a given word is a stopword or not.
     *
     * @param lexeme Word to check.
     * @return If a given word is a stopword or not.
     */
    public boolean isStopword(Language language, String lexeme) {
        return languageStopwords.get(language).contains(lexeme);
    }

}
