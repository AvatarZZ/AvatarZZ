package zzavatar;

import java.util.ArrayList;
import java.util.Vector;

import KinectPV2.KJoint;
import KinectPV2.KinectPV2;
import KinectPV2.Skeleton;
import SimpleOpenNI.*;
import processing.core.*;

/********************************************
 * 
 * Classe de gestion de la kinect
 *
 ********************************************/
public interface ZZkinect {
	static final int SKELETON_SIZE = 25;		// nombre de joints dans un squelette
	
	public default ZZoint[] getSkeleton() {
   	 	/***************************************************************
   	 	 * 
   	 	 *  permet de recuperer le squelette d'un certain utilisateur
   	 	 * 
   	 	 ***************************************************************/
    
		return getSkeleton(0);
	}
	
	public ZZoint[] getSkeleton(int numUser);		// permet de récupérer le squelette

	public boolean isTrackingSkeleton(int skelNum); // permet de savoir si le squelette skelNum est traque
	
	public int[] getUsers();			// renvoie la liste des utilisateurs actifs

	public void refresh();				// permet de mettre a jour les champs de ZZkinect

	public boolean available(); 		// permet de savoir si la kinect est disponible
	
	@Override
	public default String toString() {
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

	public int getWidth();

	public int getHeight();

	public int getVersion();	// Retourne la version de la kinect utilisee

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
		
	default int getIndexColor(int index) {
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
	
} //class