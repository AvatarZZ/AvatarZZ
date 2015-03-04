import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.ArrayList; 
import java.util.HashSet; 
import java.util.Iterator; 
import java.util.Set; 
import java.lang.Enum; 
import processing.core.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class ProjectKinect_pde extends PApplet {








// import KinectPV2.*;


  protected PShape perso;
  protected ZZModel clone;
//  protected KinectPV2 kinect;
//  Skeleton [] squelette;
//  KJoint [] oldJoints;
  protected boolean debug;
  
  //------ d\u00e9claration des variables de couleur utiles ----
  int jaune=color(255,255,0);
  int vert=color(0,255,0);
  int rouge=color(255,0,0);
  int bleu=color(0,0,255);
  int noir=color(0,0,0);
  int blanc=color(255,255,255);
  int bleuclair=color(0,255,255);
  int violet=color(255,0,255);

  //---------- angles pour 3D ----
  int angleX=0; // angle X pour trac\u00e9 de forme
  int angleY=0; // angle Y pour trac\u00e9 de forme
  int angleZ=0; // angle Z pour trac\u00e9 de forme

  int distanceCamXZ=400; // variable distance \u00e0 la cam\u00e9ra dans plan XZ
  int distanceCamYZ=0; // variable distance \u00e0 la cam\u00e9ra dans plan YZ

  int angleCamXZ=90; // angle dans le plan XZ de la vis\u00e9e de la cam\u00e9ra avec l'axe des X dans le plan XZ
  int angleCamYZ=90; // angle avec axe YZ de la vis\u00e9e de la cam\u00e9ra dans le plan YZ
  
  public void setup() {
    // fen\u00eatre
    size(1280, 960, P3D);
    // limitation du rafraichissement
    //frameRate(25);
    
    debug = false;
    
    clone = new ZZModel();
    clone.load("man.obj");

    clone.scale(64);
    clone.rotateZ(PI);
    clone.rotateY(PI);
    clone.translate(0, -1, 0);
    
    // squelette
//    squelette = new Skeleton[6];
//    oldJoints = new KJoint[25];
    
     //rotation du bras droit
    clone.rotatePart(6, PI/2, PI/2);
  }
  
  public void draw() {
//    KJoint[] joints;
    
    // effacer \u00e9cran
    background(100);
    lights();
    
    if(debug) { // deboggage
      debugTools();
    }
    
    vision();

    stroke(255, 0, 0);

   //Test pour faire tourner la tete
      for (int j = 0; j < clone.getChild("HEAD").getChildCount() ; j++) {
        for (int i = 0; i < clone.getChild("HEAD").getChild(j).getVertexCount(); i++) {
          ZZector tmp = new ZZector(clone.getChild("HEAD").getChild(j).getVertex(i));
          tmp.rotate(0,(float) 0.1f);
          clone.getChild("HEAD").getChild(j).setVertex(i, tmp);
        }
      }
    
    //Afficher le clone
    clone.draw();
    //Afficher le center
    sphere(5);
  }
  
  public void vision() {
    //Modifie la camera afin de voir convenablement le modele
    translate(width/2, height, 0 ); // d\u00e9cale origine dessin 
    
    camera(distanceCamXZ*cos(radians(angleCamXZ)), 0, distanceCamXZ*sin(radians(angleCamXZ)), 0, 0, 0,0,1,0); // angle de vue de la sc\u00e8ne 3D

        rotateX(radians(angleX)); // rotation de angleX \u00b0 autour de l'axe des X (horizontal) par rapport r\u00e9f\u00e9rence dessin
        rotateY(radians(0)); // rotation autour de l'axe des y (vertical) par rapport r\u00e9f\u00e9rence dessin
        rotateZ(radians(angleZ)); // rotation autour de l'axe des z (avant-arriere) par rapport r\u00e9f\u00e9rence dessin
     
  }
  
  public void debugTools() {
    text(frameRate, 50, 50);
    
        stroke(jaune);
        // box(width, height, depth);
        box(200, 5, 200);


        // while(true); // stoppe boucle draw

        // affiche le rep\u00e8re Ox,Oy,Oz

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

  //si touche press\u00e9e
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
      case CODED :
        if (keyCode == UP) { // si touche Haut appuy\u00e9e
                    angleX=angleX+5;
                }
                else if (keyCode == DOWN) {// si touche BAS appuy\u00e9e
                 angleX=angleX-5;
                }

                else if (keyCode == LEFT) {// si touche GAUCHE appuy\u00e9e

                 angleZ=angleZ+5;

                }
                else if (keyCode == RIGHT) {// si touche GAUCHE appuy\u00e9e

                 angleZ=angleZ-5;

                }
        break;
    }
  }

  
  
  // si clic
  public // si clic
  void mousePressed()  {
      
  }

  // si d\u00e9clic
  public void mouseReleased() {
    
  }

  // si clic maintenu
  public void mouseDragged() {
      
  }

  // si souris boug\u00e9
  public void mouseMoved() {
      
  }
  
  class ZZModel {

    protected PShape model;
    protected ZZkeleton skeleton;
    ArrayList<ZZertex> vertices;
    ArrayList<Integer>[] groups;

    protected ZZModel() {
      model = createShape(GROUP);
      skeleton = new ZZkeleton();
      vertices = new ArrayList<ZZertex>();
      groups = new ArrayList[25];
    }

    protected ZZModel(String filename) {
      this();
      this.load(filename);
    }
    
    public void load(String filename) {
      /***************************************************************
       * 
       * permet le chargement correct d'un .obj en g\u00e9rant les groupes
       * 
       ***************************************************************/
      
      String[] file;
      PShape currentShape;
      int [] counter = new int[3];
      
      counter[2] = -1;
      
      for (int i = 0; i < groups.length; i++) {
        groups[i] = new ArrayList<Integer>();
      }
      
      if(!(filename.contains(".obj"))) {
        println("Chargement du mod\u00e8le : attention, il se peut que " + filename + " soit incompatible");
      }
      
      file = loadStrings(filename);
      currentShape = model;
      
      if(file != null) {
        for(int i = 0 ; i < file.length; i++) {
          if(file[i].contains("v ")) {
            float[] line = parseFloat(file[i].substring(2).split(" "));
            vertices.add(new ZZertex(line[0], line[1], line[2]));
          } else if(file[i].contains("vt ")) {
            
          } else if(file[i].contains("f ")) {
            String[] tmp = file[i].substring(2).split(" ");
            if(counter[0]==22){println(file[i]);}
            PShape nouv = createShape();
            int [] c = new int[3];

            nouv.beginShape();
            noStroke();
            for (int lol = 0 ; lol < 3 ; lol++)
              nouv.vertex(0,0,0);
            nouv.endShape(CLOSE);
            
            for (int j = 0; j < c.length; j++) {
              c[j] = parseInt(tmp[j].split("/")[0])-1;
              nouv.setVertex(j, vertices.get(c[j]));
              vertices.get(c[j]).addOccurence(counter[2], counter[1], j);
              groups[counter[0]].add(c[j]);
            }
            
            currentShape.addChild(nouv);
            counter[1]++;
          } else if(file[i].contains("o ")) {
            
          } else if(file[i].contains("g ")) {
            currentShape = createShape(GROUP);
            println("Nouveau groupe : " + file[i].split(" ")[1]);
            currentShape.setName(file[i].split(" ")[1]);
            model.addChild(currentShape);
            counter[0] = skeleton.getTypeCode(currentShape.getName());
            counter[1] = 0;
            counter[2]++;
          }
        }
        skeleton.load("skeleton.sk");
        println("Chargement du mod\u00e8le : termin\u00e9");
      } else {
        println("Chargement du mod\u00e8le : erreur \u00e0 l'ouverture du fichier " + filename);
      }
    }
    
//    public void move(Skeleton sklKin) {
      /***************************************************************
       * 
       *  algorithme principal d'animation du mod\u00e8le
       * 
       ***************************************************************/

      // d\u00e9claration de variables
      
      
      // calcul de la translation g\u00e9n\u00e9rale et des rotations locales 
      

      // applications des transformations
/*      for (int i = 0; i < model.getChildCount(); i++) {
        for (int j = 0; j < model.getChild(i).getChildCount(); j++) {
          for (int k = 0; k < model.getChild(i).getChildCount(); k++) {
            
          }
        }
      }

      // mise \u00e0 jour des donn\u00e9es
      skeleton.update(sklKin);
    }
*/
    public void draw() {
      /***************************************************************
       * 
       * affiche simplement le mod\u00e8le
       * 
       ***************************************************************/
      
      pushMatrix();
        shape(model);
        popMatrix();
    }
    
    public int getChildCount() {
      /***************************************************************
       * 
       * donne le nombre de groupes enfant
       * 
       ***************************************************************/
      return model.getChildCount();
    }
    
    public void scale(float s) {
      /***************************************************************
       * 
       * change la taille du mod\u00e8le
       * 
       ***************************************************************/
      
      model.scale(s);
    }
    
    public void rotateX(float angle) {
      /***************************************************************
       * 
       * rotation du mod\u00e8le autour de l'axe X
       * 
       ***************************************************************/
      
      model.rotateX(angle);
    }
    
    public void rotatePart(int part, float theta, float phi) {
      /***************************************************************
       * 
       *  fait tourner tout une partie du mod\u00e8le
       * 
       ***************************************************************/
      
      ArrayList<Integer> jts = partWithChildren(part);
      HashSet<Integer> vtcs = new HashSet<Integer>();
      PVector center = skeleton.joints[skeleton.joints[part].getParent()];
      
      for (int i = 0; i < jts.size(); i++) {
        vtcs.addAll(groups[jts.get(i)]);
      }
      for (Iterator iterator = vtcs.iterator(); iterator.hasNext();) {
        Integer integer = (Integer) iterator.next();
        vertices.get(integer).rotateAround(center, theta, phi);
        vertices.get(integer).apply(model);
      }
    }
    
    protected ArrayList<Integer> partWithChildren(int part) {
      /***************************************************************
       * 
       *  retourne la liste des vertices d'une partie et de ses enfants
       * 
       ***************************************************************/
            
      return skeleton.getMember(part);
    }
    
    public void rotateY(float angle) {
      /***************************************************************
       * 
       *  rotation du mod\u00e8le autour de l'axe Y
       * 
       ***************************************************************/
      
      model.rotateY(angle);
    }
    
    public void rotateZ(float angle) {
      /***************************************************************
       * 
       * rotation du mod\u00e8le autour de l'axe Z
       * 
       ***************************************************************/
      
      model.rotateZ(angle);
    }
    
    public void translate(float x, float y, float z) {
      /***************************************************************
       * 
       * translation du mod\u00e8le
       * 
       ***************************************************************/
      
      model.translate(x, y, z);
    }
    
    public PShape getChild(String target) {
      /***************************************************************
       * 
       * retourne le sous groupe target du mod\u00e8le
       * 
       ***************************************************************/
      
      return model.getChild(target);
    }
    
    public PShape[] getChildren() {
      /***************************************************************
       * 
       * retourne le sous groupe target du mod\u00e8le
       * 
       ***************************************************************/
      
      return model.getChildren();
    }
    
    public int getVertexCount() {
      /***************************************************************
       * 
       * retourne le sous groupe target du mod\u00e8le
       * 
       ***************************************************************/
      
      return model.getVertexCount();
    }
    
  }
  
  class ZZkeleton {
    public final static int WAIST      = 0;
    public final static int ROOT      = 1;
    public final static int NECK      = 2;
    public final static int HEAD      = 3;
    public final static int SHOULDER_LEFT  = 4;
    public final static int ELBOW_LEFT    = 5;
    public final static int WRIST_LEFT      = 6;
    public final static int HAND_LEFT    = 7;
    public final static int SHOULDER_RIGHT  = 8;
    public final static int ELBOW_RIGHT    = 9;
    public final static int WRIST_RIGHT    = 10;
    public final static int HAND_RIGHT    = 11;
    public final static int HIP_LEFT    = 12;
    public final static int KNEE_LEFT    = 13;
    public final static int ANKLE_LEFT    = 14;
    public final static int FOOT_LEFT    = 15;
    public final static int HIP_RIGHT    = 16;
    public final static int KNEE_RIGHT    = 17;
    public final static int ANKLE_RIGHT    = 18;
    public final static int FOOT_RIGHT    = 19;
    public final static int TORSO      = 20;
    public final static int INDEX_LEFT    = 21;
    public final static int THUMB_LEFT    = 22;
    public final static int INDEX_RIGHT    = 23;
    public final static int THUMB_RIGHT    = 24;
    
    protected ZZoint[] joints;
    protected int jointsNumber;
    protected String name;
    
    public ZZkeleton() {
      jointsNumber = 25;
      joints = new ZZoint[jointsNumber];
      name = "default";
    }
    
    public ZZkeleton(String filename) {
      this();
      this.load(filename);
    }
    
     protected int getTypeCode(String type) {
      /***************************************************************
       * 
       * matching entre les valeurs des fichiers et du code
       * 
       ***************************************************************/
      
      int code = -1;

      if(type.equals("WAIST")) code = 0;
      else if(type.equals("ROOT")) code = 1;
      else if(type.equals("NECK")) code = 2;
      else if(type.equals("HEAD")) code = 3;
      else if(type.equals("SHOULDER_LEFT")) code = 4;
      else if(type.equals("ELBOW_LEFT")) code = 5;
      else if(type.equals("WRIST_LEFT")) code = 6;
      else if(type.equals("HAND_LEFT")) code = 7;
      else if(type.equals("SHOULDER_RIGHT")) code = 8;
      else if(type.equals("ELBOW_RIGHT")) code = 9;
      else if(type.equals("WRIST_RIGHT")) code = 10;
      else if(type.equals("HAND_RIGHT")) code = 11;
      else if(type.equals("HIP_LEFT")) code = 12;
      else if(type.equals("KNEE_LEFT")) code = 13;
      else if(type.equals("ANKLE_LEFT")) code = 14;
      else if(type.equals("FOOT_LEFT")) code = 15;
      else if(type.equals("HIP_RIGHT")) code = 16;
      else if(type.equals("KNEE_RIGHT")) code = 17;
      else if(type.equals("ANKLE_RIGHT")) code = 18;
      else if(type.equals("FOOT_RIGHT")) code = 19;
      else if(type.equals("TORSO")) code = 20;
      else if(type.equals("INDEX_LEFT")) code = 21;
      else if(type.equals("THUMB_LEFT")) code = 22;
      else if(type.equals("INDEX_RIGHT")) code = 23;
      else if(type.equals("THUMB_RIGHT")) code = 24;
      else if(type.equals("(null)")) code = WAIST;
      
      return code;
    }
  
    protected int[] getTypeCode(String[] type) {
      /***************************************************************
       * 
       * matching entre les valeurs des fichiers et du code
       * 
       ***************************************************************/
      
      int [] retour = new int[type.length];
      
      for (int i = 0; i < retour.length; i++) {
        retour[i] = getTypeCode(type[i]);
      }
      
      return retour;
    }
     
     public void load(String filename) {
      /***************************************************************
       * 
       * charge le squelette \u00e0 partir d'un fichier .sk
       * 
       ***************************************************************/
      
      String[] file;
      
      if(!(filename.contains(".sk"))) {
        println("Chargement du squelette : attention, il se peut que " + filename + " soit incompatible");
      }
      
      file = loadStrings(filename);
      
      if(file != null) {
        int index = 0;
        while (index < file.length) {
          if (file[index].contains("sk ")) {
            name = file[index].substring(3);
          }
          else if (file[index].contains("j ")) {
            int type = getTypeCode((file[index].split(" "))[1]);
            joints[type] = new ZZoint(parseFloat(file[index+1].substring(2).split(" ")), 
                          getTypeCode(file[index+2].split(" ")[1]), 
                          getTypeCode(file[index+3].substring(2).split(" ")));
            index += 3;
          }
          index++;
        }
        println("Chargement du squelette : termin\u00e9");
      } else {
        println("Chargement du squelette : erreur \u00e0 l'ouverture du fichier " + filename);
      }
    }
     
     //public void update(Skeleton sklKin) {
      /***************************************************************
       * 
       *  met \u00e0 jour les donn\u00e9es du squelette
       * 
       ***************************************************************/
      /*
       KJoint[] tmp = sklKin.getJoints();
       for (int i = 0; i < jointsNumber; i++) {
        joints[i].set(tmp[i].getX(), tmp[i].getY(), tmp[i].getZ());
      }
     }
     */
     public void update(ZZkeleton skl) {
      /***************************************************************
       * 
       *  met \u00e0 jour les donn\u00e9es du squelette
       * 
       ***************************************************************/
      
       for (int i = 0; i < jointsNumber; i++) {
        joints[i].set(skl.joints[i].get());
      }
     }
     
     public ArrayList<Integer> getMember(int part) {
      /***************************************************************
       * 
       *  renvoie la liste des joints du membre
       * 
       ***************************************************************/
      
       ArrayList<Integer> toReturn = new ArrayList<Integer>();
       ArrayList<Integer> pile = new ArrayList<Integer>();
       pile.add(part);
       
       while (!(pile.isEmpty())) {
         int [] tmp = joints[pile.get(0)].getChildren();
         if (tmp != null) {
           for (int i = 0; i < tmp.length; i++) {
            pile.add(tmp[i]);
          }
         }
        toReturn.add(pile.remove(0));
      }
       
       return toReturn;
     }
  }
  
  class ZZector extends PVector {
    protected PVector origin;
    
    public ZZector() {
      this(0, 0, 0);
    }

    public ZZector(float x, float y) {
      this(x, y, 0);
    }
    
    public ZZector(float x, float y, float z) {
      super(x, y, z);
      origin = new PVector(x, y, z);
    }
    
    public ZZector(PVector v) {
      this(v.x, v.y, v.z);
    }
    
    public void reset() {
      this.set(origin.array());
    }
    
    public void rotate(float theta, float phi) {
        float temp = x;

        x = x*PApplet.cos(theta) - y*PApplet.sin(theta);
        y = temp*PApplet.sin(theta) + y*PApplet.cos(theta);
        
        temp = x;
        
        x = x*PApplet.cos(phi) - z*PApplet.sin(phi);
        z = temp*PApplet.sin(phi) + z*PApplet.cos(phi);
    }
    
    public void rotateAround(PVector center, float theta, float phi) {
      /***************************************************************
       * 
       *  rotation autour d'un point dans un espace 3D
       * 
       ***************************************************************/

      this.sub(center);
      rotate(theta, phi);
      this.add(center);
    }
    
    public void rotateAround(PVector center, float theta) {
      /***************************************************************
       * 
       *  rotation autour d'un point dans un espace 3D
       * 
       ***************************************************************/
      
      rotateAround(center, theta, 0);
    }
  }
  
  class ZZoint extends ZZector {
    protected int parent;
    protected int[] children;

    public ZZoint(float[] o, int p, int[] c) {
      super(o[0], o[1], o[2]);
      origin = new PVector(o[0], o[1], o[2]);
      parent = p;
      children = c[0] == -1 ? null : c;
    }
    
    public int getParent() {
      /***************************************************************
       * 
       *  retourne le code valeur du joint p\u00e8re
       * 
       ***************************************************************/

      return parent;
    }
    
    public int[] getChildren() {
      /***************************************************************
       * 
       *   retourne les codes de valeur des joints enfants
       * 
       ***************************************************************/

      return children;
    }
    
    @Override
    public String toString() {
      // TODO Auto-generated method stub
      String retour = new String("Parent : " + parent + " ; Enfants : ");
      for (int i = 0; i < children.length; i++) {
        retour += children[i] + " ";
      }
      return retour;
    }
  }
  
  class ZZertex extends ZZector {
    protected ArrayList<Integer> group;
    protected ArrayList<Integer> placeInGroup;
    protected ArrayList<Integer> placeInShape;
    protected float coef; /** correspond \u00e0 la cardinalit\u00e9 de group **/
    
    public ZZertex(float x, float y, float z) {
      super(x, y, z);
      group = new ArrayList<Integer>();
      placeInGroup = new ArrayList<Integer>();
      placeInShape = new ArrayList<Integer>();
      coef = 0;
    }
    
    public void addOccurence(int gnum, int snum, int vnum) {
      /***************************************************************
       * 
       *  ajoute cette occurence de vertex
       * 
       ***************************************************************/

      group.add(gnum);
      coef += group.contains(gnum) ? 0 : 1;
      placeInGroup.add(snum);
      placeInShape.add(vnum);
    }
    
    public void apply(PShape shape) {
      /***************************************************************
       * 
       *  applique la valeur du vertex \u00e0 toutes ses occurences dans un mod\u00e8le
       * 
       ***************************************************************/
      
      for (int i = 0; i < numberOfOccurences(); i++) {
        println(group.get(i) +" "+ placeInGroup.get(i) +" "+ placeInShape.get(i)+ " nb : " + ((shape.getChild(group.get(i))).getChildCount()));
        ((shape.getChild(group.get(i))).getChild(placeInGroup.get(i))).setVertex(placeInShape.get(i), this);
      }
    }

    public int numberOfOccurences() {
      /***************************************************************
       * 
       *  retourne le nombre de groupes auxquels appartient ce vertex
       * 
       ***************************************************************/

      return group.size();
    }
  }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "ProjectKinect_pde" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
