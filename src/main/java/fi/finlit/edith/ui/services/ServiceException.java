package fi.finlit.edith.ui.services;

public class ServiceException extends RuntimeException{

    private static final long serialVersionUID = -5426150520106835552L;

    public ServiceException(String msg){
        super(msg);
    }

    public ServiceException(Throwable t) {
        super(t);
    }

    public ServiceException(String msg, Throwable t) {
        super(msg,t);
    }

}
