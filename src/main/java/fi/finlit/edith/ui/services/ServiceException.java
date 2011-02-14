package fi.finlit.edith.ui.services;

public class ServiceException extends RuntimeException{

    private static final long serialVersionUID = -5426150520106835552L;

    ServiceException(String msg){
        super(msg);
    }

    ServiceException(Throwable t) {
        super(t);
    }

    ServiceException(String msg, Throwable t) {
        super(msg,t);
    }

}
