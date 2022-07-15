package datastructures;

public abstract class AbstractVector {
	public float x = 1.0f;
	public float y = 1.0f;
	public float z = 1.0f;
	
	public AbstractVector(float xx, float yy, float zz) {
		x = xx; y = yy; z = zz;
	}
	
	public float[] asArray() {
		return new float[] {
				x, y, z
		};
	}
	
	public float[] insertFront(float[] arrayToCombine) {
		float result[] = new float[3 + arrayToCombine.length];
		System.arraycopy(asArray(), 0, result, 0, 3);
		System.arraycopy(arrayToCombine, 0, result, 3, arrayToCombine.length);
		return result;
	}
	
	public float[] insertBehind(float[] arrayToCombine) {
		float result[] = new float[3 + arrayToCombine.length];
		System.arraycopy(arrayToCombine, 0, result, 0, arrayToCombine.length);
		System.arraycopy(asArray(), 0, result, arrayToCombine.length, 3);
		return result;
	}
}
