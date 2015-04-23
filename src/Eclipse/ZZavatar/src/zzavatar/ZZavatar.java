package zzavatar;

import java.util.ArrayList;

import processing.core.*;


public class ZZavatar extends PApplet {

	protected ZZModel clone;
	protected ArrayList<ZZModel> avatars;
	protected boolean debug;
	protected ZZkinect kinect;
	protected PShape debugSphere;

	//------ déclaration des variables de couleur utiles ----
	final int jaune=color(255,255,0);
	final int vert=color(0,255,0);
	final int rouge=color(255,0,0);
	final int bleu=color(0,0,255);
	final int noir=color(0,0,0);
	final int blanc=color(255,255,255);
	final int bleuclair=color(0,255,255);
	final int violet=color(255,0,255);
	
	final int widthWindow = 1280;	//largeur de la fenetre principale
	final int heightWindow = 960;	//hauteur de la fenetre principale
	
	int distanceCamXZ=400; // variable distance à la caméra dans plan XZ
	int distanceCamYZ=0; // variable distance à la caméra dans plan YZ

	int angleCamXZ=270; // angle dans le plan XZ de la visée de la caméra avec l'axe des X dans le plan XZ
	int angleCamYZ=90; // angle avec axe YZ de la visée de la caméra dans le plan YZ

	public void setup() {
    	/***************************************************************
    	 * 
    	 *  fonction setup() standard
    	 * 
    	 ***************************************************************/
    	
	    size(widthWindow, heightWindow, P3D);	// ouverture de la fenetre en P3D
	    frame.setTitle("ZZavatar");				// modification du titre de la frame
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
	    
	    // Orientation et echelle du modele et rotation de l'avant bras bras droit
	    for (int i = 0; i < avatars.size(); i++) {
	    	avatars.get(i).scale(64);
	    	avatars.get(i).rotateX(PI);
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
			translate(0, 0, -500);
			image(kinect.rgbImage, -kinect.width/2, -kinect.height/2);	// affiche l'image couleur en haut a gauche
			
			translate(0, 0, 50);
			image(kinect.kinectV2.getBodyTrackImage(), -kinect.width/2, -kinect.height/2);	// affiche la profondeur en haut a droite
			popMatrix();
			
			for (int i = 0; i < kinect.skeletonsV2.length; i++) {
				if (kinect.skeletonsV2[i].isTracked()) {
					clone.move(kinect.getSkeleton(i));
				}
			}
		}
	    
	    vision();
	    
	    //Afficher le clone
	    clone.draw();
	    //Afficher le centre de la scène
	    shape(debugSphere);
	    lights();			// ajout de lumiere
	}
	  
	public void vision() { // comportement "spécial"
    	/***************************************************************
    	 * 
    	 *  gere la camera
    	 * 
    	 ***************************************************************/
    	
	    //Modifie la camera afin de voir convenablement le modele
		camera(distanceCamXZ*cos(radians(angleCamXZ)), distanceCamXZ*sin(radians(angleCamYZ)), ((height/2)/tan((float) (PI*30 / 180))), 0, 0, 0, 0, 1, 0);
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
	    line (0,0,0,150,0,0);
	    
	    // --- Oy
	    stroke(vert);
	    line (0,0,0,0,-150,0);
	    
	    // --- Oz
	    stroke(bleu);
	    line (0,0,0,0,0,-150); 
	    
	    text(clone.getChildCount(), 100, -100);
	    debugSkeleton(clone.skeleton);
	}

    public void debugSkeleton(ZZkeleton sk) {
    	/***************************************************************
    	 * 
    	 *  affiche les joints du squelette
    	 * 
    	 ***************************************************************/
    	
    	for (int i = 0; i < sk.joints.length; i++) {
			pushMatrix();
			stroke(rouge);
			translate(sk.joints[i].x, sk.joints[i].y, sk.joints[i].z);
			shape(debugSphere);
			popMatrix();
			text(((Boolean)(kinect.skeletonsV2[0].isTracked())).toString(), -kinect.width/8, -kinect.height/8);
			fill(153, 0, 0);
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
    	  		if (keyCode == UP) { // si touche Haut appuyée
    	  			angleCamYZ=angleCamYZ+5;
                } else if (keyCode == DOWN) {// si touche BAS appuyée
                	angleCamYZ=angleCamYZ-5;
                } else if (keyCode == LEFT) {// si touche GAUCHE appuyée
                	angleCamXZ=angleCamXZ+5;
                } else if (keyCode == RIGHT) {// si touche DROITE appuyée
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
	
