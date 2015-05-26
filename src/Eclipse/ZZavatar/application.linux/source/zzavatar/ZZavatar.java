package zzavatar;

import java.util.ArrayList;
import processing.core.*;


public class ZZavatar extends PApplet {
	/***************************************************************
	 * 
	 *  Classe principal contenant le main
	 * 
	 ***************************************************************/
	
	protected ZZModel clone;
	protected ArrayList<ZZModel> avatars;
	protected boolean debug;
	protected ZZkinect kinect;
	protected PShape surprise;
	protected PShape debugSphere;
	protected ZZoptimiseur better;
	final int NBCAPT = 3;	// nombre de captures pour moyennage
	boolean test1 = false;
	boolean test2 = false;
	
	// declaration des variables de couleur utiles
	final int jaune=color(255,255,0);
	final int vert=color(0,255,0);
	final int rouge=color(255,0,0);
	final int bleu=color(0,0,255);
	final int noir=color(0,0,0);
	final int blanc=color(255,255,255);
	final int bleuclair=color(0,255,255);
	final int violet=color(255,0,255);
	
	final int widthWindow = 1280;//1920;	//largeur de la fenetre principale
	final int heightWindow = 800;//1080;	//hauteur de la fenetre principale

	int distanceCamXZ = 100; // variable distance à la caméra dans plan XZ
	int distanceCamYZ = 100; // variable distance à la caméra dans plan YZ

	int angleCamXZ = 270; // angle dans le plan XZ de la visée de la caméra avec l'axe des X dans le plan XZ
	int angleCamYZ = 90; // angle avec axe YZ de la visée de la caméra dans le plan YZ

	public void setup() {
    	/***************************************************************
    	 * 
    	 *  fonction setup() standard
    	 * 
    	 ***************************************************************/

	    frame.setTitle("ZZavatar");				// modification du titre de la frame
	    size(1280, 760, P3D);	// ouverture de la fenetre en P3D
	    frameRate(30);						// limitation du rafraichissement
	    
	    // options de debug
	    debug = false;	
	    debugSphere = createShape(SPHERE, 8);
	    
	    // initialisation de la kinect
	    kinect = new ZZkinectV1(this);
	    if(!kinect.available()) {
	    	kinect = new ZZkinectV2(this);
	    }
	    
	    // chargement des modeles a partir de la liste
	    avatars = ZZModel.loadModels(this, "./data/avatars.bdd");
	    surprise = loadShape("./data/lightsaber.obj");
	    
	    // recuperation du premier clone pour affichage
	    clone = avatars.get(0);

	    // Orientation et echelle du modele
	    for (int i = 0; i < avatars.size(); i++) {
	    	avatars.get(i).scale(64);
	    	avatars.get(i).rotateY(PI);
	    	avatars.get(i).rotateX(PI);
	    	avatars.get(i).initBasis();
	    }
	    surprise.rotateX(HALF_PI);
	    surprise.scale((float) 1.5);
	    
	    // initiallisation de l'optimiseur
	    better = new ZZoptimiseur(NBCAPT, clone.getSkeleton().getJoints());
	}
	  
	public void draw() {
    	/***************************************************************
    	 * 
    	 *  fonction draw() standard
    	 * 
    	 ***************************************************************/
    	
	    background(100);	// efface l'ecran
	    
	    if(debug) {debugTools();} 	// outils de debug
	    
	    pushMatrix();
	    if (kinect.available()) { 	// si la kinect est presente
			kinect.refresh();		// mise a jour de la kinect
			pushMatrix();
			translate(-kinect.getWidth()/2, -kinect.getHeight()/2, -800);
			//image(kinect.getRGBImage(), 0, 0);	// affiche l'image couleur en haut a gauche
			
			//kinect.drawSkeletons();
			
			translate(0, 0, 50);
			//image(kinect.kinectV2.getBodyTrackImage(), 0, 0);	// affiche la profondeur en haut a droite
			popMatrix();
			
			if(kinect.available()) {
				int [] usersDetected = kinect.getUsers();
				
				if (usersDetected.length > 0) {		// si il y a un utilisateur
					better.addEch(kinect.getSkeleton(usersDetected[0]));	// on ajoute les données du premier joueur detecte
					//printMatrix(kinect.getSkeleton(usersDetected[0])[ZZkeleton.ROOT].orientation);
					//applyMatrix(kinect.getSkeleton(usersDetected[0])[ZZkeleton.ROOT].orientation);
				}
			}
			
			if (better.dataAvailable()) {		// si on a des donnees optimisees disponibles
				clone.move(better.getOptimizedValue());	// on fait bouger l'avatar
			}
		}
	    
	    // gestion de la camera
	    vision();
	    
	    // Afficher le clone
	    clone.draw();
	    popMatrix();
	    if(kinect.getJoinedHands() != null)
	    	test2 = true;
	    
	    if(test1 && test2) {
	    	ZZoint tmp = clone.getSkeleton().getJoint(ZZkeleton.HAND_RIGHT);
	    	surprise.resetMatrix();
	    	surprise.translate(tmp.x, tmp.y, tmp.z);

	    	shape(surprise);
	    }
	    
	    // lumiere dans la scene
	    lights();			// ajout de lumiere
	}
	  public void printMatrix(PMatrix3D m) {
		  String a = m.m00 + " " + m.m01 + " " + m.m02 + " " + m.m03 + "\n"
				 + m.m10 + " " + m.m11 + " " + m.m12 + " " + m.m13 + "\n"
				 + m.m20 + " " + m.m21 + " " + m.m22 + " " + m.m23 + "\n"
				 + m.m30 + " " + m.m31 + " " + m.m32 + " " + m.m33 + "\n";
		  println(a);
	  }
	  
	public void vision() { // comportement "special"
    	/***************************************************************
    	 * 
    	 *  gere la camera
    	 * 
    	 ***************************************************************/
    	
	    // Modifie la camera afin de voir convenablement le modele
		camera(0, 0, 200, 0, 0, 0, 0, 1, 0);
		//camera(distanceCamXZ*cos(radians(angleCamXZ)), distanceCamYZ*sin(radians(angleCamYZ)), ((height/2)/tan((float) (PI*30 / 180))), 0, 0, 0, 0, 1, 0);
	}
	  
	public void debugTools() {
    	/***************************************************************
    	 * 
    	 *  gere les outils de debuggage
    	 * 
    	 ***************************************************************/
    	
	    text(frameRate, 50, 50);
	    
	    stroke(jaune);
	    // box(width, height, depth);
	    box(200, 5, 200);
	    
	    // affiche le repère Ox,Oy,Oz
	    
	    //---- Ox
	    stroke(rouge);
	    line (0,0,0,800,0,0);
	    
	    // --- Oy
	    stroke(vert);
	    line (0,0,0,0,-800,0);
	    
	    // --- Oz
	    stroke(bleu);
	    line (0,0,0,0,0,-800); 
	    
	    text(clone.getChildCount(), 100, -100);
	    debugSkeleton(clone.skeleton);
	}

    public void debugSkeleton(ZZkeleton sk) {
    	/***************************************************************
    	 * 
    	 *  affiche les joints du squelette
    	 * 
    	 ***************************************************************/
    	kinect.drawSkeletons();
    	ZZoint[] jts = sk.getJoints();
    	for (int i = 0; i < jts.length; i++) {
			pushMatrix();
			stroke(rouge);
			translate(jts[i].x, jts[i].y, jts[i].z);
			shape(debugSphere);
			popMatrix();
		}
    }

	public void keyPressed() {
    	/***************************************************************
    	 * 
    	 *  fonction keyPressed() standard
    	 * 
    	 ***************************************************************/
    	
	    switch(key) { // ou keyCode 
	    	case 'd' : 	// passer en mode debug
	    		debug = !debug;
	            break;
	    	case 's' :	// changer d'avatar
	    	  	int suiv = avatars.indexOf(clone)+1;
	    	  	suiv = suiv >= avatars.size() ? 0 : suiv;
	    	  	clone = avatars.get(suiv);
	            break;
	    	case ' ' :
	    		test1 = !test1;
	    		test2 = false;
	    		break;
	    	case '8' : 
	    	  	angleCamXZ=angleCamXZ+5;
	            break;
	    	case '2' : 
		        angleCamXZ=angleCamXZ-5;
		        break;
	    	case '+' : 
	    	  	distanceCamXZ=distanceCamXZ-5;
		        break;
	    	case '-' : 
	    	  	distanceCamXZ=distanceCamXZ+5;
		        break;
	    	case CODED :
    	  		if (keyCode == UP) { 			// si touche Haut appuyée
    	  			angleCamYZ=angleCamYZ+5;
                } else if (keyCode == DOWN) {	// si touche BAS appuyée
                	angleCamYZ=angleCamYZ-5;
                } else if (keyCode == LEFT) {	// si touche GAUCHE appuyée
                	angleCamXZ=angleCamXZ+5;
                } else if (keyCode == RIGHT) {	// si touche DROITE appuyée
                	angleCamXZ=angleCamXZ-5;
                }
    	  		break;
	    }
	}
	  
	public void mousePressed()  {
    	/***************************************************************
    	 * 
    	 *  methode mousePressed() standard
    	 * 
    	 ***************************************************************/
	    
	    
	}

	public void mouseReleased() {
    	/***************************************************************
    	 * 
    	 *  methode mouseReleased() standard
    	 * 
    	 ***************************************************************/
	    
	  
	}

	public void mouseDragged() {
    	/***************************************************************
    	 * 
    	 *  methode mouseDragged() standard
    	 * 
    	 ***************************************************************/
	    
	    
	}

	public void mouseMoved() {
    	/***************************************************************
    	 * 
    	 *  methode mouseMouved() standard
    	 * 
    	 ***************************************************************/
	    
	}
	

	
	public static void main(String _args[]) {
    	/***************************************************************
    	 * 
    	 *  methode main() standard
    	 * 
    	 ***************************************************************/
    	
		PApplet.main(new String[] { zzavatar.ZZavatar.class.getName() });
	}	
}
	
