package components;

import datastructures.Position;
import objects.BaseObject;

public class PhysicsComponent extends BaseComponent {
	
	
	public int jumping = -20;
	public float x_movement = 0;
	public float gravitation = 0.09f;
	public float floor_height = -0.5f;
	public int friction = 20;
	
	
	private Position pos;
	
	public BaseObject object_to_calc;
	
	
	
	
	public void calculate(){
		pos = new Position(0, 0 ,0);         
		if (jumping > -20) {
			pos.y += 0.002f*jumping;
	    	jumping -= 1;
	    } else {	
	    	pos.y += -Math.min(gravitation*Math.abs(0.25/(floor_height-object_to_calc.getPos().y)), object_to_calc.getPos().y);
	    }
		
		if (pos.y == 0) {
			friction = 2;
		} else {
			friction = 40;
		}
		
		pos.x = 0.002f * x_movement;
		x_movement += -x_movement * 1/friction;
		
		object_to_calc.translatePos(pos);
	}
}
