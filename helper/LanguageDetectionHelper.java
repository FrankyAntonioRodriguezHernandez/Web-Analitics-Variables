package cu.redcuba.helper;

import com.google.common.base.Optional;
import com.optimaize.langdetect.LanguageDetector;
import com.optimaize.langdetect.LanguageDetectorBuilder;
import com.optimaize.langdetect.i18n.LdLocale;
import com.optimaize.langdetect.ngram.NgramExtractors;
import com.optimaize.langdetect.profiles.LanguageProfile;
import com.optimaize.langdetect.profiles.LanguageProfileReader;
import com.optimaize.langdetect.text.CommonTextObjectFactories;
import com.optimaize.langdetect.text.TextObject;
import com.optimaize.langdetect.text.TextObjectFactory;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.List;

public class LanguageDetectionHelper {

    public static String getLanguage(String html) throws IOException {
        String text = Jsoup.parse(html).text();

        List<LanguageProfile> languageProfiles = new LanguageProfileReader().readAllBuiltIn();

        LanguageDetector languageDetector = LanguageDetectorBuilder.create(NgramExtractors.standard())
                .withProfiles(languageProfiles)
                .build();

        TextObjectFactory textObjectFactory = CommonTextObjectFactories.forDetectingOnLargeText();

        TextObject textObject = textObjectFactory.forText(text);
        Optional<LdLocale> optionalLocale = languageDetector.detect(textObject);

        String language = "";
        if (optionalLocale.isPresent()) {
            LdLocale locale = optionalLocale.get();
            language = locale.getLanguage();
        }

        return language;
    }

}
