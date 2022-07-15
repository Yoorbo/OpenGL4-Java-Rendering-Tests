package objects;

public class Cube extends BaseObject {
	public Cube() {
		scale.setAll(0.2f);
		
		m_vertices = new float[]{ 
				-scale.x, -scale.y, scale.z, 1.0f, 0.5f, 1.0f,
				-scale.x, scale.y, scale.z, 0.0f, 1.0f, 0.0f,
				scale.x, -scale.y, scale.z, 0.7f, 1.0f, 1.0f,
				scale.x, scale.y, scale.z, 0.7f, 1.0f, 0.0f,
				
				-scale.x*2, -scale.y*2, -scale.z*2, 1.0f, 0.5f, 1.0f,
				-scale.x*2, scale.y*2, -scale.z*2, 0.0f, 1.0f, 0.0f,
				scale.x*2, -scale.y*2, -scale.z*2, 0.7f, 1.0f, 1.0f,
				scale.x*2, scale.y*2, -scale.z*2, 0.7f, 1.0f, 0.0f,
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
