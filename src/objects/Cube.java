package objects;

import datastructures.Position;

public class Cube extends BaseObject {
	
	private float[] debug_vertices;
	
	public Cube() {
		init();
	}
	
	public Cube(String objectid) {
		id = objectid;
		init();
	}
	
	public Cube(String objectid, boolean is_in_chunk) {
		id = objectid;
		this.in_chunk = is_in_chunk;
		init();
	}
	
	public Cube(String objectid, boolean is_in_chunk, Position setnewpos) {
		id = objectid;
		this.in_chunk = is_in_chunk;
		this.pos = setnewpos;
		init();
	}
	
	private void init() {
		/*
		m_vertices = new float[]{ 
				-scale.x + getPos().x, -scale.y + getPos().y, scale.z + getPos().z, (float) Math.random(), (float) Math.random(), (float) Math.random(),
				-scale.x + getPos().x, scale.y + getPos().y, scale.z + getPos().z, (float) Math.random(), (float) Math.random(), (float) Math.random(),
				scale.x + getPos().x, -scale.y + getPos().y, scale.z + getPos().z, (float) Math.random(), (float) Math.random(), (float) Math.random(),
				scale.x + getPos().x, scale.y + getPos().y, scale.z + getPos().z, (float) Math.random(), (float) Math.random(), (float) Math.random(),
				
				-scale.x + getPos().x, -scale.y + getPos().y, -scale.z + getPos().z, (float) Math.random(), (float) Math.random(), (float) Math.random(),
				-scale.x + getPos().x, scale.y + getPos().y, -scale.z + getPos().z, (float) Math.random(), (float) Math.random(), (float) Math.random(),
				scale.x + getPos().x, -scale.y + getPos().y, -scale.z + getPos().z, (float) Math.random(), (float) Math.random(), (float) Math.random(),
				scale.x + getPos().x, scale.y + getPos().y, -scale.z + getPos().z, (float) Math.random(), (float) Math.random(), (float) Math.random(),
	       };
	       */
		
		m_vertices = new float[]{ 
				getPos().x, getPos().y, getPos().z, (float) Math.random(), (float) Math.random(), (float) Math.random(),
				getPos().x, getPos().y, scale.z + getPos().z, (float) Math.random(), (float) Math.random(), (float) Math.random(),
				getPos().x, scale.y + getPos().y, getPos().z, (float) Math.random(), (float) Math.random(), (float) Math.random(),
				getPos().x, scale.y + getPos().y, scale.z + getPos().z, (float) Math.random(), (float) Math.random(), (float) Math.random(),
				
				scale.x + getPos().x, getPos().y, getPos().z, (float) Math.random(), (float) Math.random(), (float) Math.random(),
				scale.x + getPos().x, getPos().y, scale.z + getPos().z, (float) Math.random(), (float) Math.random(), (float) Math.random(),
				scale.x + getPos().x, scale.y + getPos().y, getPos().z, (float) Math.random(), (float) Math.random(), (float) Math.random(),
				scale.x + getPos().x, scale.y + getPos().y, scale.z + getPos().z, (float) Math.random(), (float) Math.random(), (float) Math.random(),
	       };
		
		setDebug_vertices(new float[]{ 
				getPos().x, getPos().y, getPos().z, 0.0f, 0.0f, 1.0f,
				getPos().x, getPos().y, scale.z + getPos().z, 0.0f, 0.0f, 1.0f,
				getPos().x, scale.y + getPos().y, getPos().z, 0.0f, 0.0f, 1.0f,
				getPos().x, scale.y + getPos().y, scale.z + getPos().z, 0.0f, 0.0f, 1.0f,
				
				scale.x + getPos().x, getPos().y, getPos().z, 0.0f, 0.0f, 1.0f,
				scale.x + getPos().x, getPos().y, scale.z + getPos().z, 0.0f, 0.0f, 1.0f,
				scale.x + getPos().x, scale.y + getPos().y, getPos().z, 0.0f, 0.0f, 1.0f,
				scale.x + getPos().x, scale.y + getPos().y, scale.z + getPos().z, 0.0f, 0.0f, 1.0f,
	       });
		
		/*
		
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
	        */
		
		
		m_indices = new short[] {
				0, 1, 3,
				0, 2, 3,
				
				1, 7, 5,
				1, 3, 7,
						
				5, 6, 4,
				5, 6, 7,
				
				5, 0, 1,
				5, 4, 0,
				
				4, 2, 0,
				4, 6, 2,

				3, 2, 6,
				3, 6, 7
	        };
	}
	
	protected void recalc() {
		/*
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
	       */
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
