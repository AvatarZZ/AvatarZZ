import SimpleOpenNI.*;
import processing.core.PImage;
import processing.core.PApplet;

/********************************************
 * 
 * Classe de gestion de la kinect
 *
 ********************************************/
public interface ZZkinect {
	static final int SKELETON_SIZE = 25;		// nombre de joints dans un squelette
	
	public ZZoint[] getSkeleton();
	
	public ZZoint[] getSkeleton(int numUser);		// permet de r�cup�rer le squelette

	public boolean isTrackingSkeleton(int skelNum); // permet de savoir si le squelette skelNum est traque
	
	public int[] getUsers();			// renvoie la liste des utilisateurs actifs

	public void refresh();				// permet de mettre a jour les champs de ZZkinect

	public boolean available(); 		// permet de savoir si la kinect est disponible
	
	public String toString();	// toString permettant l'obtention d'informations sur la Kinect
   	 	
	public int getWidth();

	public int getHeight();

	public int getVersion();	// Retourne la version de la kinect utilisee

	public void drawSkeletons();// Affiche les squelettes selon la kinect en cours
   	 	
	public void drawSkeleton(int userId);	// Affiche le squelette d'un user
		
	public int getIndexColor(int index);	// couleurs des squelettes des joueurs

	public PImage getRGBImage();	// retourne l'image couleur de la kinect active

	public ZZoint getJoinedHands();	// retourne la position des deux mains jointes sinon null
	
} //class
