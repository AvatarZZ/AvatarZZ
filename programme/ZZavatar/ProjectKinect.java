package projectkinect;

import java.util.ArrayList;
import java.lang.Enum;

import processing.core.*;
import sun.font.EAttribute;
import KinectPV2.*;


public class ProjectKinect extends PApplet {

	protected PShape perso;
	protected ZZModel clone;
	protected KinectPV2 kinect;
	Skeleton [] squelette;
	KJoint [] oldJoints;
	protected boolean debug;
	
	//------ d�claration des variables de couleur utiles ----
	int jaune=color(255,255,0);
	int vert=color(0,255,0);
	int rouge=color(255,0,0);
	int bleu=color(0,0,255);
	int noir=color(0,0,0);
	int blanc=color(255,255,255);
	int bleuclair=color(0,255,255);
	int violet=color(255,0,255);

	//---------- angles pour 3D ----
	int angleX=0; // angle X pour trac� de forme
	int angleY=0; // angle Y pour trac� de forme
	int angleZ=0; // angle Z pour trac� de forme

	int distanceCamXZ=400; // variable distance � la cam�ra dans plan XZ
	int distanceCamYZ=0; // variable distance � la cam�ra dans plan YZ

	int angleCamXZ=90; // angle dans le plan XZ de la vis�e de la cam�ra avec l'axe des X dans le plan XZ
	int angleCamYZ=90; // angle avec axe YZ de la vis�e de la cam�ra dans le plan YZ
	
	public void setup() {
		// fen�tre
		size(1280, 960, P3D);
		// limitation du rafraichissement
		frameRate(25);
		
		debug = false;
		
		clone = new ZZModel();
		clone.load("man.obj");
		println("Nombre de vertices : "+ clone.getChild("head").getVertexCount());
		
		perso = loadShape("man.obj");
		perso.scale(32);
		perso.rotateZ(PI);
		perso.rotateY(PI);
		perso.translate(0, 0, -4);
		clone.scale(64);
		clone.rotateZ(PI);
		clone.rotateY(PI);
		clone.translate(0, -1, 0);
		
		// kinect
		kinect = new KinectPV2(this);
		kinect.enableDepthMaskImg(true);
		kinect.enableSkeleton(true);
		kinect.enableSkeleton3dMap(true);
		kinect.init();
  
		// squelette
		squelette = new Skeleton[6];
		oldJoints = new KJoint[25];
	}
	
	public void draw() {
		KJoint[] joints;
		
		// effacer �cran
		background(100);
		lights();
		
		if(debug) { // deboggage
			debugTools();
		}
		
		vision();
		 
		/* traitement kinect
		// acquisition des informations
		squelette = kinect.getSkeleton3d();
		for (int i = 0; i < squelette.length; i++) {
			if (squelette[i].isTracked()) {
		    	joints = squelette[i].getJoints();
		    	for (int j = 0; j < joints.length; j++) {
					if (joints[j].getType() == SkeletonProperties.JointType_SpineBase) {
						text("X : " + joints[j].getX() + "\nY : " + joints[j].getY() + "\nZ : " + joints[j].getZ(), -100, 150, 50);
						float cx = joints[j].getX()/100;//-oldJoints[j].getX();
						float cy = joints[j].getY()/100;//-oldJoints[j].getY();
						float cz = -joints[j].getZ()/100;//-oldJoints[j].getZ();
						clone.translate(cx, cy, cz);
					}
				}
		    	oldJoints = joints;
		    	drawBody(joints);
			}
		}*/
		//(clone.getChild("forearm_R")).setVertex(0).rotateX((float) 0.01);
		stroke(255, 0, 0);
		//println(perso.getChildCount()*3);
		for (int i = 0; i < perso.getChildCount()*3; i++) {
			perso.getChild(11).setVertex(1, mouseX, mouseY, 0);
		}
		
		clone.draw();
		sphere(5);
		shape(perso);
	}
	
	public void vision() {
		translate(width/2, height, 0 ); // d�cale origine dessin 
		
		camera(distanceCamXZ*cos(radians(angleCamXZ)), 0, distanceCamXZ*sin(radians(angleCamXZ)), 0, 0, 0,0,1,0); // angle de vue de la sc�ne 3D

        rotateX(radians(angleX)); // rotation de angleX � autour de l'axe des X (horizontal) par rapport r�f�rence dessin
        rotateY(radians(0)); // rotation autour de l'axe des y (vertical) par rapport r�f�rence dessin
        rotateZ(radians(angleZ)); // rotation autour de l'axe des z (avant-arriere) par rapport r�f�rence dessin
        
	}
	
	public void debugTools() {
		text(frameRate, 50, 50);
		
        stroke(jaune);
        // box(width, height, depth);
        box(200, 5, 200);


        // while(true); // stoppe boucle draw

        // affiche le rep�re Ox,Oy,Oz

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

	//si touche press�e
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
				if (keyCode == UP) { // si touche Haut appuy�e
                    angleX=angleX+5;
                }
                else if (keyCode == DOWN) {// si touche BAS appuy�e
                 angleX=angleX-5;
                }

                else if (keyCode == LEFT) {// si touche GAUCHE appuy�e

                 angleZ=angleZ+5;

                }
                else if (keyCode == RIGHT) {// si touche GAUCHE appuy�e

                 angleZ=angleZ-5;

                }
				break;
		}
	}

	// si clic
	public // si clic
	void mousePressed()	{
	    
	}

	// si d�clic
	public void mouseReleased() {
	  
	}

	// si clic maintenu
	public void mouseDragged() {
	    
	}

	// si souris boug�
	public void mouseMoved() {
	    
	}
	
	// n�cessaire pour tourner sur java
	public static void main(String _args[]) {
		PApplet.main(new String[] { projectkinect.ProjectKinect.class.getName() });
	}
	
	class ZZModel {

		protected PShape model;
		protected ZZkeleton skeleton;

		protected ZZModel() {
			model = createShape(GROUP);
			skeleton = new ZZkeleton();
		}

		protected ZZModel(String filename) {
			this();
			this.load(filename);
		}
		
		public void load(String filename) {
			/***************************************************************
			 * 
			 * permet le chargement correct d'un .obj en g�rant les groupes
			 * 
			 ***************************************************************/
			
			ArrayList<PVector> vertices;
			String[] file;
			PShape currentShape;
			
			if(!(filename.contains(".obj"))) {
				println("Chargement du mod�le : attention, il se peut que " + filename + " soit incompatible");
			}
			
			file = loadStrings(filename);
			currentShape = model;
			vertices = new ArrayList<PVector>();
			
			if(file != null) {
				for(int i = 0 ; i < file.length; i++) {
					//println(file[i]);
					if(file[i].contains("v ")) {
						float[] line = parseFloat(file[i].substring(2).split(" "));
						vertices.add(new PVector(line[0], line[1], line[2]));
					} else if(file[i].contains("vt ")) {
						
					} else if(file[i].contains("f ")) {
						String[] tmp = file[i].substring(2).split(" ");
						PShape nouv = createShape();
						int cx = parseInt(tmp[0].split("/")[0])-1;
						int cy = parseInt(tmp[1].split("/")[0])-1;
						int cz = parseInt(tmp[2].split("/")[0])-1;
						nouv.beginShape();
						noStroke();
						nouv.vertex(0,0,0);
						nouv.vertex(0,0,0);
						nouv.vertex(0,0,0);
						nouv.endShape(CLOSE);
						nouv.setVertex(0, vertices.get(cx));
						nouv.setVertex(1, vertices.get(cy));
						nouv.setVertex(2, vertices.get(cz));
						currentShape.addChild(nouv);
					} else if(file[i].contains("o ")) {
						
					} else if(file[i].contains("g ")) {
						currentShape = createShape(GROUP);
						println("Nouveau groupe : " + file[i].split(" ")[1]);
						currentShape.setName(file[i].split(" ")[1]);
						model.addChild(currentShape);
					}
				}
				skeleton.load("skeleton.sk");
				println("Chargement du mod�le : termin�");
			} else {
				println("Chargement du mod�le : erreur � l'ouverture du fichier " + filename);
			}
		}
		
		public void draw() {
			/***************************************************************
			 * 
			 * affiche simplement le mod�le
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
		
		public void rotateY(float angle) {
			/***************************************************************
			 * 
			 * rotation du mod�le autour de l'axe Y
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
		
	}
	
	class ZZkeleton {
		public final static int WAIST			= 0;
		public final static int ROOT			= 1;
		public final static int NECK			= 2;
		public final static int HEAD			= 3;
		public final static int SHOULDER_LEFT	= 4;
		public final static int ELBOW_LEFT		= 5;
		public final static int WRIST_LEFT	    = 6;
		public final static int HAND_LEFT		= 7;
		public final static int SHOULDER_RIGHT	= 8;
		public final static int ELBOW_RIGHT		= 9;
		public final static int WRIST_RIGHT		= 10;
		public final static int HAND_RIGHT		= 11;
		public final static int HIP_LEFT		= 12;
		public final static int KNEE_LEFT		= 13;
		public final static int ANKLE_LEFT		= 14;
		public final static int FOOT_LEFT		= 15;
		public final static int HIP_RIGHT		= 16;
		public final static int KNEE_RIGHT		= 17;
		public final static int ANKLE_RIGHT		= 18;
		public final static int FOOT_RIGHT		= 19;
		public final static int TORSO			= 20;
		public final static int INDEX_LEFT		= 21;
		public final static int THUMB_LEFT		= 22;
		public final static int INDEX_RIGHT		= 23;
		public final static int THUMB_RIGHT		= 24;
		
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
			 * charge le squelette � partir d'un fichier .sk
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
				println("Chargement du squelette : termin�");
			} else {
				println("Chargement du squelette : erreur � l'ouverture du fichier " + filename);
			}
		}
	}
	
	class ZZoint {
		protected PVector coor;
		protected PVector origin;
		protected int parent;
		protected int[] children;

		public ZZoint(float[] o, int p, int[] c) {
			origin = coor = new PVector(o[0], o[1], o[2]);
			parent = p;
			children = c;
		}
		
		public ZZoint() {
			origin = coor = null;
			parent = -1;
			children = null;
		}
	}
}
