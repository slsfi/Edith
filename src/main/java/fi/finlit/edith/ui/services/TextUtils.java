package fi.finlit.edith.ui.services;

/**
 * TextUtils provides
 *
 * @author tiwe
 * @version $Id$
 */
public class TextUtils {
    
    // TODO : change into service (interface + impl) -> TextMatchingService/Impl
    //        make sure all TextUtilsTests succeed
    //         
    
    public static int getStartIndex(String xmlCharacters, String noteLongText) {
        if (xmlCharacters.contains(noteLongText)){
            int firstIndex = xmlCharacters.indexOf(noteLongText);
            int lastIndex = xmlCharacters.lastIndexOf(noteLongText);
            if (lastIndex == firstIndex){
                return firstIndex;
            }else{
                // TODO : log error
                return lastIndex;
            }
            
        }else {
            // TODO : improve
            for (int i = 0; i < xmlCharacters.length(); i++){
                if (noteLongText.charAt(0) == xmlCharacters.charAt(i) && noteLongText.startsWith(xmlCharacters.substring(i))){
                    return i;
                }
            }
        }
        return -1;         
    }
    
    public static int getEndIndex(String xmlCharacters, String noteLongText){
        if (xmlCharacters.contains(noteLongText)){
            int firstIndex = xmlCharacters.indexOf(noteLongText);
            int lastIndex = xmlCharacters.lastIndexOf(noteLongText);
            if (lastIndex == firstIndex){
                return firstIndex + noteLongText.length();
            }else{
                // TODO : log error
                return lastIndex + noteLongText.length();
            }
            
        }else{
            // TODO : improve
            for (int i = 0; i < noteLongText.length(); i++){
                if (noteLongText.charAt(i) == xmlCharacters.charAt(0) && xmlCharacters.startsWith(noteLongText.substring(i))){
                    return noteLongText.length() - i;
                }
            }
        }
        return -1;        
    }

}
