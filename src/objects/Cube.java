package objects;

public class Cube extends BaseObject {
	
	private float[] debug_vertices;
	private boolean debug = false;
	
	public Cube() {
		
		m_vertices = new float[]{ 
				-scale.x + getPos().x, -scale.y + getPos().y, scale.z + getPos().z, 1.0f, 0.5f, 1.0f,
				-scale.x + getPos().x, scale.y + getPos().y, scale.z + getPos().z, 0.0f, 1.0f, 0.0f,
				scale.x + getPos().x, -scale.y + getPos().y, scale.z + getPos().z, 0.7f, 1.0f, 1.0f,
				scale.x + getPos().x, scale.y + getPos().y, scale.z + getPos().z, 0.7f, 1.0f, 0.0f,
				
				-scale.x + getPos().x, -scale.y + getPos().y, -scale.z + getPos().z, 1.0f, 0.5f, 1.0f,
				-scale.x + getPos().x, scale.y + getPos().y, -scale.z + getPos().z, 0.0f, 1.0f, 0.0f,
				scale.x + getPos().x, -scale.y + getPos().y, -scale.z + getPos().z, 0.7f, 1.0f, 1.0f,
				scale.x + getPos().x, scale.y + getPos().y, -scale.z + getPos().z, 0.7f, 1.0f, 0.0f,
	       };
		
		
		setDebug_vertices(new float[]{ 
				-scale.x + getPos().x, -scale.y + getPos().y, scale.z + getPos().z, 1.0f, 0.0f, 0.0f,
				-scale.x + getPos().x, scale.y + getPos().y, scale.z + getPos().z, 1.0f, 0.0f, 0.0f,
				scale.x + getPos().x, -scale.y + getPos().y, scale.z + getPos().z, 1.0f, 0.0f, 0.0f,
				scale.x + getPos().x, scale.y + getPos().y, scale.z + getPos().z, 1.0f, 0.0f, 0.0f,
				
				-scale.x + getPos().x, -scale.y + getPos().y, -scale.z + getPos().z, 0.0f, 0.0f, 1.0f,
				-scale.x + getPos().x, scale.y + getPos().y, -scale.z + getPos().z, 0.0f, 0.0f, 1.0f,
				scale.x + getPos().x, -scale.y + getPos().y, -scale.z + getPos().z, 0.0f, 0.0f, 1.0f,
				scale.x + getPos().x, scale.y + getPos().y, -scale.z + getPos().z, 0.0f, 0.0f, 1.0f,
	       });
		
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
	
	protected void recalc() {
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
	}

	/**
	 * @return the debug_vertices
	 */
	public float[] getDebug_vertices() {
		return debug_vertices;
	}

	/**
	 * @param debug_vertices the debug_vertices to set
	 */
	public void setDebug_vertices(float[] debug_vertices) {
		this.debug_vertices = debug_vertices;
	}
	
	public float[] getVertices() {
		if (debug) {
			return debug_vertices;
		}
		return m_vertices;
	}
}
