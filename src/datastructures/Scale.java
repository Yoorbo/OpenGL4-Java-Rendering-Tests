package datastructures;

public class Scale extends AbstractVector {

	public Scale() {
		super(1.0f, 1.0f, 1.0f);
	}
	
	public void setAll(float size) {
		x = size; y = size; z = size;
	}
}

