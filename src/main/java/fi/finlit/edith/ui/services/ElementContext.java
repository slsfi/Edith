package fi.finlit.edith.ui.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import javax.annotation.Nullable;

import org.apache.commons.lang.mutable.MutableInt;

/**
 * ElementContext provides
 *
 * @author tiwe
 * @version $Id$
 */
public class ElementContext {
    
    public static class Item {
        
        private final String name;
        
        private final Map<String,MutableInt> counts = new HashMap<String,MutableInt>();
        
        Item(String name){
            this.name = name;
        }

        public String getName(String name) {
            MutableInt intValue = counts.get(name);
            if (intValue == null){
                intValue = new MutableInt(1);
                counts.put(name, intValue);
                return name;
            }else{
                intValue.add(1);
                return name + intValue;
            }            
        }      

    }
    
    private final Stack<Item> stack = new Stack<Item>();
    
    private final int offset;
    
    public ElementContext(int offset){
        this.offset = offset;
    }
    
    public void push(String name){
        if (!stack.isEmpty()){
            name = stack.peek().getName(name);
        }
        stack.push(new Item(name));        
    }
    
    public void pop(){
        stack.pop();
    }
    
    
    @Nullable
    public String getPath(){
        if (stack.size() > offset){
            StringBuilder b = new StringBuilder();
            for (int i = offset; i < stack.size(); i++){
                if (i > offset) b.append("-");
                b.append(stack.get(i).name);
            }
            return b.toString();    
        }else{
            return null;
        }
        
    }

}
