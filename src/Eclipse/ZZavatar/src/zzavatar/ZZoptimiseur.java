package zzavatar;

import processing.core.PApplet;

public class ZZoptimiseur {
	private int nbEch; 		// nombre d'echantillons pour moyennage
	private int cptEch;		// compteur d'echantillons
	private ZZfifo<ZZoint[]> positionsOptimisees;		// file des positions utilisables
	private ZZoint[] depart, arrivee;
	
	public ZZoptimiseur(int qtEch, ZZoint[] initial) {
   	 	/***************************************************************
   	 	 * 
   	 	 *  constructeur par défaut
   	 	 * 
   	 	 ***************************************************************/
    	
		positionsOptimisees = new ZZfifo<ZZoint[]>();
		nbEch = qtEch;
		cptEch = 0;
		depart = initial;
	}
	
	public void reset(int qtEch, ZZoint[] initial) {
   	 	/***************************************************************
   	 	 * 
   	 	 *  remet a zero l'optimiseur en economisant des allocations
   	 	 * 
   	 	 ***************************************************************/
    	
		positionsOptimisees = new ZZfifo<ZZoint[]>();
		nbEch = qtEch;
		cptEch = 0;
		depart = initial;
	}
	
	public void addEch(ZZoint[] ech) {
   	 	/***************************************************************
   	 	 * 
   	 	 *  ajoute un echantillon a l'optimiseur
   	 	 * 
   	 	 ***************************************************************/
    	
		if (cptEch!=0) {							//****************************************
			arrivee = ZZoint.add(ech, arrivee);		//
		} else {									//	on somme toutes les données entrantes
			arrivee = ech;							//
		}											//****************************************

		cptEch++;
		
		if (cptEch==nbEch) {	// si on a tous les echantillons
			cptEch = 0;
			ZZoint.div(arrivee, nbEch);
			for (int i = 1; i <= nbEch; i++) {
				positionsOptimisees.put(ZZoint.lerp(depart, arrivee, i/nbEch));	// interpolation des bonnes positions
			}
			depart = arrivee;	// verifier la copie
		}
	}
	
	public ZZoint[] getOptimizedValue() {
   	 	/***************************************************************
   	 	 *
   	 	 *  retourne une valeur optimisee
   	 	 *
   	 	 ***************************************************************/

		ZZoint[] retour = positionsOptimisees.get();
		
		return retour;
	}

	public boolean dataAvailable() {
   	 	/***************************************************************
  	 	 *
  	 	 *  retourne vrai si des donnees sont disponibles
  	 	 *
  	 	 ***************************************************************/
   	
		return !(positionsOptimisees.isEmpty());
	}
	
	@Override
	public String toString() {
   	 	/***************************************************************
  	 	 *
  	 	 *  toString classique
  	 	 *
  	 	 ***************************************************************/
   	
		String retour = "Optimiseur :\nEchantillons : " + positionsOptimisees.size();
		
		return retour;
	}
}
