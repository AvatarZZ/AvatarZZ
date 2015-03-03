import processing.core.*;
import java.util.ArrayList;

class ZZkeleton {
    public final static int WAIST      = 0;
    public final static int ROOT      = 1;
    public final static int NECK      = 2;
    public final static int HEAD      = 3;
    public final static int SHOULDER_LEFT  = 4;
    public final static int ELBOW_LEFT    = 5;
    public final static int WRIST_LEFT      = 6;
    public final static int HAND_LEFT    = 7;
    public final static int SHOULDER_RIGHT  = 8;
    public final static int ELBOW_RIGHT    = 9;
    public final static int WRIST_RIGHT    = 10;
    public final static int HAND_RIGHT    = 11;
    public final static int HIP_LEFT    = 12;
    public final static int KNEE_LEFT    = 13;
    public final static int ANKLE_LEFT    = 14;
    public final static int FOOT_LEFT    = 15;
    public final static int HIP_RIGHT    = 16;
    public final static int KNEE_RIGHT    = 17;
    public final static int ANKLE_RIGHT    = 18;
    public final static int FOOT_RIGHT    = 19;
    public final static int TORSO      = 20;
    public final static int INDEX_LEFT    = 21;
    public final static int THUMB_LEFT    = 22;
    public final static int INDEX_RIGHT    = 23;
    public final static int THUMB_RIGHT    = 24;
    
    protected PApplet app;
    protected ZZoint[] joints;
    protected int jointsNumber;
    protected String name;
    
    public ZZkeleton(PApplet a) {
      app = a;
      jointsNumber = 25;
      joints = new ZZoint[jointsNumber];
      name = "default";
    }
    
    public ZZkeleton(PApplet a, String filename) {
      this(a);
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
      else if(type.equals("(null)")) code = WAIST;
      
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
       * charge le squelette à partir d'un fichier .sk
       * 
       ***************************************************************/
      
      String[] file;
      
      if(!(filename.contains(".sk"))) {
        PApplet.println("Chargement du squelette : attention, il se peut que " + filename + " soit incompatible");
      }
      
      file = app.loadStrings(filename);
      
      if(file != null) {
        int index = 0;
        while (index < file.length) {
          if (file[index].contains("sk ")) {
            name = file[index].substring(3);
          }
          else if (file[index].contains("j ")) {
            int type = getTypeCode((file[index].split(" "))[1]);
            joints[type] = new ZZoint(PApplet.parseFloat(file[index+1].substring(2).split(" ")), 
                          getTypeCode(file[index+2].split(" ")[1]), 
                          getTypeCode(file[index+3].substring(2).split(" ")));
            index += 3;
          }
          index++;
        }
        PApplet.println("Chargement du squelette : terminé");
      } else {
        PApplet.println("Chargement du squelette : erreur à l'ouverture du fichier " + filename);
      }
    }
     
     //public void update(Skeleton sklKin) {
      /***************************************************************
       * 
       *  met à jour les données du squelette
       * 
       ***************************************************************/
      /*
       KJoint[] tmp = sklKin.getJoints();
       for (int i = 0; i < jointsNumber; i++) {
        joints[i].set(tmp[i].getX(), tmp[i].getY(), tmp[i].getZ());
      }
     }
     */
     public void update(ZZkeleton skl) {
      /***************************************************************
       * 
       *  met à jour les données du squelette
       * 
       ***************************************************************/
      
       for (int i = 0; i < jointsNumber; i++) {
        joints[i].set(skl.joints[i].get());
      }
     }
     
     public ArrayList<Integer> getMember(int part) {
      /***************************************************************
       * 
       *  renvoie la liste des joints du membre
       * 
       ***************************************************************/
      
       ArrayList<Integer> toReturn = new ArrayList<Integer>();
       ArrayList<Integer> pile = new ArrayList<Integer>();
       pile.add(part);
       
       while (!(pile.isEmpty())) {
         int [] tmp = joints[pile.get(0)].getChildren();
         if (tmp != null) {
           for (int i = 0; i < tmp.length; i++) {
            pile.add(tmp[i]);
          }
         }
        toReturn.add(pile.remove(0));
      }
       
       return toReturn;
     }
}
