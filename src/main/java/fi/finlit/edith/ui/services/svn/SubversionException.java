package fi.finlit.edith.ui.services.svn;

public class SubversionException extends RuntimeException {
    private static final long serialVersionUID = -3764374237811191541L;

    public SubversionException(String msg) {
        super(msg);
    }

    public SubversionException(Throwable t) {
        super(t);
    }

    public SubversionException(String msg, Throwable t) {
        super(msg, t);
    }
}
