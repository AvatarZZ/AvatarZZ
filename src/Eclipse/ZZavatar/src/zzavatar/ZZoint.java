package zzavatar;

import java.util.ArrayList;

import KinectPV2.KJoint;
import processing.core.*;

class ZZoint extends ZZector {
    protected int parent;
    protected ArrayList<Integer> children;
    protected int state;
	protected int type;
    
    public ZZoint(float o0, float o1, float o2, int p, int[] c) {
    	super(o0, o1, o2);
    	origin = new PVector(o0, o1, o2);
    	parent = p;
    	children = new ArrayList<Integer>();
    	if (c!=null && c[0]!=-1) {
    		for (int i = 0; i < c.length; i++) {
				children.add(c[i]);
			}
    	}
    }
    
    public ZZoint(float[] o, int p, int[] c) {
    	this(o[0], o[1], o[2], p, c);
    }
    
    public ZZoint(KJoint k) {
    	/***************************************************************
    	 * 
    	 *  constructeur a partir d'un KJoint (KinectPV2)
    	 * 
    	 ***************************************************************/
    	
    	this(k.getX(), k.getY(), k.getZ(), -1, null);
    	state = k.getState();
    	type = k.getType();
    }
    
    public ZZoint(float[] o, int p) {
    	this(o, p, null);
    }
    
    public ZZoint(PVector jointPos) {
		this(jointPos.x, jointPos.y, jointPos.z, -1, null);
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
    	String retour = new String("Coordonnees : " + x + " " + y + " " + z);
    	return retour;
    }
    
    public static void main() {
    	
    }
}
