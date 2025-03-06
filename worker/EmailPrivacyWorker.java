package cu.redcuba.worker;

import cu.redcuba.factory.VariableFactory;
import cu.redcuba.factory.WebsiteEmailsFactory;
import cu.redcuba.object.Website;
import cu.redcuba.output.EmailPrivacyOutput;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailPrivacyWorker extends AbstractWorker<EmailPrivacyOutput> {

    private static final int CORRECT_LIMIT = 0;

    private final WebsiteEmailsFactory websiteEmailsFactory;

    public EmailPrivacyWorker(WebsiteEmailsFactory websiteEmailsFactory) {
        this.websiteEmailsFactory = websiteEmailsFactory;
    }

    @Override
    long getVariableId() {
        return VariableFactory.VAR_EMAIL_PRIVACY;
    }

    @Override
    EmailPrivacyOutput analyse(Object... args) {
        @SuppressWarnings("unchecked") List<String> emails = (List<String>) args[0];

        EmailPrivacyOutput output = new EmailPrivacyOutput();

        output.setEmailItems(emails);

        boolean noHas = !(emails.size() > 0);
        output.setNotHas(noHas);

        if (emails.size() == CORRECT_LIMIT) {
            output.setValue(VALUE_CORRECT);
        } else {
            output.setValue(VALUE_INCORRECT);
        }

        return output;
    }

    public void fullWork(Website website, Object... args) {
        EmailPrivacyOutput output = analyse(args);

        save(website, getIndicatorSlugWithPrefix(), output.getValue());
        save(website, getIndicatorSlugWithPrefix("no-mail-public-exist"), output.isNotHas());

        // Reporting the evaluation to the monitor
        sendEvaluation(website, output);
        websiteEmailsFactory.createAndSave(website.getId(), output.getEmailItems());
    }

    @Override
    boolean appliesForEvaluation(Object... args) {
        return false;
    }
}

