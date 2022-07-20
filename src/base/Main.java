package base;

import components.SceneComponent;
import renderers.ArrayRenderer;
import renderers.BaseRenderer;
import renderers.MeshRenderer;

public class Main {
	
	private BaseRenderer m_renderer;
	
	public static void main(String[] args) {
		Main main = new Main();
		//main.setRenderer(new ArrayRenderer("Array Renderer", new MeshRenderer.ScreenDimension(1000, 1000)));
		main.setRenderer(new SceneComponent("Scene Renderer", new SceneComponent.ScreenDimension(1000, 1000)));
	}
	
	// Getter & Setters

	/**
	 * @return the renderer
	 */
	public BaseRenderer getRenderer() {
		return m_renderer;
	}

	/**
	 * @param renderer the renderer to set
	 */
	public void setRenderer(BaseRenderer renderer) {
		this.m_renderer = renderer;
	}
}
