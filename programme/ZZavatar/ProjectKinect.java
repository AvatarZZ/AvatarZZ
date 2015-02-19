package projectkinect;

import java.util.ArrayList;

import com.sun.javafx.iio.common.PushbroomScaler;

import processing.core.*;
import processing.data.*;
import processing.opengl.PGraphics3D;
import saito.objloader.OBJModel;


public class ProjectKinect extends PApplet {

	//protected PShape perso;
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
	
	public void setup() {
		// fenêtre
		size(1280, 960, P3D);
		
		debug = false;
		
		clone = new ZZModel();
		clone.load("man.obj");
		/*
		perso = loadShape("man.obj");
		perso.scale(32);
		perso.rotateZ(PI);
		perso.rotateY(PI);
		perso.translate(0, 0, -4);*/
		clone.scale(64);
		clone.rotateZ(PI);
		clone.rotateY(PI);
		clone.translate(0, -1, -4);
	}
	
	public void draw() {
		// effacer écran
		background(100);
		lights();
		
		if(debug) { // deboggage
			debugTools();
		}
		
		vision();
		
		clone.draw();
		//shape(perso);
	}
	
	public void vision() {
		translate(width/2, height, 0 ); // décale origine dessin 
		
		camera(distanceCamXZ*cos(radians(angleCamXZ)), 0, distanceCamXZ*sin(radians(angleCamXZ)), 0, 0, 0,0,1,0); // angle de vue de la scène 3D

        rotateX(radians(angleX)); // rotation de angleX ° autour de l'axe des X (horizontal) par rapport référence dessin
        rotateY(radians(0)); // rotation autour de l'axe des y (vertical) par rapport référence dessin
        rotateZ(radians(angleZ)); // rotation autour de l'axe des z (avant-arriere) par rapport référence dessin
        
	}
	
	public void debugTools() {
		text(frameRate, 50, 50);
		
        stroke(jaune);
        // box(width, height, depth);
        box(200, 5, 200);


        // while(true); // stoppe boucle draw

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

	public // si clic
	void mousePressed()	{
	    
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
	
	public static void main(String _args[]) {
		PApplet.main(new String[] { projectkinect.ProjectKinect.class.getName() });
	}
	
	class ZZModel {

		protected PShape model;

		protected ZZModel() {
			model = createShape(GROUP);
		}
		
		public void load(String filename) {
			/***************************************************************
			 * 
			 * permet le chargement correct d'un .obj en gérant les groupes
			 * 
			 ***************************************************************/
			
			ArrayList<PVector> vertices = new ArrayList<PVector>();
			String[] lines = loadStrings(filename);
			PShape currentShape = model; 
			int j = 0;
			println("there are " + lines.length + " lines");
			for(int i = 0 ; i < lines.length; i++) {
				//println(lines[i]);
				if(lines[i].contains("v ")) {
					float[] line = parseFloat(lines[i].substring(2).split(" "));
					vertices.add(new PVector(line[0], line[1], line[2]));
				} else if(lines[i].contains("vt ")) {
					
				} else if(lines[i].contains("f ")) {
					String[] tmp = lines[i].substring(2).split(" ");
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
				} else if(lines[i].contains("o ")) {
					
				} else if(lines[i].contains("g ")) {
					currentShape = createShape(GROUP);
					println("Nouveau groupe : " + lines[i].split(" ")[1]);
					currentShape.setName(lines[i].split(" ")[1]);
					model.addChild(currentShape);
				}
			}
		}
		
		public void draw() {
			/***************************************************************
			 * 
			 * affiche simplement le modèle
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
		
		public void rotateY(float angle) {
			/***************************************************************
			 * 
			 * rotation du modèle autour de l'axe Y
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
		
	}
}
