package projectkinect;

import processing.core.*;

class ZZector extends PVector {
    protected PVector origin;
    
    public ZZector() {
      this(0, 0, 0);
    }

    public ZZector(float x, float y) {
      this(x, y, 0);
    }
    
    public ZZector(float x, float y, float z) {
      super(x, y, z);
      origin = new PVector(x, y, z);
    }
    
    public ZZector(PVector v) {
      this(v.x, v.y, v.z);
    }
    
    public void reset() {
      this.set(origin.array());
    }
    
    public void rotate(float theta, float phi) {
        float temp = x;

        x = x*PApplet.cos(theta) - y*PApplet.sin(theta);
        y = temp*PApplet.sin(theta) + y*PApplet.cos(theta);
        
        temp = x;
        
        x = x*PApplet.cos(phi) - z*PApplet.sin(phi);
        z = temp*PApplet.sin(phi) + z*PApplet.cos(phi);
    }
    
    public void rotate(float theta, float phi, float epsilon) {
        float temp = x;

        x = x*PApplet.cos(theta) - y*PApplet.sin(theta);
        y = temp*PApplet.sin(theta) + y*PApplet.cos(theta);
        
        temp = x;
        
        x = x*PApplet.cos(phi) - z*PApplet.sin(phi);
        z = temp*PApplet.sin(phi) + z*PApplet.cos(phi);
        
        temp = y;
        
        y = y*PApplet.cos(phi) - z*PApplet.sin(phi);
        z = temp*PApplet.sin(phi) + z*PApplet.cos(phi);
    }

    public void rotateAround(PVector center, float theta, float phi, float epsilon) {
		/***************************************************************
		 * 
		 *  rotation autour d'un point dans un espace 3D
		 * 
		 ***************************************************************/
		
		this.sub(center);
		rotate(theta, phi, epsilon);
		this.add(center);
    }
    
    public void rotateAround(PVector center, float theta, float phi) {
		/***************************************************************
		 * 
		 *  rotation autour d'un point dans un espace 3D
		 * 
		 ***************************************************************/
		
		this.sub(center);
		rotate(theta, phi);
		this.add(center);
    }
    
    public void rotateAround(PVector center, float theta) {
      /***************************************************************
       * 
       *  rotation autour d'un point dans un espace 3D
       * 
       ***************************************************************/
      
      rotateAround(center, theta, 0);
    }
}
