protected ZZModel clone;
protected boolean debug;

//------ déclaration des variables de couleur utiles ----
int jaune=color(255,255,0);
int vert=color(0,255,0);
int rouge=color(255,0,0);
int bleu=color(0,0,255);
int noir=color(0,0,0);
int blanc=color(255,255,255);
int bleuclair=color(0,255,255);
int violet=color(255,0,255);

//---------- angles pour 3D ----
int angleX=0; // angle X pour tracé de forme
int angleY=0; // angle Y pour tracé de forme
int angleZ=0; // angle Z pour tracé de forme

int distanceCamXZ=400; // variable distance à la caméra dans plan XZ
int distanceCamYZ=0; // variable distance à la caméra dans plan YZ

int angleCamXZ=90; // angle dans le plan XZ de la visée de la caméra avec l'axe des X dans le plan XZ
int angleCamYZ=90; // angle avec axe YZ de la visée de la caméra dans le plan YZ

int vTest = 0; float angleMouv = PI/(2*60); // utile aux tests de mouvement

public void setup() {
    // fenêtre
    size(1280, 960, P3D);
    // limitation du rafraichissement
    //frameRate(25);
    
    debug = false;
    
    clone = new ZZModel(this);
    clone.load("man.obj");

    clone.scale(64);
    clone.rotateZ(PI);
    clone.rotateY(PI);
    clone.translate(0, -1, 0);
    
    // rotation du bras droit
    clone.rotatePart(6, PI/2, PI/2);
}
  
public void draw() {    
    // effacer écran
    background(100);
    lights();
    
    if(debug) { // deboggage
      debugTools();
    }
    
    vision();

    stroke(255, 0, 0);

    //Test pour faire tourner la tete par la méthode du "face par face" (basique)
/*      for (int j = 0; j < clone.getChild("HEAD").getChildCount() ; j++) {
      for (int i = 0; i < clone.getChild("HEAD").getChild(j).getVertexCount(); i++) {
        ZZector tmp = new ZZector(clone.getChild("HEAD").getChild(j).getVertex(i));
        tmp.rotate(0,(float) 0.1);
        clone.getChild("HEAD").getChild(j).setVertex(i, tmp);
      }
    }
*/
    
    // test de mouvement
    if(vTest<60) {
      vTest++;
    } else {
      angleMouv = -angleMouv;
      vTest = 0;
    }
    
    clone.rotatePart(ZZkeleton.HEAD, 0, -angleMouv/2);
    clone.rotatePart(ZZkeleton.ELBOW_LEFT, 0, angleMouv);
    clone.rotatePart(ZZkeleton.KNEE_RIGHT, 0, 0, angleMouv);
    
    //Afficher le clone
    clone.draw();
    //Afficher le centre de la scène
    sphere(5);
}
  
public void vision() { // comportement "spécial"
    //Modifie la camera afin de voir convenablement le modele
    translate(width/2, height, 0 ); // décale origine dessin 
    
    camera(distanceCamXZ*cos(radians(angleCamXZ)), 0, distanceCamXZ*sin(radians(angleCamXZ)), 0, 0, 0,0,1,0); // angle de vue de la scène 3D

      rotateX(radians(angleX)); // rotation de angleX ° autour de l'axe des X (horizontal) par rapport référence dessin
      rotateY(radians(angleY)); // rotation autour de l'axe des y (vertical) par rapport référence dessin
      rotateZ(radians(angleZ)); // rotation autour de l'axe des z (avant-arriere) par rapport référence dessin
}
  
public void debugTools() {
    text(frameRate, 50, 50);
    
    stroke(jaune);
    // box(width, height, depth);
    box(200, 5, 200);
    
    // affiche le repère Ox,Oy,Oz
    
    //---- Ox
    stroke(rouge);
    line (0,0,0,150,0,0);
    
    // --- Oy
    stroke(vert);
    line (0,0,0,0,-150,0);
    
    // --- Oz
    stroke(bleu);
    line (0,0,0,0,0,-150); 
    
    text(clone.getChildCount(), 100, -100);
}

//si touche pressée
public void keyPressed() {
    switch(key) { // ou keyCode 
      case 'd' : 
        debug = !debug;
            break;
      case '8' : 
        angleCamXZ=angleCamXZ+5;
            break;
      case '2' : 
          angleCamXZ=angleCamXZ-5;
              break;
      case '+' : 
        distanceCamXZ=distanceCamXZ-5;
              break;
      case '-' : 
        distanceCamXZ=distanceCamXZ+5;
              break;
      case CODED :
        if (keyCode == UP) { // si touche Haut appuyée
                    angleX=angleX+5;
                }
                else if (keyCode == DOWN) {// si touche BAS appuyée
                  angleX=angleX-5;
                }
                else if (keyCode == LEFT) {// si touche GAUCHE appuyée
                  angleZ=angleZ+5;
                }
                else if (keyCode == RIGHT) {// si touche GAUCHE appuyée
                  angleZ=angleZ-5;
                }
        break;
    }
}

  
  
// si clic
public void mousePressed()  {
    
}

// si déclic
public void mouseReleased() {
  
}

// si clic maintenu
public void mouseDragged() {
    
}

// si souris bougé
public void mouseMoved() {
    
}
