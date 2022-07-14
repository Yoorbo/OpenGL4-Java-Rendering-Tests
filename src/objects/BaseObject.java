package objects;

import components.BaseComponent;

public abstract class BaseObject extends BaseComponent {
	
	
	
	private float[] m_vertices = { 
			-1.0f*scale, -1.0f*scale, 1.0f*scale, 1.0f, 0.5f, 1.0f,
			-1.0f*scale, 1.0f*scale, 1.0f*scale, 0.0f, 1.0f, 0.0f,
			1.0f*scale, -1.0f*scale, 1.0f*scale, 0.7f, 1.0f, 1.0f,
			1.0f*scale, 1.0f*scale, 1.0f*scale, 0.7f, 1.0f, 0.0f,
			
			-1.0f*scale*2, -1.0f*scale*2, -1.0f*scale*2, 1.0f, 0.5f, 1.0f,
			-1.0f*scale*2, 1.0f*scale*2, -1.0f*scale*2, 0.0f, 1.0f, 0.0f,
			1.0f*scale*2, -1.0f*scale*2, -1.0f*scale*2, 0.7f, 1.0f, 1.0f,
			1.0f*scale*2, 1.0f*scale*2, -1.0f*scale*2, 0.7f, 1.0f, 0.0f,
       };
	
	private short[] m_indices = {
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
	public BaseObject() {
		
	}
}
