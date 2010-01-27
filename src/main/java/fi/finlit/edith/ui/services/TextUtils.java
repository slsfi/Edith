package fi.finlit.edith.ui.services;

/**
 * TextUtils provides
 *
 * @author tiwe
 * @version $Id$
 */
public class TextUtils {
    
    public static int getStartIndex(String data, String text) {
        if (data.contains(text)){
            int firstIndex = data.indexOf(text);
            int lastIndex = data.lastIndexOf(text);
            if (lastIndex == firstIndex){
                return firstIndex;
            }else{
                // TODO : log error
                return lastIndex;
            }
            
        }else {
            // TODO : optimize
            for (int i = 0; i < data.length(); i++){
                if (text.charAt(0) == data.charAt(i) && text.startsWith(data.substring(i))){
                    return i;
                }
            }
        }
        return -1;         
    }
    
    public static int getEndIndex(String data, String text){
        if (data.contains(text)){
            int firstIndex = data.indexOf(text);
            int lastIndex = data.lastIndexOf(text);
            if (lastIndex == firstIndex){
                return firstIndex + text.length();
            }else{
                // TODO : log error
                return lastIndex + text.length();
            }
            
        }else{
            for (int i = 0; i < text.length(); i++){
                if (text.charAt(i) == data.charAt(0) && data.startsWith(text.substring(i))){
                    return text.length() - i;
                }
            }
        }
        return -1;        
    }

}
