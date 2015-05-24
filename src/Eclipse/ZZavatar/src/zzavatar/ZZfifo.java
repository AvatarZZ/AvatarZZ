package zzavatar;

import java.util.LinkedList;


public class ZZfifo<E> {

	private LinkedList<E> list = new LinkedList<E>();

	public void put(E el) {
   	 	/***************************************************************
   	 	 * 
   	 	 *  ajoute un element a la file
   	 	 * 
   	 	 ***************************************************************/
		
		list.addLast(el);
	}


	public E get() {
   	 	/***************************************************************
   	 	 * 
   	 	 *  sort le premier element a servir
   	 	 * 
   	 	 ***************************************************************/
		
		if (list.isEmpty()) {
			return null;
		}
		return list.removeFirst();
	}

	public Object[] getAll() {
   	 	/***************************************************************
   	 	 * 
   	 	 *  sort tous les elements
   	 	 * 
   	 	 ***************************************************************/
		
		Object[] res = new Object[list.size()];
		for (int i = 0; i < res.length; i++) {
			res[i] = list.get(i);
		}
		list.clear();
		return res;
	}

	public E head() {
   	 	/***************************************************************
   	 	 * 
   	 	 *  lecture du premier element sans le sortir
   	 	 * 
   	 	 ***************************************************************/
		
		return list.getFirst();
	}

	public boolean isEmpty() {
   	 	/***************************************************************
   	 	 * 
   	 	 *  vrai si la file est vide
   	 	 * 
   	 	 ***************************************************************/
		
		return list.isEmpty();
	}

	public int size() {
   	 	/***************************************************************
   	 	 * 
   	 	 *  retourne la taille de la file
   	 	 * 
   	 	 ***************************************************************/
		
		return list.size();
	}
}