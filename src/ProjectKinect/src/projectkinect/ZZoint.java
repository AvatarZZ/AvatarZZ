package projectkinect;

import processing.core.*;

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
       *  retourne le code valeur du joint père
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
