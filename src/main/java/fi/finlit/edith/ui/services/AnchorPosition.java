/**
 * 
 */
package fi.finlit.edith.ui.services;

/**
 * Position provides
 *
 * @author tiwe
 * @version $Id$
 */
public class AnchorPosition{
    
    private final int act;
    
    private final int level2;
    
    private final boolean sp;
    
    public AnchorPosition(String id){
        String[] elements = id.split("\\-");
        act = Integer.valueOf(elements[0].substring(3));
        if (elements[1].startsWith("sp")){
            level2 = Integer.valueOf(elements[1].substring(2));
            sp = true;
        }else{
            level2 = Integer.valueOf(elements[1].substring(4));
            sp = false;
        }
    }
    
    public int getAct() {
        return act;
    }

    public int getLevel2() {
        return level2;
    }

    public boolean isSp() {
        return sp;
    }

    public boolean matches(int act, int level2, boolean sp) {
        return this.act == act && this.level2 == level2 && this.sp == sp;
    }
    
    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder.append("act").append(act).append("-");
        builder.append(sp ? "sp" : "stage").append(level2);
        return builder.toString();
    }
    
}