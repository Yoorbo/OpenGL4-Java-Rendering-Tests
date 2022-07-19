package objects;

import components.BaseComponent;
import datastructures.Position;
import datastructures.Scale;

public abstract class BaseObject extends BaseComponent {
	
	protected Scale scale = new Scale();
	protected Position pos = new Position(0, 0, 0);
	
	protected float[] m_vertices;
	
	protected short[] m_indices;
	
	protected float[] rotation;
	
	protected double rotAngle;
	
	protected String id;
	
	protected boolean in_chunk;
	public boolean debug = false;
	
	public BaseObject() {
		
	}
	
	public BaseObject(String objectid) {
		id = objectid;
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
		this.recalc();
		
	}
	
	public void translatePos(Position pos) {
		this.pos.x += pos.x; this.pos.y += pos.y; this.pos.z += pos.z;
		this.recalc();
	}
	
	public void changeScale(Scale addScale) {
		this.scale.x += addScale.x; this.scale.y += addScale.y; this.scale.z += addScale.z;
		this.recalc();
	}
	
	protected void recalc() {};
	
	public float[] verticesInsertBehind(float[] arrayToCombine) {
		float result[] = new float[getVertices().length + arrayToCombine.length];
		System.arraycopy(arrayToCombine, 0, result, 0, arrayToCombine.length);
		System.arraycopy(getVertices(), 0, result, arrayToCombine.length, getVertices().length);
		return result;
	}
	
	public short[] indicesInsertBehind(short[] arrayToCombine) {
		for (int iter = 0; iter < getIndices().length; iter++) {
			getIndices()[iter] += arrayToCombine.length/36*8;
		}
		short result[] = new short[getIndices().length + arrayToCombine.length];
		System.arraycopy(arrayToCombine, 0, result, 0, arrayToCombine.length);
		System.arraycopy(getIndices(), 0, result, arrayToCombine.length, getIndices().length);
		return result;
	}
	
	public short[] singleIndiceInsertBehind(short[] arrayToCombine, int index, int heigher) {
		short result[] = new short[1 + arrayToCombine.length];
		System.arraycopy(arrayToCombine, 0, result, 0, arrayToCombine.length);
		System.arraycopy(new short[] { (short) (getIndices()[index] +  heigher)  }, 0, result, arrayToCombine.length, 1);
		return result;
	}
}
