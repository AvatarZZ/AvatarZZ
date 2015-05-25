package zzavatar;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PImage;
import KinectPV2.KJoint;
import KinectPV2.KinectPV2;
import KinectPV2.Skeleton;

class ZZkinectV2 implements ZZkinect {
	protected KinectPV2 kinectV2;
	protected PApplet app;
	protected int height = 0;	// hauteur de la capture
	protected int width = 0;	// largeur de la capture
	protected int version = 0;	// version de la Kinect utilisee
	
	protected PImage rgbImage;		// capture video normale
	protected PImage depthImage;	// capture de profondeur
	private Skeleton [] skeletonsV2;
	protected Skeleton [] skeletonsV2_ColorMap;
	
	public ZZkinectV2(PApplet parent) {
   	 	/***************************************************************
   	 	 * 
   	 	 *  constructeur de ZZkinect
   	 	 * 
   	 	 ***************************************************************/

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
	
	@Override
	public ZZoint[] getSkeleton(int numUser) {
		/***************************************************************
   	 	 * 
   	 	 *  permet de recuperer le squelette (version 2)
   	 	 * 
   	 	 ***************************************************************/
    	
		ZZoint[] retour = new ZZoint[25];
    	KJoint[] k = skeletonsV2[numUser].getJoints();
		
		for (int i = 0; i < retour.length; i++) {
			retour[i] = new ZZoint(k[i]);
			retour[i].mult(-256);	// necessaire sinon mouvements de trop faible amplitude
			/*retour[i].y *= -1;		// correction d'orientation
			retour[i].z *= -1;		// correction d'orientation
			retour[i].x *= -1;		// effet non miroir*/
			retour[i].z += 500;		// correction de proximité
		}
    	
		return retour;
	}

	@Override
	public boolean isTrackingSkeleton(int skelNum) {
		/***************************************************************
   	 	 * 
   	 	 *  permet de savoir si le squelette skelNum est traque
   	 	 * 
   	 	 ***************************************************************/
    
		return skeletonsV2[skelNum].isTracked();
	}
	
	@Override
	public int[] getUsers() {
		/***************************************************************
   	 	 * 
   	 	 *	renvoie la liste des utilisateurs actifs
   	 	 * 
   	 	 ***************************************************************/
    	
		int [] retour = null;
		ArrayList<Integer> tmp = new ArrayList<Integer>();
		
		for (int i = 0; i < skeletonsV2.length; i++) {
			if(this.isTrackingSkeleton(i))
		    {
				tmp.add(i);
		    }
		}
		retour = new int[tmp.size()];
		for (int i = 0; i < tmp.size(); i++)
			retour[i] = tmp.get(i);
		
		return retour;
	}
	
	@Override
	public void refresh() {
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
	
	@Override
	public boolean available() {
   	 	/***************************************************************
   	 	 * 
   	 	 *  available pour Kinect V2
   	 	 * 
   	 	 ***************************************************************/
    	
        return kinectV2 != null;
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
   	 	/***************************************************************
   	 	 * 
   	 	 *  Affiche les squelettes selon la kinect en cours
   	 	 * 
   	 	 ***************************************************************/

		for (int i = 0; i < skeletonsV2_ColorMap.length; i++) {
	    	if (skeletonsV2_ColorMap[i].isTracked()) {

	    		int col  = getIndexColor(i);
	    		app.fill(col);
	    		app.stroke(col);
	    		drawSkeleton(i);
	    	}
	    }
	}
	
	@Override
	public void drawSkeleton(int userId) {
   	 	/***************************************************************
   	 	 * 
   	 	 *	Affiche le squelette
   	 	 * 
   	 	 ***************************************************************/
		
		KJoint[] joints = skeletonsV2_ColorMap[userId].getJoints();
		
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
		
		//draw different color for each hand state
		drawHandState(joints[KinectPV2.JointType_HandRight]);
		drawHandState(joints[KinectPV2.JointType_HandLeft]);
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
   	 	/***************************************************************
   	 	 * 
   	 	 *  renvoie la largeur de la capture Kinect
   	 	 * 
   	 	 ***************************************************************/
    	
		return width;
	}

	@Override
	public int getHeight() {
   	 	/***************************************************************
   	 	 * 
   	 	 *  renvoie la hauteur de la capture Kinect
   	 	 * 
   	 	 ***************************************************************/
    	
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

	@Override
	public ZZoint getJoinedHands() {
		// TODO Auto-generated method stub
		return null;
	}
}
