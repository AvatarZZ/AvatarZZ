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
			retour[i].mult(-64);	// necessaire sinon mouvements de trop faible amplitude
			/*retour[i].y *= -1;		// correction d'orientation
			retour[i].z *= -1;		// correction d'orientation
			retour[i].x *= -1;		// effet non miroir*/
			//retour[i].z += 400;		// correction de proximité
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
