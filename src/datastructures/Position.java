package datastructures;

public class Position {
	public float x = 1.0f;
	public float y = 1.0f;
	public float z = 1.0f;
	
	public Position(float xx, float yy, float zz) {
		x = xx; y = yy; z = zz;
	}
	
	float[] asArray() {
		return new float[] {
				x, y, z
		};
	}
	
	float[] insertFront(float[] arrayToCombine) {
		float result[] = new float[3 + arrayToCombine.length];
		System.arraycopy(asArray(), 0, result, 0, 3);
		System.arraycopy(arrayToCombine, 0, result, 3, arrayToCombine.length);
		return result;
	}
	
	float[] insertBehind(float[] arrayToCombine) {
		float result[] = new float[3 + arrayToCombine.length];
		System.arraycopy(arrayToCombine, 0, result, 0, arrayToCombine.length);
		System.arraycopy(asArray(), 0, result, arrayToCombine.length, 3);
		return result;
	}
}
