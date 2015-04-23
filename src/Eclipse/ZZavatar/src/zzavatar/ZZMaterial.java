package zzavatar;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PImage;
import sun.security.krb5.internal.APRep;

public class ZZMaterial {
    /***************************************************************
     * 
     *  classe contenant les informations sur les materiaux de textures
     * 
     ***************************************************************/
	
	protected String name;
	protected int Ns, d, illum;
	protected String map_Kd;
	protected float[] Kd;
	protected float[] Ks;
	protected float[] Ka;
	protected PImage texture;
	
	public ZZMaterial() {
		/***************************************************************
		 * 
		 *	constructeur basique
		 * 
		 ***************************************************************/
		
		name = new String();
		map_Kd = new String();
		Kd = new float[3];
		Ka = new float[3];
		Ks = new float[3];
	}
	
	public static ArrayList<ZZMaterial> loadMaterials(String filename) {
		/***************************************************************
		 * 
		 *  permet le chargement des materiaux d'un fichier .mtl
		 * 
		 ***************************************************************/
		
		InputStream file = null;
		String [] lines = null;
		ArrayList<ZZMaterial> retour = null;
		int nbOfMaterials = -1;
		
		try {
			file = new FileInputStream(filename);
		} catch (FileNotFoundException e) {
			PApplet.println("Chargement de la texture : le fichier " + filename + " n'existe pas.");
		}
		
		lines = PApplet.loadStrings(file);
		retour = new ArrayList<ZZMaterial>();
		
		if(lines != null) {
			for (int i = 0; i < lines.length; i++) {
				if(lines[i].contains("newmtl ")) {
					retour.add(new ZZMaterial());
					nbOfMaterials++;
					retour.get(nbOfMaterials).name = lines[i].split(" ")[1];
				} else if(lines[i].contains("Ns ")) {
					retour.get(nbOfMaterials).Ns = PApplet.parseInt(lines[i].split(" ")[1]);
				} else if(lines[i].contains("illum ")) {
					retour.get(nbOfMaterials).illum = PApplet.parseInt(lines[i].split(" ")[1]);
				} else if(lines[i].contains("map_Kd ")) {
					retour.get(nbOfMaterials).map_Kd = "./data/";
					retour.get(nbOfMaterials).map_Kd += lines[i].split(" ")[1];
				} else if(lines[i].contains("Kd ")) {
					float[] tmp = PApplet.parseFloat(lines[i].split(" "));
					for (int j = 1; j < tmp.length; j++) {
						retour.get(nbOfMaterials).Kd[j-1] = tmp[j];
					}
				} else if(lines[i].contains("d ")) {
					retour.get(nbOfMaterials).d = PApplet.parseInt(lines[i].split(" ")[1]);
				} else if(lines[i].contains("Ks ")) {
					float[] tmp = PApplet.parseFloat(lines[i].split(" "));
					for (int j = 1; j < tmp.length; j++) {
						retour.get(nbOfMaterials).Ks[j-1] = tmp[j];
					}
				} else if(lines[i].contains("Ka ")) {
					float[] tmp = PApplet.parseFloat(lines[i].split(" "));
					for (int j = 1; j < tmp.length; j++) {
						retour.get(nbOfMaterials).Ka[j-1] = tmp[j];
					}
				}
			}
			PApplet.println("Chargement des textures : terminé");
		} else {
			PApplet.println("Chargement de la texture : erreur lors du chargement de fichier " + filename);
		}
		return retour;
	}
	
	public static ZZMaterial textureByName(ArrayList<ZZMaterial> l, String n) {
		/***************************************************************
		 * 
		 *  retourne le materiel ayant le nom recherché dans une liste
		 * 
		 ***************************************************************/
		
		ZZMaterial retour = null;
		int i = 0;
		
		while (i < l.size() && !l.get(i).name.contentEquals(n)) {
			i++;
		}
		
		if(i < l.size() && l.get(i).name.contentEquals(n))
			retour = l.get(i);
		
		return retour;
	}
	
	@Override
	public String toString() {
		/***************************************************************
		 * 
		 *  retourne le nom de la texture
		 * 
		 ***************************************************************/
		
		return name;
	}
}
