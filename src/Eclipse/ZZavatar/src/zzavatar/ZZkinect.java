package zzavatar;

import java.util.Vector;

import KinectPV2.KJoint;
import KinectPV2.KinectPV2;
import KinectPV2.Skeleton;
import SimpleOpenNI.*;
import processing.core.*;
import processing.core.PApplet;

/********************************************
 * 
 * Classe de gestion de la kinect
 *
 ********************************************/
public class ZZkinect {
	protected SimpleOpenNI kinectV1;
	protected KinectPV2 kinectV2;
	protected PApplet app;
	protected int height = 0;	// hauteur de la capture
	protected int width = 0;	// largeur de la capture
	protected int version = 0;	// version de la Kinect utilisee
	
	protected PImage rgbImage;		// capture video normale
	protected PImage depthImage;	// capture de profondeur
	protected Skeleton [] skeletonsV1;
	protected Skeleton [] skeletonsV2;
	protected Skeleton [] skeletonsV1_ColorMap;
	protected Skeleton [] skeletonsV2_ColorMap;
	
	private int[] refKinect1 = new int[25];
	
	public ZZkinect(PApplet parent) {
   	 	/***************************************************************
   	 	 * 
   	 	 *  constructeur de ZZkinect
   	 	 * 
   	 	 ***************************************************************/
		
		try {
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
		} catch (Exception e) { //Si pas de kinect 1 on cherche une kinect 2
			kinectV2 = new KinectPV2(parent);

			kinectV2.enableColorImg(true);
			kinectV2.enableDepthImg(true);
			kinectV2.enableSkeleton(true);
			kinectV2.enableBodyTrackImg(true);
			kinectV2.enableSkeleton3dMap(true);
			kinectV2.enableSkeletonColorMap(true);
			kinectV2.init();
			height = kinectV2.HEIGHTColor;
			width = kinectV2.WIDTHColor;
			app = parent;
			version = 2;	// actualisation de la version
		}
	}
	
	public ZZoint[] getSkeleton() {
   	 	/***************************************************************
   	 	 * 
   	 	 *  permet de recuperer le squelette d'un certain utilisateur
   	 	 * 
   	 	 ***************************************************************/
    
		return getSkeleton(0);
	}
	
	public ZZoint[] getSkeleton(int numUser) {
   	 	/***************************************************************
   	 	 * 
   	 	 *  permet de recuperer le squelette d'un certain utilisateur
   	 	 * 
   	 	 ***************************************************************/
    	
		ZZoint[] retour = null;
		
		if (version==1) {
			retour = getSkeleton_1(numUser);
		} else if (version==2) {
			retour = getSkeleton_2(numUser);
		}
		
		return retour;
	}
	
	private ZZoint[] getSkeleton_2(int numUser) {
   	 	/***************************************************************
   	 	 * 
   	 	 *  permet de recuperer le squelette (version 2)
   	 	 * 
   	 	 ***************************************************************/
    	
		ZZoint[] retour = new ZZoint[25];
    	KJoint[] k = skeletonsV2[numUser].getJoints();
		
		for (int i = 0; i < retour.length; i++) {
			retour[i] = new ZZoint(k[i]);
			retour[i].mult(-64);	// necessaire sinon mouvements de trop faible amplitude
			/*retour[i].y *= -1;		// correction d'orientation
			retour[i].z *= -1;		// correction d'orientation
			retour[i].x *= -1;		// effet non miroir*/
			//retour[i].z += 400;		// correction de proximité
		}
    	
		return retour;
	}

	private ZZoint[] getSkeleton_1(int numUser) {
   	 	/***************************************************************
   	 	 * 
   	 	 *  permet de recuperer le squelette (version 1)
   	 	 * 
   	 	 ***************************************************************/
		
		ZZoint[] retour = new ZZoint[25];//TODO mettre dans un tableau de 25 avec du null la ou on a pas le joion k1
		PVector jointPos = new PVector();
		int realNum;
		if(kinectV1.isTrackingSkeleton(numUser)) {
			
			for (int i = 0; i < retour.length; i++) {
				realNum = refKinect1[i];
				if (realNum>=0) {
					kinectV1.getJointPositionSkeleton(numUser, realNum, jointPos);
					retour[i] = new ZZoint(jointPos);
					retour[i].mult(-64);
//					retour[i].x *= -1;
				} else {
					retour[i] = null;
				}
			}
		}
    	
		return retour;
	}

	public void refresh() {
   	 	/***************************************************************
   	 	 * 
   	 	 *  permet de mettre a jour les champs de ZZkinect
   	 	 * 
   	 	 ***************************************************************/
    	
		if (version==1) {
			refresh_1();
		} else if (version==2) {
			refresh_2();
		}
	}
	
	public boolean available() {
   	 	/***************************************************************
   	 	 * 
   	 	 *  permet de savoir si la kinect est disponible
   	 	 * 
   	 	 ***************************************************************/
    	
		boolean retour = false;
		
		if (version==1) {
			retour = available_1();
		} else if (version==2) {
			retour = available_2();
		}
		
		return retour;
	}
	
	private void refresh_1() {
   	 	/***************************************************************
   	 	 * 
   	 	 *  refresh pour Kinect V1
   	 	 * 
   	 	 ***************************************************************/
    	
		kinectV1.update();	// mise a jour de la kinect
		
		rgbImage = kinectV1.userImage();//kinectV1.rgbImage();		// mise a jour de l'image couleur
		depthImage = kinectV1.depthImage();	// mise a jour de la profondeur		
	}
	
	private boolean available_1() {
   	 	/***************************************************************
   	 	 * 
   	 	 *  available pour Kinect V1
   	 	 * 
   	 	 ***************************************************************/
    	
        return kinectV1 != null;
    }
	
	private void refresh_2() {
   	 	/***************************************************************
   	 	 * 
   	 	 *  refresh pour Kinect V2
   	 	 * 
   	 	 ***************************************************************/

		skeletonsV2 = kinectV2.getSkeleton3d();	// mise a jour des squelettes
		skeletonsV2_ColorMap = kinectV2.getSkeletonColorMap();	// mise a jour des squelettes
		rgbImage = kinectV2.getColorImage();	// mise a jour de l'image couleur
		depthImage = kinectV2.getDepthImage();	// mise a jour de la profondeur
	}
	
	private boolean available_2() {
   	 	/***************************************************************
   	 	 * 
   	 	 *  available pour Kinect V2
   	 	 * 
   	 	 ***************************************************************/
    	
        return kinectV2 != null;
    }
	
	@Override
	public String toString() {
   	 	/***************************************************************
   	 	 * 
   	 	 *  toString permettant l'obtention d'informations sur la Kinect
   	 	 * 
   	 	 ***************************************************************/
    	
		String out = "";
		
		if (kinectV1 != null || kinectV2 != null) {
			out += "Kinect version " + getVersion() + " ouverte en " + width + " x " + height;
		} else {
			out += "Kinect non initialisée";
		}
		
		return out;
	}

	public int getVersion() {
		/*****************************************************
		 * 
		 * Retourne la version de la kinect utilisee
		 * 
		 *****************************************************/
		
		return version;
	}

	public void updateSkel_1() {
		/********************************************************
		 * 
		 * Mise a jour de l'avatar a partir de la kinect 1
		 * 
		 ********************************************************/
		
		
	}

	public void drawSkeletons() {
   	 	/***************************************************************
   	 	 * 
   	 	 *  Affiche les squelettes selon la kinect en cours
   	 	 * 
   	 	 ***************************************************************/
    	if(version == 1){ //Affichage kinect1
    		for (int i = 0; i < kinectV1.getNumberOfUsers(); i++) {
	    		//Change de couleur pour chaque squelette
    			int col  = getIndexColor(i);
	    		app.fill(col);
	    		app.stroke(col);
	    		
	    		drawSkeleton_1(i);
			}
    	} else if (version == 2) { //Affichage kienct2
    		for (int i = 0; i < skeletonsV2_ColorMap.length; i++) {
    	    	if (skeletonsV2_ColorMap[i].isTracked()) {
    	    		KJoint[] joints = skeletonsV2_ColorMap[i].getJoints();

    	    		int col  = getIndexColor(i);
    	    		app.fill(col);
    	    		app.stroke(col);
    	    		drawSkeleton_2(joints);
    	        
    	    		//draw different color for each hand state
    	    		drawHandState(joints[KinectPV2.JointType_HandRight]);
    	    		drawHandState(joints[KinectPV2.JointType_HandLeft]);
    	    	}
    	    }
    	}
	}
	
	private void drawSkeleton_1(int userId) {
		/**********************************************************
		 * 
		 * 			Methode de kinect1
		 * 		Affiche le squelette d'un user
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
	
	private void drawSkeleton_2(KJoint[] joints) {
   	 	/***************************************************************
   	 	 * 
   	 	 * 				Methode kienct2
   	 	 *  		Affiche le squelette
   	 	 * 
   	 	 ***************************************************************/
		
		drawBone(joints, KinectPV2.JointType_Head, KinectPV2.JointType_Neck);
		drawBone(joints, KinectPV2.JointType_Neck, KinectPV2.JointType_SpineShoulder);
		drawBone(joints, KinectPV2.JointType_SpineShoulder, KinectPV2.JointType_SpineMid);
		drawBone(joints, KinectPV2.JointType_SpineMid, KinectPV2.JointType_SpineBase);
		drawBone(joints, KinectPV2.JointType_SpineShoulder, KinectPV2.JointType_ShoulderRight);
		drawBone(joints, KinectPV2.JointType_SpineShoulder, KinectPV2.JointType_ShoulderLeft);
		drawBone(joints, KinectPV2.JointType_SpineBase, KinectPV2.JointType_HipRight);
		drawBone(joints, KinectPV2.JointType_SpineBase, KinectPV2.JointType_HipLeft);

		// Right Arm    
		drawBone(joints, KinectPV2.JointType_ShoulderRight, KinectPV2.JointType_ElbowRight);
		drawBone(joints, KinectPV2.JointType_ElbowRight, KinectPV2.JointType_WristRight);
		drawBone(joints, KinectPV2.JointType_WristRight, KinectPV2.JointType_HandRight);
		drawBone(joints, KinectPV2.JointType_HandRight, KinectPV2.JointType_HandTipRight);
		drawBone(joints, KinectPV2.JointType_WristRight, KinectPV2.JointType_ThumbRight);

		// Left Arm
		drawBone(joints, KinectPV2.JointType_ShoulderLeft, KinectPV2.JointType_ElbowLeft);
		drawBone(joints, KinectPV2.JointType_ElbowLeft, KinectPV2.JointType_WristLeft);
		drawBone(joints, KinectPV2.JointType_WristLeft, KinectPV2.JointType_HandLeft);
		drawBone(joints, KinectPV2.JointType_HandLeft, KinectPV2.JointType_HandTipLeft);
		drawBone(joints, KinectPV2.JointType_WristLeft, KinectPV2.JointType_ThumbLeft);

		// Right Leg
		drawBone(joints, KinectPV2.JointType_HipRight, KinectPV2.JointType_KneeRight);
		drawBone(joints, KinectPV2.JointType_KneeRight, KinectPV2.JointType_AnkleRight);
		drawBone(joints, KinectPV2.JointType_AnkleRight, KinectPV2.JointType_FootRight);

		// Left Leg
		drawBone(joints, KinectPV2.JointType_HipLeft, KinectPV2.JointType_KneeLeft);
		drawBone(joints, KinectPV2.JointType_KneeLeft, KinectPV2.JointType_AnkleLeft);
		drawBone(joints, KinectPV2.JointType_AnkleLeft, KinectPV2.JointType_FootLeft);

		drawJoint(joints, KinectPV2.JointType_HandTipLeft);
		drawJoint(joints, KinectPV2.JointType_HandTipRight);
		drawJoint(joints, KinectPV2.JointType_FootLeft);
		drawJoint(joints, KinectPV2.JointType_FootRight);

		drawJoint(joints, KinectPV2.JointType_ThumbLeft);
		drawJoint(joints, KinectPV2.JointType_ThumbRight);

		drawJoint(joints, KinectPV2.JointType_Head);
	}

	private void drawJoint(KJoint[] joints, int jointType) {
   	 	/***************************************************************
   	 	 * 
   	 	 *  affiche les joints
   	 	 * 
   	 	 ***************************************************************/
    	
		app.pushMatrix();
		app.translate(joints[jointType].getX(), joints[jointType].getY(), joints[jointType].getZ());
		app.ellipse(0, 0, 25, 25);
		app.popMatrix();
	}

	private void drawBone(KJoint[] joints, int jointType1, int jointType2) {
   	 	/***************************************************************
   	 	 * 
   	 	 *  affiche les os
   	 	 * 
   	 	 ***************************************************************/
    	
		app.pushMatrix();
		app.translate(joints[jointType1].getX(), joints[jointType1].getY(), joints[jointType1].getZ());
		app.ellipse(0, 0, 25, 25);
		app.popMatrix();
		app.line(joints[jointType1].getX(), joints[jointType1].getY(), joints[jointType1].getZ(), joints[jointType2].getX(), joints[jointType2].getY(), joints[jointType2].getZ());
	}

	private void drawHandState(KJoint joint) {
   	 	/***************************************************************
   	 	 * 
   	 	 *  affiche l'etat des mains
   	 	 * 
   	 	 ***************************************************************/
    	
		app.noStroke();
		handState(joint.getState());
		app.pushMatrix();
		app.translate(joint.getX(), joint.getY(), joint.getZ());
		app.ellipse(0, 0, 70, 70);
		app.popMatrix();
	}

	private void handState(int handState) {
   	 	/***************************************************************
   	 	 * 
   	 	 *  couleurs des differents etats
   	 	 * 
   	 	 ***************************************************************/
    	
		switch(handState) {
			case KinectPV2.HandState_Open:
				app.fill(0, 255, 0);
				break;
			case KinectPV2.HandState_Closed:
				app.fill(255, 0, 0);
				break;
			case KinectPV2.HandState_Lasso:
				app.fill(0, 0, 255);
				break;
			case KinectPV2.HandState_NotTracked:
				app.fill(255, 255, 255);
				break;
		}
	}
		
	private int getIndexColor(int index) {
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

	public void onNewUser(int userId) {
		app.println("onNewUser - userId: " + userId);
	  
		kinectV1.startTrackingSkeleton(userId);
	}

	public void onLostUser(int userId) {
		app.println("onLostUser - userId: " + userId);
	}
	
} //class