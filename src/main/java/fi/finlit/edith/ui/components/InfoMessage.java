package fi.finlit.edith.ui.components;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

@SuppressWarnings("unused")
public class InfoMessage {

    @Inject
    @Property
    private Block infoMessageBlock;
    
    @Inject
    @Property
    private Messages messages;
    
    //private List<String> infoKeys;
    
    //private List<String> errorKeys;

    @Property
    private String info;
    
    @Property
    private String error;
    
    public Block getBlock() {
        return infoMessageBlock;
    }
//    
//    public List<String> getInfoKeys() {
//        if (infoKeys == null) {
//            infoKeys = new ArrayList<String>();
//        }
//        return infoKeys;
//    }
//    
//    public List<String> getErrorKeys() {
//        if (errorKeys == null) {
//            errorKeys = new ArrayList<String>();
//        }
//        return errorKeys;
//    }
    
//    public String getKey() {
//        System.out.println("get key" +key);
//        return "haloo";
//    }
//    
//    public void setKey(String key) {
//        System.out.println("set key" +key);
//        this.key = key;
//    }
    
    public void addInfoMsg(String key) {
        this.info = key;
        //getInfoKeys().add(key);
        //infoKeys.add(key);
        //System.out.println(infoKeys);
    }
    
    public String getInfoMsg() {
        return info != null ? messages.get(info) : "";
    }
    
    public String getErrorMsg() {
        return error != null ? messages.get(error) : "";
    }
    
//    public void setInfoKeys(List<String> infoKeys) {
//        this.infoKeys = infoKeys;
//    }
//    public List<String> getInfoKeys() {
//        System.out.println("on get "+ infoKeys);
//        return infoKeys;
//    }
    
    public void addErrorMsg(String key) {
        this.error = key;
        //getErrorKeys().add(key);
    }
    
    
    
}
