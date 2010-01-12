package fi.finlit.edith.ui.mixins;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.RenderSupport;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.IncludeJavaScriptLibrary;
import org.apache.tapestry5.annotations.InjectContainer;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.corelib.components.Hidden;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ComponentDefaultProvider;


@IncludeJavaScriptLibrary({
        "classpath:jquery-1.3.2.js","InlineEditEvent.js",})
@SuppressWarnings("unused")
        public class InlineMixin {
    
    @InjectContainer
    private Hidden field;
    
    //Change to BindParameter when updated to Tapestry 5.2
    //then the name can be value and it should reflect the Hidden fields value property
    @Parameter
    private Object value1;
    
    @Inject
    private RenderSupport renderSupport;

    @Inject
    private ComponentDefaultProvider defaultProvider;
    
    @Inject
    private ComponentResources resources;
    
    @SuppressWarnings("unchecked")
    public void afterRender(MarkupWriter writer) {   
        ValueEncoder<Object> encoder = defaultProvider.defaultValueEncoder("value1", resources);
        
        String hiddenId = field.getControlName();
        String editId = "inlineField_" + hiddenId; 
        
        writer.element("div",
                "id", editId,
                "contentEditable","true",
                "class", "editable");
        writer.write(encoder.toClient(value1));
        writer.end();
       
    }
       
    
}
