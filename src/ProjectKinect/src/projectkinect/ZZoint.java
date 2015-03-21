package projectkinect;

import java.util.ArrayList;

import processing.core.*;

class ZZoint extends ZZector {
    protected int parent;
    protected ArrayList<Integer> children;

    public ZZoint(float[] o, int p, int[] c) {
    	super(o[0], o[1], o[2]);
    	origin = new PVector(o[0], o[1], o[2]);
    	parent = p;
    	children = new ArrayList<Integer>();
    	if (c!=null && c[0]!=-1) {
    		for (int i = 0; i < c.length; i++) {
				children.add(c[i]);
			}
    	}
    }
    
    public ZZoint(float[] o, int p) {
    	this(o, p, null);
    }
    
    public int getParent() {
    	/***************************************************************
    	 * 
    	 *  retourne le code valeur du joint père
    	 * 
    	 ***************************************************************/
    	
    	return parent;
    }
    
    public Integer[] getChildren() {
    	/***************************************************************
    	 * 
 	     *   retourne les codes de valeur des joints enfants
 	     * 
 	     ***************************************************************/
    	
    	return children.toArray(new Integer[children.size()]);
    }
    
    public void addChild(int code) {
    	/***************************************************************
    	 * 
 	     *   ajoute le code valeur d'un nouveau joint enfant
 	     * 
 	     ***************************************************************/

    	children.add(code);
    	
    }
    
    @Override
    public String toString() {
    	// TODO Auto-generated method stub
    	String retour = new String("Parent : " + parent + " ; Enfants : ");
    	for (int i = 0; i < children.size(); i++) {
    		retour += children.get(i) + " ";
    	}
    	return retour;
    }
    
    public static void main() {
    	
    }
}
