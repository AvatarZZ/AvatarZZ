package projectkinect;

import processing.core.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

class ZZModel {
    protected PApplet app;
    protected PShape model;
    protected ZZkeleton skeleton;
    ArrayList<ZZertex> vertices;
    ArrayList<Integer>[] groups;

    protected ZZModel(PApplet a) {
      app = a;
      model = app.createShape(PConstants.GROUP);
      skeleton = new ZZkeleton(app);
      vertices = new ArrayList<ZZertex>();
      groups = new ArrayList[25];
    }

    protected ZZModel(PApplet a, String filename) {
      this(a);
      this.load(filename);
    }
    
    public void load(String filename) {
      /***************************************************************
       * 
       * permet le chargement correct d'un .obj en gérant les groupes
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
        PApplet.println("Chargement du modèle : attention, il se peut que " + filename + " soit incompatible");
      }
      
      file = app.loadStrings(filename);
      currentShape = model;
      
      if(file != null) {
        for(int i = 0 ; i < file.length; i++) {
          if(file[i].contains("v ")) {
            float[] line = PApplet.parseFloat(file[i].substring(2).split(" "));
            vertices.add(new ZZertex(line[0], line[1], line[2]));
          } else if(file[i].contains("vt ")) {
            
          } else if(file[i].contains("f ")) {
            String[] tmp = file[i].substring(2).split(" ");
            PShape nouv = app.createShape();
            int [] c = new int[3];

            nouv.beginShape();
            app.noStroke();
            for (int lol = 0 ; lol < 3 ; lol++)
              nouv.vertex(0,0,0);
            nouv.endShape(PConstants.CLOSE);
            
            for (int j = 0; j < c.length; j++) {
              c[j] = PApplet.parseInt(tmp[j].split("/")[0])-1;
              nouv.setVertex(j, vertices.get(c[j]));
              vertices.get(c[j]).addOccurence(counter[2], counter[1], j);
              groups[counter[0]].add(c[j]);
            }
            
            currentShape.addChild(nouv);
            counter[1]++;
          } else if(file[i].contains("o ")) {
            
          } else if(file[i].contains("g ")) {
            currentShape = app.createShape(PConstants.GROUP);
            PApplet.println("Nouveau groupe : " + file[i].split(" ")[1]);
            currentShape.setName(file[i].split(" ")[1]);
            model.addChild(currentShape);
            counter[0] = skeleton.getTypeCode(currentShape.getName());
            counter[1] = 0;
            counter[2]++;
          }
        }
        skeleton.load("skeleton.sk");
        PApplet.println("Chargement du modèle : terminé");
      } else {
        PApplet.println("Chargement du modèle : erreur à l'ouverture du fichier " + filename);
      }
    }
    
    public void draw() {
      /***************************************************************
       * 
       * affiche simplement le modèle
       * 
       ***************************************************************/
      
        app.pushMatrix();
        app.shape(model);
        app.popMatrix();
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
       * change la taille du modèle
       * 
       ***************************************************************/
      
      model.scale(s);
    }
    
    public void rotateX(float angle) {
      /***************************************************************
       * 
       * rotation du modèle autour de l'axe X
       * 
       ***************************************************************/
      
      model.rotateX(angle);
    }
    
    public void rotatePart(int part, float theta, float phi, float epsilon) {
      /***************************************************************
       * 
       *  fait tourner toute une partie du mod�le
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
        vertices.get(integer).rotateAround(center, theta, phi, epsilon);
        vertices.get(integer).apply(model);
      }
    }
    
    public void rotatePart(int part, float theta, float phi) {
	    /***************************************************************
	     * 
	     *  fait tourner toute une partie du mod�le
	     * 
	     ***************************************************************/
      
    	rotatePart(part, theta, phi, 0);
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
       *  rotation du modèle autour de l'axe Y
       * 
       ***************************************************************/
      
      model.rotateY(angle);
    }
    
    public void rotateZ(float angle) {
      /***************************************************************
       * 
       * rotation du modèle autour de l'axe Z
       * 
       ***************************************************************/
      
      model.rotateZ(angle);
    }
    
    public void translate(float x, float y, float z) {
      /***************************************************************
       * 
       * translation du modèle
       * 
       ***************************************************************/
      
      model.translate(x, y, z);
    }
    
    public PShape getChild(String target) {
      /***************************************************************
       * 
       * retourne le sous groupe target du modèle
       * 
       ***************************************************************/
      
      return model.getChild(target);
    }
    
    public PShape[] getChildren() {
      /***************************************************************
       * 
       * retourne le sous groupe target du modèle
       * 
       ***************************************************************/
      
      return model.getChildren();
    }
    
    public int getVertexCount() {
      /***************************************************************
       * 
       * retourne le sous groupe target du modèle
       * 
       ***************************************************************/
      
      return model.getVertexCount();
    }
    
    
//  public void move(Skeleton sklKin) {
    /***************************************************************
     * 
     *  algorithme principal d'animation du modèle
     * 
     ***************************************************************/

    // déclaration de variables
    
    
    // calcul de la translation générale et des rotations locales 
    

    // applications des transformations
/*      for (int i = 0; i < model.getChildCount(); i++) {
      for (int j = 0; j < model.getChild(i).getChildCount(); j++) {
        for (int k = 0; k < model.getChild(i).getChildCount(); k++) {
          
        }
      }
    }

    // mise à jour des données
    skeleton.update(sklKin);
  }
*/
    
  }
