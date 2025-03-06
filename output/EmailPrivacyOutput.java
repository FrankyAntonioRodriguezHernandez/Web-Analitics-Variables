package cu.redcuba.output;

import java.util.List;

public class EmailPrivacyOutput extends Output {
    private List<String> emailItems;

    private boolean notHas;

    public List<String> getEmailItems() {
        return emailItems;
    }

    public void setEmailItems(List<String> emailItems) {
        this.emailItems = emailItems;
    }

    public boolean isNotHas() {
        return notHas;
    }

    public void setNotHas(boolean has) {
        this.notHas = has;
    }
}

