package zzavatar;

import java.util.Vector;

import KinectPV2.KJoint;
import KinectPV2.KinectPV2;
import KinectPV2.Skeleton;
import SimpleOpenNI.SimpleOpenNI;
import SimpleOpenNI.SimpleOpenNIConstants;
import processing.core.*;

/********************************************
 * 
 * Classe de gestion de la kinect
 *
 ********************************************/
public class ZZkinect {
	protected SimpleOpenNI kinectV1;
	protected KinectPV2 kinectV2;
	protected int height = 0;	// hauteur de la capture
	protected int width = 0;	// largeur de la capture
	protected int version = 0;	// version de la Kinect utilisee
	
	protected PImage rgbImage;		//capture video normale
	protected PImage depthImage;	//capture de profondeur
	protected Skeleton [] skeletonsV2;	//
	
	public ZZkinect(PApplet parent) {
   	 	/***************************************************************
   	 	 * 
   	 	 *  constructeur de ZZkinect
   	 	 * 
   	 	 ***************************************************************/
    	
		try {
			// kinect = new SimpleOpenNI(parent); // ne marche pas sous eclipse
			
			if(!kinectV1.isInit()){ //kinect reconnue ou pas
				PApplet.println("Kinect non reconnue ou non presente");
				kinectV1 = null;
			} else { //kinect reconnue
				kinectV1.enableDepth();	//chargement de la profondeur
				kinectV1.enableRGB();	//chargement de l'image couleur
				kinectV1.enableUser();	//autoriser le tracking du squelette des utilisateurs
				
				height = kinectV1.depthHeight();	//hauteur de la capture
				width = kinectV1.depthWidth();		//largeur de la capture
				
				version = 1;	// actualisation de la version
				
				if ((width != 640) || (height != 480)) { //reconnaissance des parametres
					PApplet.println("Erreur sur les dimension de capture kinect");
					kinectV1 = null;
				}
			}
		} catch (Exception e) {
			kinectV2 = new KinectPV2(parent);

			kinectV2.enableColorImg(true);
			kinectV2.enableDepthImg(true);
			kinectV2.enableSkeleton(true);
			kinectV2.enableBodyTrackImg(true);
			kinectV2.enableSkeleton3dMap(true);
			kinectV2.init();
			height = kinectV2.HEIGHTColor;
			width = kinectV2.WIDTHColor;
			
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
			retour[i].mult(256);	// necessaire sinon mouvements de trop faible amplitude
			retour[i].y *= -1;		// correction d'orientation
			retour[i].z *= -1;		// correction d'orientation
			retour[i].z += 800;		// correction de proximité
		}
    	
		return retour;
	}

	private ZZoint[] getSkeleton_1(int numUser) {
   	 	/***************************************************************
   	 	 * 
   	 	 *  permet de recuperer le squelette (version 2)
   	 	 * 
   	 	 ***************************************************************/
		
		return null;
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
		
		rgbImage = kinectV1.rgbImage();		// mise a jour de l'image couleur
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
		
		//Vecteur contenant les membres a traiter
		
		
		//Ajout des membres dans le vector
		
		//Traitement des angles uns par uns
		for (int i = 0; i < membres.length; i++) {
			SimpleOpenNIConstants cur = membres[i];
			
			angle = angleWith(cur);
			
		}
		
	}
	
} //class
