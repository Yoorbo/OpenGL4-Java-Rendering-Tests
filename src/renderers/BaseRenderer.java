package renderers;

import components.WindowComponent;

public abstract class BaseRenderer extends WindowComponent {

	public BaseRenderer(String title, ScreenDimension dimensions) {
		super(title, dimensions);
	}
	
	public void openWindow() {
		init(getWindow());
	}
	
}
