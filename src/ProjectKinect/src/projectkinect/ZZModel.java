package projectkinect;

import processing.core.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import com.jogamp.opengl.util.texture.Texture;

class ZZModel {
	protected PApplet app;
    protected PShape model;
    protected ZZkeleton skeleton;
    ArrayList<ZZertex> vertices;
    ArrayList<ZZector> vertiTexture;
    ArrayList<Integer>[] groups;
	ArrayList<ZZMaterial> materiel = null;

    protected ZZModel(PApplet a) {
    	app = a;
    	model = app.createShape(PConstants.GROUP);
    	skeleton = new ZZkeleton();
    	vertices = new ArrayList<ZZertex>();
    	vertiTexture = new ArrayList<ZZector>();
    	groups = new ArrayList[25];
    }

    protected ZZModel(PApplet a, String filename) {
      this(a);
      this.load(filename);
    }
    
    public void load(String filename) {
      /***************************************************************
       * 
       * permet le chargement correct d'un .obj en g�rant les groupes
       * 
       ***************************************************************/
      
		String[] file;
		PShape currentShape = model;	// permet de d�terminer � quel partie on ajoute les faces
		ZZMaterial currentMat = null;
		int [] counter = new int[3];	
		InputStream fichier = null;		// pour ouvrir le fichier
		
		// initialisation de variables
		counter[2] = -1;
		for (int i = 0; i < groups.length; i++) {
			groups[i] = new ArrayList<Integer>();
		}
		
		// v�rification du type de fichier
		if(!(filename.contains(".obj"))) {
			PApplet.println("Chargement du mod�le : attention, il se peut que " + filename + " soit incompatible");
		}

		// ouverture du fichier
		try {
			fichier = new FileInputStream(filename);
		} catch (FileNotFoundException e) {
			PApplet.println("Chargement du mod�le : le fichier " + filename + " n'existe pas.");
		}
		file = PApplet.loadStrings(fichier);
      
		if(file != null) {
			for(int i = 0 ; i < file.length; i++) {
				if(file[i].contains("v ")) {	// lorsque l'on trouve un sommet
					float[] line = PApplet.parseFloat(file[i].substring(2).split(" "));
					vertices.add(new ZZertex(line[0], line[1], line[2]));
				} else if(file[i].contains("vt ")) {	// lorsque l'on trouve un sommet de texture
					float[] line = PApplet.parseFloat(file[i].substring(3).split(" "));
					vertiTexture.add(new ZZector(line[0], 1-line[1]));	// attention inversion de opengl
				} else if(file[i].contains("mtllib ")) {	// chargement des textures
					materiel = ZZMaterial.loadMaterials("./data/"+file[i].split(" ")[1]);
					for (int j = 0; j < materiel.size(); j++) {
						materiel.get(j).texture = app.loadImage(materiel.get(j).map_Kd);
					}
				} else if(file[i].contains("f ")) {		// lorsque l'on trouve une face
					String[] tmp = file[i].substring(2).split(" ");
					PShape nouv = app.createShape();
					int [] c = new int[3];	// indices des vertices
					int [] d = new int[3];	// indices des textures
					
					for (int j = 0; j < c.length; j++) {
						c[j] = PApplet.parseInt(tmp[j].split("/")[0])-1;
						d[j] = PApplet.parseInt(tmp[j].split("/")[1])-1;
					}

					nouv.setTexture(currentMat.texture);
					nouv.beginShape();
					nouv.textureMode(PConstants.NORMAL);
					nouv.noStroke();
					for (int lol = 0 ; lol < 3 ; lol++) {
						nouv.vertex(vertices.get(c[lol]).x,vertices.get(c[lol]).y,vertices.get(c[lol]).z, 
									vertiTexture.get(d[lol]).x, vertiTexture.get(d[lol]).y);
						vertices.get(c[lol]).addOccurence(counter[2], counter[1], lol);
						groups[counter[0]].add(c[lol]);
					}
					nouv.endShape(PConstants.CLOSE);
            
					currentShape.addChild(nouv);
					counter[1]++;
				} else if(file[i].contains("o ")) {		// lorsque l'on trouve un nouvel objet
            
				} else if(file[i].contains("g ")) {		// lorsque l'on trouve un nouveau groupe
					currentShape = app.createShape(PConstants.GROUP);
					PApplet.println("Nouveau groupe : " + file[i].split(" ")[1]);
					currentShape.setName(file[i].split(" ")[1]);
					model.addChild(currentShape);
					counter[0] = skeleton.getTypeCode(currentShape.getName());
					counter[1] = 0;
					counter[2]++;
				} else if(file[i].contains("usemtl ")) {		// lorsque l'on trouve un nouveau materiau
					currentMat = ZZMaterial.textureByName(materiel, file[i].split(" ")[1]);
				}
			}
			skeleton.load("./data/skeleton.sk");
			PApplet.println("Chargement du mod�le : termin�");
		} else {
			PApplet.println("Chargement du mod�le : erreur � l'ouverture du fichier " + filename);
		}
   	}
    
    public void draw() {
      /***************************************************************
       * 
       * affiche simplement le mod�le
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
       * change la taille du mod�le
       * 
       ***************************************************************/
      
    	model.scale(s);
    }
    
    public void rotateX(float angle) {
      /***************************************************************
       * 
       * rotation du mod�le autour de l'axe X
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
       *  rotation du mod�le autour de l'axe Y
       * 
       ***************************************************************/
      
    	model.rotateY(angle);
    }
    
    public void rotateZ(float angle) {
      /***************************************************************
       * 
       * rotation du mod�le autour de l'axe Z
       * 
       ***************************************************************/
      
    	model.rotateZ(angle);
    }
    
    public void translate(float x, float y, float z) {
      /***************************************************************
       * 
       * translation du mod�le
       * 
       ***************************************************************/
      
    	model.translate(x, y, z);
    }
    
    public PShape getChild(String target) {
      /***************************************************************
       * 
       * retourne le sous groupe target du mod�le
       * 
       ***************************************************************/
      
    	return model.getChild(target);
    }
    
    public PShape[] getChildren() {
      /***************************************************************
       * 
       * retourne le sous groupe target du mod�le
       * 
       ***************************************************************/
      
    	return model.getChildren();
    }
    
    public int getVertexCount() {
      /***************************************************************
       * 
       * retourne le sous groupe target du mod�le
       * 
       ***************************************************************/
      
    	return model.getVertexCount();
    }
    
    
//  public void move(Skeleton sklKin) {
    /***************************************************************
     * 
     *  algorithme principal d'animation du mod�le
     * 
     ***************************************************************/

    // d�claration de variables
    
    
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
