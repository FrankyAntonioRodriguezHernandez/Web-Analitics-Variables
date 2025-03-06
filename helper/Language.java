package cu.redcuba.helper;

public enum Language {

    ES,
    EN;

    public static Language valueFrom(String name) {
        if (name == null) {
            return ES;
        }

        switch (name.toUpperCase()) {
            case "ES":
                return ES;
            case "EN":
                return EN;
            default:
                return ES;
        }
    }

}
