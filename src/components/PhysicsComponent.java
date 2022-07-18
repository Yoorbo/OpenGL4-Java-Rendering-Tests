package components;

import datastructures.Position;
import datastructures.Rotation;
import datastructures.Scale;

public class PhysicsComponent extends BaseComponent {
	
	
	public int jumping = -20;
	public float x_movement = 0;
	public float gravitation = 0.09f;
	public float floor_height = -0.5f;
	public int friction = 20;
	
	private Scale scale = new Scale(0, 0, 0);
	public Scale current_scale = new Scale(0.2f, 0.2f, 0.2f);
	
	private Position pos;
	public Position current_pos = new Position(0, 0, 0);
	
	private Rotation rot = new Rotation(0, 0, 0);
	public Rotation current_rot = new Rotation(0, 0, 0);
	
	
	
	
	public void calculate(){
		pos = new Position(0, 0 ,0); 
		
		// Jump
		if (jumping > -20) {
			pos.y += 0.002f*jumping;
	    	jumping -= 1;
	    } else {	
	    	pos.y += -Math.min(gravitation*Math.abs(0.25/(floor_height-current_pos.y)), current_pos.y);
	    }
		
		
		// Friction
		if (pos.y == 0) {
			friction = 2;
		} else {
			friction = 40;
		}
		
		
		if (rot.x != 0) {
			current_rot.x += (0.0174532925199433*rot.speed) * Math.signum(rot.x);
			rot.x -= 1 * Math.signum(rot.x);
		} if (rot.y != 0) {
			current_rot.y += (0.0174532925199433*rot.speed) * Math.signum(rot.y);
			rot.y -= 1 * Math.signum(rot.y);
		} if (rot.z != 0) {
			current_rot.z += (0.0174532925199433*rot.speed) * Math.signum(rot.z);
			rot.z -= 1 * Math.signum(rot.z);
		}
		
		if (scale.x != 0) {
			current_scale.x += (0.02*rot.speed) * Math.signum(scale.x);
			System.out.println(scale.x);
			scale.x -= 1 * Math.signum(scale.x);
		} if (scale.y != 0) {
			current_scale.y += (0.02*rot.speed) * Math.signum(scale.y);
			scale.y -= 1 * Math.signum(scale.y);
		} if (scale.z != 0) {
			current_scale.z += (0.02*rot.speed) * Math.signum(scale.z);
			scale.z -= 1 * Math.signum(scale.z);
		}
		
		
		pos.x = 0.002f * x_movement;
		x_movement += -x_movement * 1/friction;
		
		translatePos(pos);
	}
	
	public void translatePos(Position posT) {
		this.current_pos.x += posT.x; this.current_pos.y += posT.y; this.current_pos.z += posT.z;
	}
	
	public void translateRot(int xx, int yy, int zz, int speed) {
		if(this.rot.x == 0) {
			this.rot.x += xx/speed;
			this.rot.speed = speed;
		} if(this.rot.y == 0) {
			this.rot.y += yy/speed;
			this.rot.speed = speed;
		} if(this.rot.y == 0) {
			this.rot.z += zz/speed;
			this.rot.speed = speed;
		}
	}
	
	public void translateScale(float xx, float yy, float zz, int speed) {
		if(this.scale.x == 0) {
			this.scale.x += xx/speed;
			this.scale.speed = speed;
		} if(this.scale.y == 0) {
			this.scale.y += yy/speed;
			this.scale.speed = speed;
		} if(this.scale.y == 0) {
			this.scale.z += zz/speed;
			this.scale.speed = speed;
		}
	}
}
