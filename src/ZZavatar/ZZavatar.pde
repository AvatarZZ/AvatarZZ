  import processing.core.PImage;
  import processing.core.PApplet;
  import SimpleOpenNI.*;
  
  protected ZZModel clone;
  protected ArrayList<ZZModel> avatars;
  protected boolean debug;
  protected ZZkinect kinect;

  //------ déclaration des variables de couleur utiles ----
  int jaune=color(255,255,0);
  int vert=color(0,255,0);
  int rouge=color(255,0,0);
  int bleu=color(0,0,255);
  int noir=color(0,0,0);
  int blanc=color(255,255,255);
  int bleuclair=color(0,255,255);
  int violet=color(255,0,255);
  
  int widthWindow = 1280;  //largeur de la fenetre principale
  int heightWindow = 960;  //hauteur de la fenetre principale
  
  int distanceCamXZ=400; // variable distance à la caméra dans plan XZ
  int distanceCamYZ=0; // variable distance à la caméra dans plan YZ

  int angleCamXZ=270; // angle dans le plan XZ de la visée de la caméra avec l'axe des X dans le plan XZ
  int angleCamYZ=90; // angle avec axe YZ de la visée de la caméra dans le plan YZ
  
  int vTest = 0; float angleMouv = PI/(2*60); // utile aux tests de mouvement

  void setup() {
      size(widthWindow, heightWindow, P3D);        //ouverture de la fenetre en P3D
      frame.setTitle("Project Kinect");  //modification du titre de la frame
      //frameRate(25);          //limitation du rafraichissement
      
      debug = false;  //options de debug
      
      //initialisation de la kinect
      kinect = new ZZkinect(this);
      
      //chargement des modeles a partir de la liste
      avatars = ZZModel.loadModels(this, "./data/avatars.bdd");

      //recuperation du premier clone pour affichage
      clone = avatars.get(0);
      
      //Orientation et echelle du modele et rotation de l'avant bras bras droit
      for (int i = 0; i < avatars.size(); i++) {
        avatars.get(i).scale(64);
        avatars.get(i).rotatePart(ZZkeleton.WRIST_LEFT, 0, PI/2);
    }
      
      /*clone.rotateZ(PI);
      clone.rotateY(PI);
      clone.translate(0, -1, 0);*/
      
      //Initialisation du bras le long du corp (ou presque)
      //clone.rotatePart(ZZkeleton.ELBOW_LEFT, PI/2, 0);
  }
    
  void draw() {
      background(100);  //efface l'ecran
      lights();      //ajout de lumiere
      
      if(debug) {debugTools();} //outils de debug
      
      if (kinect.available()) { //si la kinect est presente
          kinect.refresh();  //mise a jour de la kinect
      
          image(kinect.rgbImage, 0, 0);  //affiche l'image couleur en haut a gauche
          image(kinect.depthImage, widthWindow-kinect.width,0);  //affiche la profondeur en haut a droite
      }
      
      vision();

      stroke(255, 0, 0);
      
      // test de mouvement
      if(vTest<60) {
          vTest++;
      } else {
          angleMouv = -angleMouv;
          vTest = 0;
      }
      
      clone.rotatePart(ZZkeleton.HEAD, 0, -angleMouv/2);
      clone.rotatePart(ZZkeleton.ELBOW_LEFT, 0, angleMouv, 0);
      //clone.rotatePart(ZZkeleton.KNEE_RIGHT, 0, 0, angleMouv);
      
      //Afficher le clone
      clone.draw();
      //Afficher le centre de la scène
      sphere(5);
  }
    
  public void vision() { // comportement "spécial"
      //Modifie la camera afin de voir convenablement le modele
      translate(width/2, height, 0 ); // décale origine dessin 
      
      camera(distanceCamXZ*cos(radians(angleCamXZ)), distanceCamXZ*sin(radians(angleCamYZ)), distanceCamXZ*sin(radians(angleCamXZ)), 0, 0, 0,0,-1,0); // angle de vue de la scène 3D
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
      debugSkeleton(clone.skeleton);
  }

    public void debugSkeleton(ZZkeleton sk) {
      for (int i = 0; i < sk.joints.length; i++) {
      pushMatrix();
      stroke(rouge);
      translate(64*sk.joints[i].x, 64*sk.joints[i].y, 64*sk.joints[i].z);
      sphere(20);
      popMatrix();
    }
    }

  //si touche pressée
  public void keyPressed() {
      switch(key) { // ou keyCode 
        case 'd' :   // passer en mode debug
            debug = !debug;
              break;
        case 's' :  // changer d'avatar
            int suiv = avatars.indexOf(clone)+1;
            suiv = suiv >= avatars.size() ? 0 : suiv;
            clone = avatars.get(suiv);
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
                angleCamYZ=angleCamYZ+5;
                  } else if (keyCode == DOWN) {// si touche BAS appuyée
                    angleCamYZ=angleCamYZ-5;
                  } else if (keyCode == LEFT) {// si touche GAUCHE appuyée
                    angleCamXZ=angleCamXZ+5;
                  } else if (keyCode == RIGHT) {// si touche DROITE appuyée
                    angleCamXZ=angleCamXZ-5;
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

  

