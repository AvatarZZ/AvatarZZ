package zzavatar;

import java.util.ArrayList;

import KinectPV2.*;
import SimpleOpenNI.*;
import processing.core.*;


public class ZZavatar extends PApplet {

	protected ZZModel clone;
	protected ArrayList<ZZModel> avatars;
	protected boolean debug;
	protected ZZkinect kinect;
	protected PShape debugSphere;

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
	    size(widthWindow, heightWindow, P3D);	// ouverture de la fenetre en P3D
	    sketchFullScreen();
	    //frameRate(25);						// limitation du rafraichissement
	    
	    // options de debug
	    debug = false;	
	    debugSphere = createShape(SPHERE, 8);
	    
	    // initialisation de la kinect
	    kinect = new ZZkinect(this);
	    
	    // chargement des modeles a partir de la liste
	    avatars = ZZModel.loadModels(this, "./data/avatars.bdd");
	    
	    // recuperation du premier clone pour affichage
	    clone = avatars.get(0);

	    // Orientation et echelle du modele
	    for (int i = 0; i < avatars.size(); i++) {
	    	avatars.get(i).scale(64);
	    	avatars.get(i).rotateY(PI);
	    	avatars.get(i).rotateX(PI);
	    	avatars.get(i).initBasis();
	    }
	}
	  
	public void draw() {
    	/***************************************************************
    	 * 
    	 *  fonction draw() standard
    	 * 
    	 ***************************************************************/
    	
	    background(100);	// efface l'ecran
	    
	    if(debug) {debugTools();} 	// outils de debug
	    
	    if (kinect.available()) { 	// si la kinect est presente
			kinect.refresh();		// mise a jour de la kinect
			pushMatrix();
			translate(-kinect.width/2, -kinect.height/2, -800);
			image(kinect.rgbImage, 0, 0);	// affiche l'image couleur en haut a gauche
			
			//kinect.drawSkeletons();
			
			translate(0, 0, 50);
			//image(kinect.kinectV2.getBodyTrackImage(), 0, 0);	// affiche la profondeur en haut a droite
			popMatrix();
			
			if(kinect.getVersion() == 2) {
				for (int i = 0; i < kinect.skeletonsV2.length; i++) {
					if (kinect.skeletonsV2[i].isTracked()) {
						
						/***************** DEBUG du move ***************************
						for (int j = 0; j < kinect.getSkeleton(i).length; j++) {
							pushMatrix();
							stroke(rouge);
							translate(4*kinect.getSkeleton(i)[j].x, 4*kinect.getSkeleton(i)[j].y, 4*kinect.getSkeleton(i)[j].z);
							shape(debugSphere);
							popMatrix();
						}
						***********************************************************/
						clone.move_2(kinect.getSkeleton(i));
					}
				}
			} else if (kinect.getVersion() == 1) {
				int[] userList = kinect.kinectV1.getUsers();
				println("On est dans le if");
				for(int i=0;i<userList.length;i++) {
					
					kinect.kinectV1.startTrackingSkeleton(userList[i]);
					
					if(kinect.kinectV1.isTrackingSkeleton(userList[i]))
				    {
						clone.move_1(kinect.getSkeleton(userList[i]));
				    }
				}
			}
		}
	    
	    // gestion de la camera
	    vision();
	    
	    // Afficher le clone
	    clone.draw();
	    
	    // lumiere dans la scene
	    lights();			// ajout de lumiere
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
    	for (int i = 0; i < sk.joints.length; i++) {
			pushMatrix();
			stroke(rouge);
			translate(sk.joints[i].x, sk.joints[i].y, sk.joints[i].z);
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
	
