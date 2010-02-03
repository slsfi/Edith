/**
 * 
 */
package fi.finlit.edith.domain;

/**
 * SelectedText provides
 *
 * @author tiwe
 * @version $Id$
 */
public class SelectedText {
    
    // TODO : add startIndex and endIndex
    
    private String selection;
    
    private String startId;
    
    private String endId;

    public SelectedText(){}
    
    public SelectedText(String startId, String endId, String selection) {
        this.startId = startId;
        this.endId = endId;
        this.selection = selection;
    }

    public String getSelection() {
        return selection;
    }

    public void setSelection(String selection) {
        this.selection = selection;
    }

    public String getStartId() {
        return startId;
    }

    public void setStartId(String startId) {
        this.startId = startId;
    }

    public String getEndId() {
        return endId;
    }

    public void setEndId(String endId) {
        this.endId = endId;
    }

    public boolean hasSelection() {
        return selection != null && startId != null && endId != null
                && selection.trim().length() > 0 && startId.trim().length() > 0
                && endId.trim().length() > 0;
    }
    
    @Override
    public String toString(){
        return startId + "," + endId + ":[" + selection + "]";
    }
}