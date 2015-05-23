package zzavatar;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import KinectPV2.KJoint;
import KinectPV2.Skeleton;
import SimpleOpenNI.SimpleOpenNI;

class ZZkinectV1 implements ZZkinect {
	protected SimpleOpenNI kinectV1;
	protected PApplet app;
	protected int height = 0;	// hauteur de la capture
	protected int width = 0;	// largeur de la capture
	protected int version = 0;	// version de la Kinect utilisee
	
	protected PImage rgbImage;		// capture video normale
	protected PImage depthImage;	// capture de profondeur
	protected Skeleton [] skeletonsV1_ColorMap;
	
	private int[] refKinect1 = new int[SKELETON_SIZE];
	
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
					retour[i].mult(-64);
				} else {
					retour[i] = null;
				}
			}
			
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
		 * Retourne la version de la kinect utilisee
		 * 
		 *****************************************************/
		
		return version;
	}

	@Override
	public void drawSkeletons() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawSkeleton_1(int userId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawSkeleton_2(KJoint[] joints) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawJoint(KJoint[] joints, int jointType) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawBone(KJoint[] joints, int jointType1, int jointType2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawHandState(KJoint joint) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handState(int handState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getIndexColor(int index) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}
}
