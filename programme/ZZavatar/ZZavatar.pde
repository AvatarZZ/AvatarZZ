/***************************************************/
/*  BARBESANGE Benjamin & GARCON Benoît     ISIMA  */
/*                                                 */
/*                 Projet ISIMA 1                  */
/*          Contrôle d'avatar par Kinect           */
/*                                                 */
/*  ZZavatar.pde                 Processing 2.2.1  */
/***************************************************/

// importation des librairies utiles
import KinectPV2.*;

// déclaration de variables globales ... penser à créer des objets
boolean debug;
KinectPV2 kinect;  // représente le capteur kinect
Skeleton [] squelette;  // permettra de contenir les données des utilisateurs (6 maximum)

    
// fonction d'initialisation
void setup() {
  // fenêtre
  size(1280, 960, P3D);
  
  // kinect
  kinect = new KinectPV2(this);
  kinect.enableDepthMaskImg(true);
  kinect.enableSkeleton(true );
  kinect.enableSkeletonDepthMap(true);
  kinect.init();
  
  // squelette
  squelette = new Skeleton[6];
  
  // debug
  debug = false;
}

// fonction principale répétée en boucle
void draw() {
  // effacer écran
  background(0);
  
  // acquisition des informations
  squelette = kinect.getSkeletonDepthMap();
  
  // traitement
  for (int i = 0; i < squelette.length; i++) {
    if (squelette[i].isTracked()) {
      KJoint[] joints = squelette[i].getJoints();
    }
  }
  
  if(debug) { // deboggage
    text(frameRate, 50, 50);
  }  
}

// si touche pressée
void keyPressed() 
{
    switch(key) // ou keyCode 
    { 
        case 'd' : 
            debug = !debug;
            break;
    }
}

// si clic
void mousePressed()
{
    
}

// si déclic
void mouseReleased()
{
  
}

// si clic maintenu
void mouseDragged()
{
    
}

// si souris bougé
void mouseMoved()
{
    
}
