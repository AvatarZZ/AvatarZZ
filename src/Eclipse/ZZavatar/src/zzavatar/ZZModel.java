package zzavatar;

import processing.core.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import javax.print.attribute.standard.JobOriginatingUserName;

import KinectPV2.Skeleton;

import com.jogamp.opengl.util.texture.Texture;

class ZZModel {
	protected PApplet app;
    protected PShape model;
    protected ZZkeleton skeleton;
    ArrayList<ZZertex> vertices;
    ArrayList<ZZector> vertiTexture;
    ArrayList<Integer>[] groups;
	ArrayList<ZZMaterial> materiel = null;
	protected int idUser = 0; // determine le numero du joueur (6 max.)

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
    	 *	permet le chargement correct d'un .obj en gérant les groupes
    	 * 
    	 ***************************************************************/
    	
		String[] file;
		PShape currentShape = model;	// permet de déterminer à quel partie on ajoute les faces
		ZZMaterial currentMat = null;
		int [] counter = new int[3];	
		InputStream fichier = null;		// pour ouvrir le fichier
		
		// initialisation de variables
		counter[2] = -1;
		for (int i = 0; i < groups.length; i++) {
			groups[i] = new ArrayList<Integer>();
		}
		
		// vérification du type de fichier
		if(!(filename.contains(".obj"))) {
			PApplet.println("Chargement du modèle : attention, il se peut que " + filename + " soit incompatible");
		}

		// ouverture du fichier
		try {
			fichier = new FileInputStream(filename);
		} catch (FileNotFoundException e) {
			PApplet.println("Chargement du modèle : le fichier " + filename + " n'existe pas.");
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
			// squelette au format sk
			//skeleton.load("./data/skeleton.sk");		rajouter le cas où quand le squelette n'est pas trouvé on utilise le squelette de base
			// squelette au format bvh
			skeleton.loadBVH(filename.replace("obj", "bvh"));
			PApplet.println("Chargement du modèle : terminé");
		} else {
			PApplet.println("Chargement du modèle : erreur à l'ouverture du fichier " + filename);
		}
   	}
    
    public void draw() {
    	/***************************************************************
    	 * 
    	 *	affiche simplement le modele
    	 * 
    	 ***************************************************************/
    	
        app.pushMatrix();
        app.shape(model);
        app.popMatrix();
    }
    
    public int getChildCount() {
    	/***************************************************************
    	 * 
    	 *	donne le nombre de groupes enfants
    	 * 
    	 ***************************************************************/
    	
    	return model.getChildCount();
    }
    
    public void scale2(float s) { // deprecated : ne modifie pas les vertices mais joue sur la matrice
    	/***************************************************************
    	 * 
    	 *	change la taille du modèle
    	 * 
    	 ***************************************************************/

    	model.scale(s);
    	skeleton.scale(s);
    }
    
    public void scale(float s) {
    	/***************************************************************
    	 * 
    	 *	change la taille du modèle
    	 * 
    	 ***************************************************************/

    	for (int i = 0; i < vertices.size(); i++) {
    		vertices.get(i).mult(s);
    		vertices.get(i).apply(model);
		}
    	
    	skeleton.scale(s);
    }
    
    public void rotateX2(float angle) {	// deprecated : ne modifie pas les vertices mais joue sur la matrice
    	/***************************************************************
    	 * 
    	 *	rotation du modèle autour de l'axe X
    	 * 
    	 ***************************************************************/
      
    	model.rotateX(angle);
    	skeleton.rotateX(angle);
    	
    }
    
    public void rotateX(float angle) {
    	/***************************************************************
    	 * 
    	 *	rotation du modèle autour de l'axe X
    	 * 
    	 ***************************************************************/
    	
    	rotateAround(new PVector(0, 0, 0), angle, 0, 0);
    }
    
    public void rotateY(float angle) {
    	/***************************************************************
    	 * 
    	 *	rotation du modèle autour de l'axe Y
    	 * 
    	 ***************************************************************/
    	
    	rotateAround(new PVector(0, 0, 0), 0, angle, 0);
    }
    
    public void rotateZ(float angle) {
    	/***************************************************************
    	 * 
    	 *	rotation du modèle autour de l'axe Z
    	 * 
    	 ***************************************************************/
    	
    	rotateAround(new PVector(0, 0, 0), 0, 0, angle);
    }
    
    public void rotateAround(PVector center, float theta, float phi, float epsilon) {
    	/***************************************************************
    	 * 
    	 *	rotation du modèle autour d'un point
    	 * 
    	 ***************************************************************/
    	
    	for (int i = 0; i < vertices.size(); i++) {
    		vertices.get(i).rotateAround(center, theta, phi, epsilon);
    		vertices.get(i).apply(model);
		}
    	
    	skeleton.rotateAround(center, theta, phi, epsilon);
    }
    
    public void rotatePart(int part, float theta, float phi, float epsilon) {
    	/***************************************************************
    	 * 
    	 *  fait tourner toute une partie du modèle
    	 * 
    	 ***************************************************************/
      
    	ArrayList<Integer> jts = partWithChildren(part);
    	HashSet<Integer> vtcs = new HashSet<Integer>();
    	PVector center = skeleton.joints[skeleton.joints[part].getParent()];
      
    	for (int i = 0; i < jts.size(); i++) {
    		vtcs.addAll(groups[jts.get(i)]);
    		skeleton.joints[jts.get(i)].rotateAround(center, theta, phi);	// mise a jour du squelette
    	}
    	for (Iterator iterator = vtcs.iterator(); iterator.hasNext();) {
    		Integer integer = (Integer) iterator.next();
    		vertices.get(integer).rotateAround(center, theta, phi, epsilon);	// rotation autour du point center
    		vertices.get(integer).apply(model);									// modifie toutes les occurences
    	}
    }
    
    public void rotatePart(int part, float theta, float phi) {
	    /***************************************************************
	     * 
	     *  fait tourner toute une partie du modèle
	     * 
	     ***************************************************************/
      
    	rotatePart(part, theta, phi, 0);
    }
    
    protected ArrayList<Integer> partWithChildren(int part) {
    	/***************************************************************
    	 * 
    	 *	retourne la liste des vertices d'une partie et de ses enfants
    	 * 
    	 ***************************************************************/
            
    	return skeleton.getMember(part);
    }
    
    public void rotateY2(float angle) { // deprecated : ne modifie pas les vertices mais joue sur la matrice 
    	/***************************************************************
    	 * 
    	 *  rotation du modèle autour de l'axe Y
    	 * 
    	 ***************************************************************/
      
    	model.rotateY(angle);
    	skeleton.rotateY(angle);
    }
    
    public void rotateZ2(float angle) { // deprecated : ne modifie pas les vertices mais joue sur la matrice
    	/***************************************************************
    	 * 
    	 *	rotation du modèle autour de l'axe Z
    	 * 
    	 ***************************************************************/
      
    	model.rotateZ(angle);
    	skeleton.rotateZ(angle);
    }
    
    public void translate2(float x, float y, float z) { // deprecated : ne modifie pas les vertices mais joue sur la matrice
    	/***************************************************************
    	 * 
    	 *	translation du modèle
    	 * 
    	 ***************************************************************/

    	model.translate(x, y, z);
    	skeleton.translate(x, y, z);
    }
    
    public void translate(float x, float y, float z) { // deprecated : ne modifie pas les vertices mais joue sur la matrice
    	/***************************************************************
    	 * 
    	 *	translation du modèle
    	 * 
    	 ***************************************************************/

    	for (int i = 0; i < vertices.size(); i++) {
    		vertices.get(i).add(x, y, z);
    		vertices.get(i).apply(model);
		}
    	
    	skeleton.translate(x, y, z);
    }
    
    public void translate(ZZector zz) {
    	/***************************************************************
    	 * 
    	 *	translation du modèle
    	 * 
    	 ***************************************************************/

    	translate(zz.x, zz.y, zz.z);
    }
    
    public PShape getChild(String target) {
    	/***************************************************************
    	 * 
    	 *	retourne le sous groupe target du modèle
    	 * 
    	 ***************************************************************/
      
    	return model.getChild(target);
    }
    
    public PShape[] getChildren() {
    	/***************************************************************
    	 * 
    	 *	retourne le sous groupe target du modèle
    	 * 
    	 ***************************************************************/
      
    	return model.getChildren();
    }
    
    public int getVertexCount() {
    	/***************************************************************
    	 * 
    	 *	retourne le sous groupe target du modèle
    	 * 
    	 ***************************************************************/
      
    	return model.getVertexCount();
    }
    
    public static ArrayList<ZZModel> loadModels(PApplet a, String filename) {
    	/***************************************************************
    	 * 
    	 *  permet le chargement de plusieurs modèles à partir d'un fichier
    	 * 
    	 ***************************************************************/
    		
    	InputStream file = null;
    	String [] lines = null;
    	ArrayList<ZZModel> retour = null;
    	
    	try {
    		file = new FileInputStream(filename);
    	} catch (FileNotFoundException e) {
    		PApplet.println("Chargement de la base de données : le fichier " + filename + " n'existe pas.");
    	}
    	
    	lines = PApplet.loadStrings(file);
    	retour = new ArrayList<ZZModel>();
    	
    	if(lines != null) {
    		for (int i = 0; i < lines.length; i++) {
    			retour.add(new ZZModel(a, "./data/"+lines[i]+".obj"));
    		}
    		PApplet.println("Chargement de la base de données : terminé");
    	} else {
    		PApplet.println("Chargement de la base de données : erreur lors du chargement de fichier " + filename);
    	}
    	return retour;
    }
    
    public void move(ZZoint[] newPosition) {
	    /***************************************************************
	     * 
	     *  algorithme principal d'animation du modele
	     * 
	     ***************************************************************/

	    // translation generale
    	ZZector dl = newPosition[ZZkeleton.ROOT];
    	dl.sub(skeleton.joints[ZZkeleton.ROOT]);
    	this.translate(dl);
    }
    
}
