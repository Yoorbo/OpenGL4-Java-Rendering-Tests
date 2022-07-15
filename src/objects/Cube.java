package objects;

import datastructures.Position;

public class Cube extends BaseObject {
	public Cube() {
		
		m_vertices = new float[]{ 
				-scale.x + getPos().x, -scale.y + getPos().y, scale.z + getPos().z, 1.0f, 0.5f, 1.0f,
				-scale.x + getPos().x, scale.y + getPos().y, scale.z + getPos().z, 0.0f, 1.0f, 0.0f,
				scale.x + getPos().x, -scale.y + getPos().y, scale.z + getPos().z, 0.7f, 1.0f, 1.0f,
				scale.x + getPos().x, scale.y + getPos().y, scale.z + getPos().z, 0.7f, 1.0f, 0.0f,
				
				-scale.x*2 + getPos().x, -scale.y*2 + getPos().y, -scale.z*2 + getPos().z, 1.0f, 0.5f, 1.0f,
				-scale.x*2 + getPos().x, scale.y*2 + getPos().y, -scale.z*2 + getPos().z, 0.0f, 1.0f, 0.0f,
				scale.x*2 + getPos().x, -scale.y*2 + getPos().y, -scale.z*2 + getPos().z, 0.7f, 1.0f, 1.0f,
				scale.x*2 + getPos().x, scale.y*2 + getPos().y, -scale.z*2 + getPos().z, 0.7f, 1.0f, 0.0f,
	       };
		
		m_indices = new short[] {
				1, 0, 2,
				3, 2, 1,
				
				5, 4, 6,
				7, 6, 5,
				
				2, 3, 6,
				7, 3, 6,
				
				0, 1, 4,
				5, 4, 1,
				
				3, 7, 1,
				5, 7, 1,
				
				0, 4, 6,
				2, 6, 0
	        };
	}
	
	/**
	 * @return the pos
	 */
	public Position getPos() {
		return pos;
	}
	/**
	 * @param pos the pos to set
	 */
	public void setPos(Position pos) {
		this.pos = pos;
		m_vertices = new float[]{ 
				-scale.x + getPos().x, -scale.y + getPos().y, scale.z + getPos().z, 1.0f, 0.5f, 1.0f,
				-scale.x + getPos().x, scale.y + getPos().y, scale.z + getPos().z, 0.0f, 1.0f, 0.0f,
				scale.x + getPos().x, -scale.y + getPos().y, scale.z + getPos().z, 0.7f, 1.0f, 1.0f,
				scale.x + getPos().x, scale.y + getPos().y, scale.z + getPos().z, 0.7f, 1.0f, 0.0f,
				
				-scale.x*2 + getPos().x, -scale.y*2 + getPos().y, -scale.z*2 + getPos().z, 1.0f, 0.5f, 1.0f,
				-scale.x*2 + getPos().x, scale.y*2 + getPos().y, -scale.z*2 + getPos().z, 0.0f, 1.0f, 0.0f,
				scale.x*2 + getPos().x, -scale.y*2 + getPos().y, -scale.z*2 + getPos().z, 0.7f, 1.0f, 1.0f,
				scale.x*2 + getPos().x, scale.y*2 + getPos().y, -scale.z*2 + getPos().z, 0.7f, 1.0f, 0.0f,
	       };
		
		m_indices = new short[] {
				1, 0, 2,
				3, 2, 1,
				
				5, 4, 6,
				7, 6, 5,
				
				2, 3, 6,
				7, 3, 6,
				
				0, 1, 4,
				5, 4, 1,
				
				3, 7, 1,
				5, 7, 1,
				
				0, 4, 6,
				2, 6, 0
	    };
	}
}
