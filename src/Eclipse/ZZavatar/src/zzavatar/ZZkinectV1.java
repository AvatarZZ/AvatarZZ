package zzavatar;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import SimpleOpenNI.SimpleOpenNI;

class ZZkinectV1 implements ZZkinect {
	protected SimpleOpenNI kinectV1;
	protected PApplet app;
	protected int height = 0;	// hauteur de la capture
	protected int width = 0;	// largeur de la capture
	protected int version = 0;	// version de la Kinect utilisee
	
	protected PImage rgbImage;		// capture video normale
	protected PImage depthImage;	// capture de profondeur
	
	private int[] refKinect1 = new int[SKELETON_SIZE];
	private ZZoint joinedHands = null;
	
	public ZZkinectV1(PApplet parent) {
   	 	/***************************************************************
   	 	 * 
   	 	 *  constructeur de ZZkinect
   	 	 * 
   	 	 ***************************************************************/
		
		kinectV1 = new SimpleOpenNI(parent); // marche sous eclipse
		
		if(!kinectV1.isInit()){ //kinect reconnue ou pas
			PApplet.println("Kinect non reconnue ou non presente");
			kinectV1 = null;
		} else { //kinect reconnue
			kinectV1.enableDepth();	//chargement de la profondeur
			kinectV1.enableRGB();	//chargement de l'image couleur
			kinectV1.enableUser(parent);	//autoriser le tracking du squelette des utilisateurs
			
			height = kinectV1.depthHeight();	//hauteur de la capture
			width = kinectV1.depthWidth();		//largeur de la capture
			joinedHands = new ZZoint(0, 0, 0, -1, null);	// details des mains jointes
			
			version = 1;	// actualisation de la version
			app = parent;
			
			if ((width != 640) || (height != 480)) { //reconnaissance des parametres
				PApplet.println("Erreur sur les dimension de capture kinect");
				kinectV1 = null;
			}
			//Matching du skeleton avec une kinect1
			refKinect1[ZZkeleton.WAIST] = -100;
			refKinect1[ZZkeleton.ROOT] = -101;
			refKinect1[ZZkeleton.NECK] = SimpleOpenNI.SKEL_NECK;
			refKinect1[ZZkeleton.HEAD] = SimpleOpenNI.SKEL_HEAD;
			refKinect1[ZZkeleton.SHOULDER_LEFT] = SimpleOpenNI.SKEL_LEFT_SHOULDER;
			refKinect1[ZZkeleton.ELBOW_LEFT] = SimpleOpenNI.SKEL_LEFT_ELBOW;
			refKinect1[ZZkeleton.WRIST_LEFT] = SimpleOpenNI.SKEL_LEFT_HAND; // inversion main poignet
			refKinect1[ZZkeleton.HAND_LEFT] = -100;       					// inversion avec wrist
			refKinect1[ZZkeleton.SHOULDER_RIGHT] = SimpleOpenNI.SKEL_RIGHT_SHOULDER;	
			refKinect1[ZZkeleton.ELBOW_RIGHT] = SimpleOpenNI.SKEL_RIGHT_ELBOW;
			refKinect1[ZZkeleton.WRIST_RIGHT] = SimpleOpenNI.SKEL_RIGHT_HAND; // inversion main poignet
			refKinect1[ZZkeleton.HAND_RIGHT] = -100;				    	  // inversion main poignet
			refKinect1[ZZkeleton.HIP_LEFT] = SimpleOpenNI.SKEL_LEFT_HIP;	
			refKinect1[ZZkeleton.KNEE_LEFT] = SimpleOpenNI.SKEL_LEFT_KNEE;
			refKinect1[ZZkeleton.ANKLE_LEFT] = SimpleOpenNI.SKEL_LEFT_FOOT; // ankle left						// INVERSION pied cheville
			refKinect1[ZZkeleton.FOOT_LEFT] = -100;							// INVERSION pied cheville
			refKinect1[ZZkeleton.HIP_RIGHT] = SimpleOpenNI.SKEL_RIGHT_HIP;	
			refKinect1[ZZkeleton.KNEE_RIGHT] = SimpleOpenNI.SKEL_RIGHT_KNEE;
			refKinect1[ZZkeleton.ANKLE_RIGHT] = SimpleOpenNI.SKEL_RIGHT_FOOT; // ankle right						// INVERSION pied cheville
			refKinect1[ZZkeleton.FOOT_RIGHT] = -100;		// INVERSION pied cheville
			refKinect1[ZZkeleton.TORSO] = SimpleOpenNI.SKEL_TORSO;	
			refKinect1[ZZkeleton.INDEX_LEFT] = SimpleOpenNI.SKEL_LEFT_FINGERTIP;
			refKinect1[ZZkeleton.THUMB_LEFT] = -100;
			refKinect1[ZZkeleton.INDEX_RIGHT] = SimpleOpenNI.SKEL_RIGHT_FINGERTIP;
			refKinect1[ZZkeleton.THUMB_RIGHT] = -100;
		}
	}

	@Override
	public ZZoint[] getSkeleton(int numUser) {
		/***************************************************************
   	 	 * 
   	 	 *  permet de recuperer le squelette (version 1)
   	 	 * 
   	 	 ***************************************************************/
		
		ZZoint[] retour = new ZZoint[25];
		PVector jointPos = new PVector();
		int realNum;
		//if(kinectV1.isTrackingSkeleton(numUser)) {
			
			for (int i = 0; i < retour.length; i++) {
				realNum = refKinect1[i];
				if (realNum>=0) {
					kinectV1.getJointPositionSkeleton(numUser, realNum, jointPos);
					retour[i] = new ZZoint(jointPos);
					retour[i].mult((float) -0.5);
					retour[i].z += 500;		// correction de proximité
					kinectV1.getJointOrientationSkeleton(numUser, realNum, retour[i].orientation);
				} else {
					retour[i] = null;
				}
			}
			
			// calcul du bassin waist
			retour[ZZkeleton.WAIST] = retour[ZZkeleton.HIP_LEFT].copy();
			retour[ZZkeleton.WAIST].avg(retour[ZZkeleton.HIP_RIGHT]);
			
			// calcul de la racine root
			retour[ZZkeleton.ROOT] = retour[ZZkeleton.WAIST].copy();
			retour[ZZkeleton.ROOT].avg(retour[ZZkeleton.TORSO]);
			
			// copie des poignets dans les mains
			retour[ZZkeleton.HAND_RIGHT] = retour[ZZkeleton.WRIST_RIGHT];
			retour[ZZkeleton.HAND_LEFT] = retour[ZZkeleton.WRIST_LEFT];
			
			// mise a jour des infos mains
			joinedHands.set(retour[ZZkeleton.HAND_RIGHT]);
			joinedHands.sub(retour[ZZkeleton.HAND_LEFT]);
			joinedHands.state = joinedHands.mag() < 50 ? 1 : 0;
		//}
    	
		return retour;
	}
	
	@Override
	public boolean isTrackingSkeleton(int skelNum) {
		/***************************************************************
   	 	 * 
   	 	 *  permet de savoir si le squelette skelNum est traque
   	 	 * 
   	 	 ***************************************************************/
    
		return this.kinectV1.isTrackingSkeleton(skelNum);
	}

	@Override
	public int[] getUsers() {
		/***************************************************************
   	 	 * 
   	 	 *	renvoie la liste des utilisateurs actifs
   	 	 * 
   	 	 ***************************************************************/
    	
		int [] retour = this.kinectV1.getUsers();
		
		for (int i = 0; i < retour.length; i++) {
			if(!this.isTrackingSkeleton(retour[i])) {
				this.kinectV1.startTrackingSkeleton(retour[i]);
			}
			refresh();
		}
		
		return retour;
	}

	@Override
	public void refresh() {
		/***************************************************************
   	 	 * 
   	 	 *  refresh pour Kinect V1
   	 	 * 
   	 	 ***************************************************************/
    	
		kinectV1.update();	// mise a jour de la kinect
		
		rgbImage = kinectV1.userImage();//kinectV1.rgbImage();		// mise a jour de l'image couleur
		depthImage = kinectV1.depthImage();	// mise a jour de la profondeur	
	}

	@Override
	public boolean available() {
   	 	/***************************************************************
   	 	 * 
   	 	 *  available pour Kinect V1
   	 	 * 
   	 	 ***************************************************************/
    	
        return kinectV1 != null;
	}

	@Override
	public int getVersion() {
		/*****************************************************
		 * 
		 *	Retourne la version de la kinect utilisee
		 * 
		 *****************************************************/
		
		return version;
	}

	@Override
	public ZZoint getJoinedHands() {
		/*****************************************************
		 * 
		 * Retourne la version de la kinect utilisee
		 * 
		 *****************************************************/
		
		return (joinedHands.state != 0 ? joinedHands.copy() : null);
	}

	@Override
	public void drawSkeletons() {
   	 	/***************************************************************
   	 	 * 
   	 	 *  Affiche les squelettes selon la kinect en cours
   	 	 * 
   	 	 ***************************************************************/

		for (int i = 0; i < kinectV1.getNumberOfUsers(); i++) {
    		//Change de couleur pour chaque squelette
			int col  = getIndexColor(i);
    		app.fill(col);
    		app.stroke(col);
    		
    		drawSkeleton(i);
		}
	}

	@Override
	public void drawSkeleton(int userId) {
		/**********************************************************
		 *
		 *	Affiche le squelette d'un user
		 * 
		 **********************************************************/
		//Tete
		kinectV1.drawLimb(userId, SimpleOpenNI.SKEL_HEAD, SimpleOpenNI.SKEL_NECK);
		 
		//Bras gauche
		kinectV1.drawLimb(userId, SimpleOpenNI.SKEL_NECK, SimpleOpenNI.SKEL_LEFT_SHOULDER);
		kinectV1.drawLimb(userId, SimpleOpenNI.SKEL_LEFT_SHOULDER, SimpleOpenNI.SKEL_LEFT_ELBOW);
		kinectV1.drawLimb(userId, SimpleOpenNI.SKEL_LEFT_ELBOW, SimpleOpenNI.SKEL_LEFT_HAND);
		
		//Bras droit
		kinectV1.drawLimb(userId, SimpleOpenNI.SKEL_NECK, SimpleOpenNI.SKEL_RIGHT_SHOULDER);
		kinectV1.drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_SHOULDER, SimpleOpenNI.SKEL_RIGHT_ELBOW);
		kinectV1.drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_ELBOW, SimpleOpenNI.SKEL_RIGHT_HAND);
		
		//Epaules
		kinectV1.drawLimb(userId, SimpleOpenNI.SKEL_LEFT_SHOULDER, SimpleOpenNI.SKEL_TORSO);
		kinectV1.drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_SHOULDER, SimpleOpenNI.SKEL_TORSO);
		
		//Jambe gauche
		kinectV1.drawLimb(userId, SimpleOpenNI.SKEL_TORSO, SimpleOpenNI.SKEL_LEFT_HIP);
		kinectV1.drawLimb(userId, SimpleOpenNI.SKEL_LEFT_HIP, SimpleOpenNI.SKEL_LEFT_KNEE);
		kinectV1.drawLimb(userId, SimpleOpenNI.SKEL_LEFT_KNEE, SimpleOpenNI.SKEL_LEFT_FOOT);
		
		//Jambe droite
		kinectV1.drawLimb(userId, SimpleOpenNI.SKEL_TORSO, SimpleOpenNI.SKEL_RIGHT_HIP);
		kinectV1.drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_HIP, SimpleOpenNI.SKEL_RIGHT_KNEE);
		kinectV1.drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_KNEE, SimpleOpenNI.SKEL_RIGHT_FOOT);  
	}

	@Override
	public int getIndexColor(int index) {
   	 	/***************************************************************
   	 	 * 
   	 	 *  couleurs des squelettes des joueurs
   	 	 * 
   	 	 ***************************************************************/
    	
		int col = app.color(255);
		if (index == 0)
			col = app.color(255, 0, 0);
		if (index == 1)
		    col = app.color(0, 255, 0);
		if (index == 2)
		    col = app.color(0, 0, 255);
		if (index == 3)
		    col = app.color(255, 255, 0);
		if (index == 4)
		    col = app.color(0, 255, 255);
		if (index == 5)
		    col = app.color(255, 0, 255);

		return col;
	}
	
	@Override
	public String toString() {
   	 	/***************************************************************
   	 	 * 
   	 	 *  toString permettant l'obtention d'informations sur la Kinect
   	 	 * 
   	 	 ***************************************************************/
    	
		String out = "";
		
		if (getVersion()!=0) {
			out += "Kinect version " + getVersion() + " ouverte en " + getWidth() + " x " + getHeight();
		} else {
			out += "Kinect non initialisée";
		}
		
		return out;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public PImage getRGBImage() {
   	 	/***************************************************************
   	 	 * 
   	 	 *  renvoie l'image couleur de la Kinect
   	 	 * 
   	 	 ***************************************************************/
    	
		return this.rgbImage;
	}
}
