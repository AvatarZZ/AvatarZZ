import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PImage;

class ZZbackground {

	private ArrayList<PImage> images = null;
	private PImage current;
	private PApplet app = null;
	private int index = -1;
	private boolean activate = true;
	
	public ZZbackground(PApplet a, String list) {
    	/***************************************************************
    	 * 
    	 *  constructeur elementaire
    	 * 
    	 ***************************************************************/
		
		app = a;
		images = new ArrayList<PImage>();
		loadBackgrounds(list);
		index = images.isEmpty() ? -1 : 0;
		if (0==index)
			current = images.get(0);
	}
	
	public ZZbackground(PApplet a) {
    	/***************************************************************
    	 * 
    	 *  constructeur elementaire
    	 * 
    	 ***************************************************************/
		
		this(a, "./data/backgrounds");
	}
	
	private void loadBackgrounds(String filename) {
    	/***************************************************************
    	 * 
    	 *  permet le chargement de plusieurs fond a partir d'un fichier
    	 * 
    	 ***************************************************************/
    		
    	String [] lines = null;
    	
    	lines = app.loadStrings(filename);
    	
    	if(lines != null) {
    		for (int i = 0; i < lines.length; i++) {
    			images.add(app.loadImage("./data/"+lines[i]));
    		}
    		PApplet.println("Chargement de la base de donnees : termine");
    	} else {
    		PApplet.println("Chargement de la base de donnees : erreur lors du chargement de fichier " + filename);
    	}
	}
	
	public void next() {
		/***************************************************************
	     * 
	     *  change le fond / suivant
	     * 
	     ***************************************************************/
		
		index++;
		if (index >= images.size())
			index = 0;
		current = images.get(index);
	}
	
	public void previous() {
		/***************************************************************
	     * 
	     *  change le fond / precedent
	     * 
	     ***************************************************************/
		
		index--;
		if (index < 0)
			index = images.size()-1;
		current = images.get(index);
	}
	
	public void activate() {
		/***************************************************************
	     * 
	     *  active/desactive le fond
	     * 
	     ***************************************************************/
		
		activate = !activate;
	}	
	
	public void draw() {
		/***************************************************************
	     * 
	     *  dessine le font
	     * 
	     ***************************************************************/
		
		if(activate) {
			app.pushMatrix();
			app.translate(0, 0, -512);
			app.image(current, -(current.width/2), -(current.height/2));
			app.popMatrix();
		}
	}

}
