package fi.finlit.edith.ui.services;

/**
 * TextUtils provides
 *
 * @author tiwe
 * @version $Id$
 */
public class TextUtils {
    
    public static int getStartIndex(String string, String text) {
        if (string.contains(text)){
            int firstIndex = string.indexOf(text);
            int lastIndex = string.lastIndexOf(text);
            if (lastIndex == firstIndex){
                return firstIndex;
            }else{
                // TODO : log error
                return lastIndex;
            }
            
        }else {
            // TODO : optimize
            for (int i = 0; i < string.length(); i++){
                if (text.charAt(0) == string.charAt(i) && text.startsWith(string.substring(i))){
                    return i;
                }
            }
        }
        return -1;         
    }
    
    public static int getEndIndex(String string, String text){
        if (string.contains(text)){
            int firstIndex = string.indexOf(text);
            int lastIndex = string.lastIndexOf(text);
            if (lastIndex == firstIndex){
                return firstIndex + text.length();
            }else{
                // TODO : log error
                return lastIndex + text.length();
            }
            
        }else{
            for (int i = 0; i < text.length(); i++){
                if (text.charAt(i) == string.charAt(0) && string.startsWith(text.substring(i))){
                    return text.length() - i;
                }
            }
        }
        return -1;        
    }

}
