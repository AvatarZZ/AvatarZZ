package zzavatar;

import java.util.ArrayList;
import processing.core.*;


public class ZZavatar extends PApplet {
	/***************************************************************
	 * 
	 *  Classe principal contenant le main
	 * 
	 ***************************************************************/
	
	protected ZZModel clone;				// modele courant
	protected ArrayList<ZZModel> avatars;	// modeles
	protected ZZkinect kinect;				// capteur kinect
	protected ZZbackground fond;			// fond de scene
	protected ZZoptimiseur better;			// optimisation
	final int NBCAPT = 3;					// nombre de captures pour moyennage
	protected boolean debug;				// activation du mode debug
	protected PShape debugSphere;			// spheres de debugage
	protected PShape surprise;				// sabre laser
	boolean test1 = false;					// booleen pour la surprise
	boolean test2 = false;					// idem
	
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

	float cameraX = 0;
	float cameraY = 0;
	float cameraZ = 200;
	
	public void setup() {
    	/***************************************************************
    	 * 
    	 *  fonction setup() standard
    	 * 
    	 ***************************************************************/

	    frame.setTitle("ZZavatar");		// modification du titre de la frame
	    size(1280, 760, P3D);			// ouverture de la fenetre en P3D
	    frameRate(30);					// limitation du rafraichissement
	    
	    // options de debug
	    debug = false;	
	    debugSphere = createShape(SPHERE, 8);
	    
	    // initialisation de la kinect
	    kinect = new ZZkinectV1(this);
	    if(!kinect.available()) {
	    	kinect = new ZZkinectV2(this);
	    }
	    
	    // chargement des fonds
	    fond = new ZZbackground(this);
	    
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
	    fond.draw(); 		// affiche le background
	    
	    if(debug) {debugTools();} 	// outils de debug
	    
	    pushMatrix();
	    if (kinect.available()) { 	// si la kinect est presente
			kinect.refresh();		// mise a jour de la kinect
			
			pushMatrix();
			translate(-kinect.getWidth()/2, -kinect.getHeight()/2, -800);
			popMatrix();
			
			if(kinect.available()) {
				int [] usersDetected = kinect.getUsers();
				
				if (usersDetected.length > 0) {		// si il y a un utilisateur
					better.addEch(kinect.getSkeleton(usersDetected[0]));	// on ajoute les donnees du premier joueur detecte
				}
			}
			
			if (better.dataAvailable()) {		// si on a des donnees optimisees disponibles
				clone.move(better.getOptimizedValue());	// on fait bouger l'avatar
			}
		}
	    	    
	    // Afficher le clone
	    clone.draw();
	    popMatrix();
	    
	    // gestion de la camera
	    vision();
	    
	    if(kinect.getJoinedHands() != null)
	    	test2 = true;
	    
	    if(test1 && test2) {
	    	ZZoint tmp = clone.getSkeleton().getJoint(ZZkeleton.HAND_RIGHT);
	    	surprise.resetMatrix();
	    	surprise.translate(tmp.x, tmp.y, tmp.z);

	    	shape(surprise);
	    }
	    
	    // lumiere dans la scene
	    ambientLight(cameraX, cameraY, cameraZ) ;			// ajout de lumiere
	}
	  
	public void vision() { // comportement "special"
    	/***************************************************************
    	 * 
    	 *  gere la camera (vision orientee vers le personnage)
    	 * 
    	 ***************************************************************/
    	
		camera(cameraX, cameraY, cameraZ+clone.getPosition().z+200,
				clone.getPosition().x, clone.getPosition().y, clone.getPosition().z, 0, 1, 0);
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
	    	case 'f' :	// changer de fond
	    	  	fond.next();
	            break;
	    	case 'g' :	// active/desactive le fond
	    	  	fond.activate();
	            break;
	    	case ' ' :
	    		test1 = !test1;
	    		test2 = false;
	    		break;
	    	case '4' :
	    	  	cameraX-=5;
	            break;
	    	case '6' :
		        cameraX+=5;
		        break;
	    	case '2' :
	    	  	cameraY+=5;
	            break;
	    	case '8' :
		        cameraY-=5;
		        break;
	    	case '+' : 
	    	  	cameraZ-=5;
		        break;
	    	case '-' :
	    	  	cameraZ+=5;
		        break;
	    	case CODED :
    	  		if (keyCode == UP) { 			// si touche Haut appuyée
    	  			cameraY-=5;
                } else if (keyCode == DOWN) {	// si touche BAS appuyée
                	cameraY+=5;
                } else if (keyCode == LEFT) {	// si touche GAUCHE appuyée
                	cameraX-=5;
                } else if (keyCode == RIGHT) {	// si touche DROITE appuyée
                	cameraX+=5;
                }
    	  		break;
	    	case '5' :
	    	  	cameraX=0;
	    	  	cameraY=0;
	    	  	cameraZ=200;
		        break;
	    }
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
	
