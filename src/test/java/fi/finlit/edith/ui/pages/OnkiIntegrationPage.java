package fi.finlit.edith.ui.pages;

import java.util.Arrays;

import org.apache.tapestry5.annotations.IncludeJavaScriptLibrary;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

@IncludeJavaScriptLibrary({ "classpath:jquery-1.4.1.js", "classpath:TapestryExt.js", "OnkiIntegrationPage.js"})
public class OnkiIntegrationPage {

    @Inject
    private Request request;
    
    public void onSuccessFromForm(){
        String[] uris = request.getParameters("uri");
        if (uris != null){
            System.out.println(Arrays.asList(uris));    
        }        
    }
    
}
