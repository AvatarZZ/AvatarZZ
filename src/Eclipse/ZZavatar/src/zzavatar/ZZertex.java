package zzavatar;

import processing.core.*;
import java.util.ArrayList;

class ZZertex extends ZZector {
    protected ArrayList<Integer> group;
    protected ArrayList<Integer> placeInGroup;
    protected ArrayList<Integer> placeInShape;
    protected float coef; /** correspond a la cardinalite de group **/
    
    public ZZertex(float x, float y, float z) {
    	super(x, y, z);
    	group = new ArrayList<Integer>();
    	placeInGroup = new ArrayList<Integer>();
    	placeInShape = new ArrayList<Integer>();
    	coef = 0;
    }
    
    public void addOccurence(int gnum, int snum, int vnum) {
    	/***************************************************************
    	 * 
    	 *	ajoute cette occurence de vertex
    	 * 
    	 ***************************************************************/

    	group.add(gnum);
    	coef += group.contains(gnum) ? 0 : 1;
    	placeInGroup.add(snum);
    	placeInShape.add(vnum);
    }
    
    public void apply(PShape shape) {	// modifie les vertices et pas la matrice
    	/***************************************************************
    	 * 
    	 *  applique la valeur du vertex a toutes ses occurences dans un modele
    	 * 
    	 ***************************************************************/
      
    	for (int i = 0; i < numberOfOccurences(); i++) {
    		((shape.getChild(group.get(i))).getChild(placeInGroup.get(i))).setVertex(placeInShape.get(i), this);
    	}
    }

    public int numberOfOccurences() {
    	/***************************************************************
    	 * 
    	 *	retourne le nombre de groupes auxquels appartient ce vertex
    	 * 
    	 ***************************************************************/

    	return group.size();
    }
}
