package fi.finlit.edith.ui.services.svn;

public class SubversionException extends RuntimeException {
    private static final long serialVersionUID = 2137588590021188211L;

    public SubversionException(Throwable t) {
        super(t);
    }

    public SubversionException(String msg, Throwable t) {
        super(msg, t);
    }
}
