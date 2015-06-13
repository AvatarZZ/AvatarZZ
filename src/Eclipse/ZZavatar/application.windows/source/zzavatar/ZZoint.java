package zzavatar;

import java.util.ArrayList;

import KinectPV2.KJoint;
import processing.core.*;

class ZZoint extends ZZector {
    protected int parent;
    protected ArrayList<Integer> children;
    protected int state;
	protected int type;
	protected PMatrix3D orientation;
    
    public ZZoint(float o0, float o1, float o2, int p, int[] c, PMatrix3D mat) {
    	super(o0, o1, o2);
    	origin = new PVector(o0, o1, o2);
    	parent = p;
    	children = new ArrayList<Integer>();
    	if (c!=null && c[0]!=-1) {
			for (int i = 0; i < c.length; i++) {
				children.add(c[i]);
			}
    	}
    	if (mat != null) {
    		orientation = mat.get();
    	} else {
    		orientation = new PMatrix3D();
    	}
    }
    
    public ZZoint(float o0, float o1, float o2, int p, int[] c) {
    	this(o0, o1, o2, p, c, null);
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
    
    public int[] getChildren() {
    	/***************************************************************
    	 * 
 	     *	retourne les codes de valeur des joints enfants
 	     * 
 	     ***************************************************************/
    	
    	int[] retour = null;
    	
    	if(children.size()!=0) {
	    	retour = new int[children.size()];
	    	
	    	for (int i = 0; i < retour.length; i++) {
				retour[i] = children.get(i);
			}
    	}
    	
    	return retour;
    }
    
    public void addChild(int code) {
    	/***************************************************************
    	 * 
 	     *	ajoute le code valeur d'un nouveau joint enfant
 	     * 
 	     ***************************************************************/

    	children.add(code);
    }
    
    @Override
    public String toString() {
    	/***************************************************************
    	 * 
 	     *	Fonction toString classique
 	     * 
 	     ***************************************************************/

    	String retour = new String("Coordonnees : " + x + " " + y + " " + z);
    	return retour;
    }
    
    public static ZZoint[] add(ZZoint[] a, ZZoint[] b) {
    	/***************************************************************
    	 * 
 	     *	Fait la somme de deux vecteurs de ZZoint et retourne le resultat
 	     * 
 	     ***************************************************************/

    	ZZoint[] c = new ZZoint[a.length];
    	
    	for (int i = 0; i < c.length; i++) {
    		if(a[i] != null && b[i] != null) {
    			c[i] = a[i].copy();
    			c[i].add(b[i]);
    		}
    	}
    	
    	return c;
    }
    
    public static void div(ZZoint[] a, float f) {
    	/***************************************************************
    	 * 
 	     *	Divise un vecteur de ZZoint par un scalaire
 	     * 
 	     ***************************************************************/
    	
    	for (int i = 0; i < a.length; i++) {
    		if(a[i] != null)
    			div(a[i], f, a[i]);
		}
    }
    
    public void avg(ZZoint b) {
    	/***************************************************************
    	 * 
 	     *	fait la moyenne avec un autre ZZoint
 	     * 
 	     ***************************************************************/
    	
    	add(b);
    	div(2);
    }
    
    public static ZZoint[] lerp(ZZoint[] a, ZZoint[] b, float amt) {
    	/***************************************************************
    	 * 
 	     *	fonction lerp appliquee a un vecteur de ZZoint
 	     * 
 	     ***************************************************************/
    	
    	ZZoint[] c = new ZZoint[a.length];
    	
    	for (int i = 0; i < a.length; i++) {
    		if(a[i] != null && b[i] != null) {
    			c[i] = a[i].copy();
    			c[i].lerp(b[i], amt);
    		}
		}

        return c;
    }
    
    @Override
    public ZZoint copy() {
    	/***************************************************************
    	 * 
 	     *	fonction de copy du ZZoint
 	     * 
 	     ***************************************************************/
    	
    	return new ZZoint(x, y, z, parent, getChildren(), orientation);
    }
    
    @Override
    public void lerp(PVector v, float amt) {
    	/***************************************************************
    	 * 
 	     *	adaptation de lerp au ZZoint
 	     * 
 	     ***************************************************************/
    	
    	super.lerp(v, amt);
    }
}
