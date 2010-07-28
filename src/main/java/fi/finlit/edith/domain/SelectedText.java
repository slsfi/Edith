/**
 *
 */
package fi.finlit.edith.domain;

import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * SelectedText provides
 *
 * @author tiwe
 * @version $Id$
 */
public class SelectedText {
    
    private String selection;

    private String startId, endId;

    private int startIndex = 1, endIndex = 1;

    public SelectedText(){}

    private static final Pattern HYPHEN = Pattern.compile("-");

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

    public String getEndId() {
        return endId;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public String getSelection() {
        return selection;
    }

    public String getStartId() {
        return startId;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public boolean isValid() {
        return hasSelection() && hasStart() && hasEnd();
    }

    private boolean hasSelection() {
        return selection != null && selection.trim().length() > 0;
    }

    private boolean hasStart() {
        return startId != null && startId.trim().length() > 0;
    }

    private boolean hasEnd() {
        return endId != null && endId.trim().length() > 0;
    }

    public void setEndId(String endId) {
        this.endId = endId;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

    public void setSelection(String selection) {
        this.selection = selection;
    }

    public void setStartId(String startId) {
        this.startId = startId;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public String getFirstWord(){
        String[] words = StringUtils.split(selection);
        return words[0];
    }

    public String getLastWord(){
        String[] words = StringUtils.split(selection);
        return words[words.length-1];
    }

    public boolean startIsChildOfEnd() {
        return startId.startsWith(endId) && endId.length() < startId.length();
    }

    public int howDeepIsStartInEnd() {
        int n = 0;
        String start[] = HYPHEN.split(startId);
        String end[] = HYPHEN.split(endId);
        for (int i = 0; i < start.length; ++i) {
            if (i < end.length) {
                if (!start[i].equals(end[i])) {
                    return -1;
                }
            } else {
                ++n;
            }
        }
        return n;
    }

    public int howDeepIsEndInStart() {
        int n = 0;
        String start[] = HYPHEN.split(startId);
        String end[] = HYPHEN.split(endId);
        for (int i = 0; i < end.length; ++i) {
            if (i < start.length) {
                if (!end[i].equals(start[i])) {
                    return -1;
                }
            } else {
                ++n;
            }
        }
        return n;
    }

    public boolean endIsChildOfStart() {
        return endId.startsWith(startId) && endId.length() > startId.length();
    }

    @Override
    public String toString(){
        StringBuffer buffer = new StringBuffer();
        buffer.append(startId + "[" + startIndex + "] , ");
        buffer.append(endId + "[" + endIndex + "] : ");
        buffer.append(selection);
        return buffer.toString();
    }






}