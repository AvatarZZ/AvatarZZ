package zzavatar;

import processing.core.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

class ZZkeleton {
    public final static int WAIST      		= 0;
    public final static int ROOT      		= 1;
    public final static int NECK     		= 2;
    public final static int HEAD      		= 3;
    public final static int SHOULDER_LEFT  	= 4;
    public final static int ELBOW_LEFT    	= 5;
    public final static int WRIST_LEFT      = 6;
    public final static int HAND_LEFT    	= 7;
    public final static int SHOULDER_RIGHT  = 8;
    public final static int ELBOW_RIGHT    	= 9;
    public final static int WRIST_RIGHT    	= 10;
    public final static int HAND_RIGHT    	= 11;
    public final static int HIP_LEFT    	= 12;
    public final static int KNEE_LEFT    	= 13;
    public final static int ANKLE_LEFT    	= 14;
    public final static int FOOT_LEFT    	= 15;
    public final static int HIP_RIGHT    	= 16;
    public final static int KNEE_RIGHT    	= 17;
    public final static int ANKLE_RIGHT    	= 18;
    public final static int FOOT_RIGHT    	= 19;
    public final static int TORSO      		= 20;
    public final static int INDEX_LEFT    	= 21;
    public final static int THUMB_LEFT    	= 22;
    public final static int INDEX_RIGHT    	= 23;
    public final static int THUMB_RIGHT    	= 24;
    
    protected float[][] lastRotation;
    protected ZZoint[] joints;
    protected int jointsNumber;
    protected String name;
    
    public ZZkeleton() {
    	jointsNumber = 25;
    	joints = new ZZoint[jointsNumber];
    	name = "default";
    	
    	// creation et initialisation du tab de dernieres rotations
    	lastRotation = new float[jointsNumber][3];
    	for (int i = 0; i < lastRotation.length; i++) {
			for (int j = 0; j < lastRotation[i].length; j++) {
				lastRotation[i][j] = 0;
			}
		}
    }
    
    public ZZkeleton(String filename) {
    	this();
    	this.load(filename);
    }
    
    protected int getTypeCode(String type) {
    	/***************************************************************
    	 * 
    	 *	matching entre les valeurs des fichiers et du code
    	 * 
    	 ***************************************************************/
      
    	int code = -1;

    	if(type.equals("WAIST")) code = 0;
    	else if(type.equals("ROOT")) code = 1;
	    else if(type.equals("NECK")) code = 2;
	    else if(type.equals("HEAD")) code = 3;
	    else if(type.equals("SHOULDER_LEFT")) code = 4;
	    else if(type.equals("ELBOW_LEFT")) code = 5;
	    else if(type.equals("WRIST_LEFT")) code = 6;
	    else if(type.equals("HAND_LEFT")) code = 7;
	    else if(type.equals("SHOULDER_RIGHT")) code = 8;
	    else if(type.equals("ELBOW_RIGHT")) code = 9;
	    else if(type.equals("WRIST_RIGHT")) code = 10;
	    else if(type.equals("HAND_RIGHT")) code = 11;
	    else if(type.equals("HIP_LEFT")) code = 12;
	    else if(type.equals("KNEE_LEFT")) code = 13;
	    else if(type.equals("ANKLE_LEFT")) code = 14;
	    else if(type.equals("FOOT_LEFT")) code = 15;
	    else if(type.equals("HIP_RIGHT")) code = 16;
	    else if(type.equals("KNEE_RIGHT")) code = 17;
	    else if(type.equals("ANKLE_RIGHT")) code = 18;
	    else if(type.equals("FOOT_RIGHT")) code = 19;
	    else if(type.equals("TORSO")) code = 20;
	    else if(type.equals("INDEX_LEFT")) code = 21;
	    else if(type.equals("THUMB_LEFT")) code = 22;
	    else if(type.equals("INDEX_RIGHT")) code = 23;
	    else if(type.equals("THUMB_RIGHT")) code = 24;
	    else if(type.equals("(null)")) code = WAIST;
      
    	return code;
    }
  
    protected int[] getTypeCode(String[] type) {
    	/***************************************************************
    	 * 
    	 *	matching entre les valeurs des fichiers et du code
    	 * 
    	 ***************************************************************/
      
    	int [] retour = new int[type.length];
  
    	for (int i = 0; i < retour.length; i++) {
    		retour[i] = getTypeCode(type[i]);
    	}
  
    	return retour;
    }
    
    public void load(String filename) {
    	/***************************************************************
   	 	 * 
   	 	 *	charge le squelette à partir d'un fichier .sk
   	 	 * 
   	 	 ***************************************************************/
     
	 	String[] file;
	 	InputStream fichier = null;		// pour ouvrir le fichier
     
	 	if(!(filename.contains(".sk"))) {
	 		PApplet.println("Chargement du squelette : attention, il se peut que " + filename + " soit incompatible");
	 	}
     
	 	// ouverture du fichier
		try {
			fichier = new FileInputStream(filename);
		} catch (FileNotFoundException e) {
			PApplet.println("Chargement du modèle : le fichier " + filename + " n'existe pas.");
		}
		file = PApplet.loadStrings(fichier);
     
	 	if(file != null) {
	 		int index = 0;
	 		while (index < file.length) {
	 			if (file[index].contains("sk ")) {
	 				name = file[index].substring(3);
	 			}
	 			else if (file[index].contains("j ")) {
	 				int type = getTypeCode((file[index].split(" "))[1]);
	 				joints[type] = new ZZoint(PApplet.parseFloat(file[index+1].substring(2).split(" ")), 
	 											getTypeCode(file[index+2].split(" ")[1]), 
												getTypeCode(file[index+3].substring(2).split(" ")));
	 				index += 3;
	 			}
	 			index++;
	 		}
	 		PApplet.println("Chargement du squelette : terminé");
	 	} else {
	 		PApplet.println("Chargement du squelette : erreur à l'ouverture du fichier " + filename);
	 	}
    }
   
    public void loadBVH(String filename) {
    	/***************************************************************
   	 	 * 
   	 	 *	charge le squelette à partir d'un fichier .bvh
   	 	 * 
   	 	 ***************************************************************/
     
    	 String[] file;
	 	 InputStream fichier = null;		// pour ouvrir le fichier
	 	 boolean again = true;
	 	 int [] tree = new int[3];		// sauvegarde de la hierarchie
	 	 int level = 1;	// niveau dans l'arborescence
     
	 	 if(!(filename.contains(".bvh"))) {
	 		 PApplet.println("Chargement du squelette : attention, il se peut que " + filename + " soit incompatible");
	 	 }
     
	 	 // ouverture du fichier
	 	 try {
	 		 fichier = new FileInputStream(filename);
	 	 } catch (FileNotFoundException e) {
	 		 PApplet.println("Chargement du modèle : le fichier " + filename + " n'existe pas.");
	 	 }
	 	 file = PApplet.loadStrings(fichier);
     
	 	 if(file != null) {
	 		 int index = 0;
	 		 while (index < file.length && again) {
	 			 if (file[index].contains("HIERARCHY")) { // chargement du squelette
	 				 name = file[index];
	 			 } else if (file[index].contains("ROOT ")) {	// initialisation des variables
	 				 tree[0] = -1;
	 				 tree[1] = getTypeCode(file[index].split(" ")[0]);
	 				 tree[2] = getTypeCode(file[index].split(" ")[1]);
	 			 } else if (file[index].contains("JOINT ")) {		//	on trouve un fils
	 				 tree[0] = tree[1];
	 				 tree[1] = tree[2];
	 				 tree[2] = getTypeCode(file[index].split(" ")[1]);
	 			 } else if (file[index].contains("{")) {		//	on descend dans la hierarchie
	 				
	 			 } else if (file[index].contains("}")) {		//	on remonte
	 				 level = -1;
	 				 tree[2] = tree[1];
	 				 tree[1] = tree[0];
	 				 if(tree[1]!=-1) {
	 					 tree[0] = joints[tree[1]].getParent();
	 				 }
	 			 } else if (file[index].contains("OFFSET ")) {	// coordonnees du joint, ajout de celui-ci	 				
	 				 float [] father = null;
	 				 float [] coords = PApplet.parseFloat(file[index].substring(file[index].indexOf("T")+2).split(" "));
	 				 float tmp = coords[1];
	 				
	 				 // calcul du point par somme des coordonnees du pere avec l'offset
	 				 father = tree[0] != -1 ? joints[tree[0]].get(father) : new float[] {0,0,0};
	 				 coords[0] = coords[0] + father[0];
	 				 coords[1] = coords[2] + father[1];
	 				 coords[2] = tmp + father[2];
	 				
	 				 if(level>0) {	// si l'on ne vient pas de remonter l'arborescence
	 					 joints[tree[1]] = new ZZoint(coords, tree[0]);
	 					 if(tree[0]!=-1) {
	 						 joints[tree[0]].addChild(tree[1]);
	 					 }
	 				 } else if(coords[0] == father[0] && coords[1] == father[1] && coords[2] == father[2]) { // on se trouve à la racine
	 					 tree[1] = tree[0];
	 					 tree[0] = joints[tree[0]].getParent();
	 				 }
	 					
	 				 level = 1;	// on descend
	 			 } else if (file[index].contains("CHANNELS ")) {		// OSEF
	 				
	 			 } else if (file[index].contains("End Site")) {		// feuille trouvee
	 				 tree[0] = tree[1];
	 				 tree[1] = tree[2];
	 			 } else if(file[index].contains("MOTION")) {	// coordonnees de motion capture non traitees
	 				 again = !again;
	 			 }
	 			 index++;
	 		 }
	 		 this.rotateY(PApplet.PI);	// rotation pour correspondre au modele
	 		 PApplet.println("Chargement du squelette : terminé");
	 	 } else {
	 		 PApplet.println("Chargement du squelette : erreur à l'ouverture du fichier " + filename);
	 	 }
    }
     
	protected void setLastRotation(int part, float theta, float phi, float epsilon) {
		/***********************************************************************
		 * 
		 * Stocke les donnees de la derniere rotation de la partie concernee
		 * 
		 ***********************************************************************/
		lastRotation[part][0] = theta;
		lastRotation[part][1] = phi;
		lastRotation[part][2] = epsilon;
	}
	
	public float[] getLastRotation(int part) {
		return lastRotation[part];
	}
    
    public ArrayList<Integer> getMember(int part) {
    	/***************************************************************
    	 * 
    	 *  renvoie la liste des joints du membre
    	 * 
    	 ***************************************************************/
      
    	ArrayList<Integer> toReturn = new ArrayList<Integer>();
    	ArrayList<Integer> pile = new ArrayList<Integer>();
    	pile.add(part);
       
    	while (!(pile.isEmpty())) {
    		Integer[] tmp = joints[pile.get(0)].getChildren();
    		if (tmp != null) {
    			for (int i = 0; i < tmp.length; i++) {
    				pile.add(tmp[i]);
    			}
    		}
    		toReturn.add(pile.remove(0));
    	}

    	return toReturn;
    }
    
    @Override
    public String toString() {
   	 	/***************************************************************
   	 	 * 
   	 	 *  toString permettant l'affichage des données du squelette
   	 	 * 
   	 	 ***************************************************************/
    	
		String retour = new String();
    	
    	for (int i = 0; i < joints.length; i++) {
			retour += "Joint : " + i + " " + joints[i]  + "\n";
		}
    	
    	return retour;
    }
    
    protected void rotateX(float angle) {
   	 	/***************************************************************
   	 	 * 
   	 	 *  pivote le squelette autour de l'axe Ox
   	 	 * 
   	 	 ***************************************************************/
    	
		rotateAround(new PVector(0,0,0), angle, 0, 0);
    }
    
    protected void rotateY(float angle) {
   	 	/***************************************************************
   	 	 * 
   	 	 *  pivote le squelette autour de l'axe Oy
   	 	 * 
   	 	 ***************************************************************/
    	
		rotateAround(new PVector(0,0,0), 0, angle, 0);
    }
    
    protected void rotateZ(float angle) {
   	 	/***************************************************************
   	 	 * 
   	 	 *  pivote le squelette autour de l'axe Oz
   	 	 * 
   	 	 ***************************************************************/
    	
		rotateAround(new PVector(0,0,0), 0, 0, angle);
    }
    
    protected void rotateAround(PVector center, float theta, float phi, float epsilon) {
   	 	/***************************************************************
   	 	 * 
   	 	 *  pivote le squelette autour d'un certain point
   	 	 * 
   	 	 ***************************************************************/
    	
		for (int i = 0; i < joints.length; i++) {
			joints[i].rotateAround(center, theta, phi, epsilon);
		}
    }
    
    protected void translate(float x, float y, float z) {
   	 	/***************************************************************
   	 	 * 
   	 	 *  deplace le squelette
   	 	 * 
   	 	 ***************************************************************/
    	
		for (int i = 0; i < joints.length; i++) {
			joints[i].add(x, y, z);
		}
    }
    
    protected void scale(float s) {
   	 	/***************************************************************
   	 	 * 
   	 	 *  redimensionne le squelette
   	 	 * 
   	 	 ***************************************************************/
    	
		for (int i = 0; i < joints.length; i++) {
			joints[i].mult(s);
		}
    }

}
