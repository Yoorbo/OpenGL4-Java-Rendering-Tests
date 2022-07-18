package base;

import renderers.ArrayRenderer;
import renderers.BaseRenderer;
import renderers.MeshRenderer;

public class Main {
	
	private BaseRenderer m_renderer;
	
	public static void main(String[] args) {
		Main main = new Main();
		main.setRenderer(new ArrayRenderer("Mesh Renderer", new MeshRenderer.ScreenDimension(1920, 1080)));
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
