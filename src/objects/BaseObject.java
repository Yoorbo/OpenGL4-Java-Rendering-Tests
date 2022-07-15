package objects;

import components.BaseComponent;
import datastructures.Scale;

public abstract class BaseObject extends BaseComponent {
	
	protected Scale scale = new Scale();
	
	protected float[] m_vertices;
	
	protected short[] m_indices;
	
	public BaseObject() {
		
	}
	/**
	 * @return the indices
	 */
	public short[] getIndices() {
		return m_indices;
	}
	/**
	 * @param indices the indices to set
	 */
	public void setIndices(short[] indices) {
		this.m_indices = indices;
	}
	/**
	 * @return the vertices
	 */
	public float[] getVertices() {
		return m_vertices;
	}
	/**
	 * @param vertices the vertices to set
	 */
	public void setVertices(float[] vertices) {
		this.m_vertices = vertices;
	}
	/**
	 * @return the scale
	 */
	public Scale getScale() {
		return scale;
	}

}
