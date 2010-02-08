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
    
    private int startIndex, endIndex;
    
    private String selection;
    
    private String startId, endId;
    
    public SelectedText(){}
    
    public SelectedText(String startId, String endId, int startIndex, int endIndex, String selection) {
        this.startId = startId;
        this.endId = endId;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.selection = selection;
    }
    
    public SelectedText(String startId, String endId, String selection) {
        this(startId, endId, 1, 1, selection);
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

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }
    
    
    
    
}