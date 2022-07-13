package components;

import static com.jogamp.opengl.GL4.GL_FRAGMENT_SHADER;
import static com.jogamp.opengl.GL4.GL_VERTEX_SHADER;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.util.glsl.ShaderCode;
import com.jogamp.opengl.util.glsl.ShaderProgram;

public class ShaderComponent extends BaseComponent{
	public int name = 0;
	
	private String m_vertexShaderCode;
	private String m_fragmentShaderCode;

    public ShaderComponent(GL4 gl, String root, String vertex, String fragment) {

        ShaderCode vertShader = ShaderCode.create(gl, GL_VERTEX_SHADER, this.getClass(), root, null, vertex,
                "vert", null, true);
        ShaderCode fragShader = ShaderCode.create(gl, GL_FRAGMENT_SHADER, this.getClass(), root, null, fragment,
                "frag", null, true);

        ShaderProgram shaderProgram = new ShaderProgram();

        shaderProgram.add(vertShader);
        shaderProgram.add(fragShader);

        shaderProgram.init(gl);

        name = shaderProgram.program();

        shaderProgram.link(gl, System.err);
    }

	/**
	 * @return the vertexShaderCode
	 */
	public String getVertexShaderCode() {
		return m_vertexShaderCode;
	}

	/**
	 * @param vertexShaderCode the vertexShaderCode to set
	 */
	public void setVertexShaderCode(String vertexShaderCode) {
		this.m_vertexShaderCode = vertexShaderCode;
	}

	/**
	 * @return the fragmentShaderCode
	 */
	public String getFragmentShaderCode() {
		return m_fragmentShaderCode;
	}

	/**
	 * @param fragmentShaderCode the fragmentShaderCode to set
	 */
	public void setFragmentShaderCode(String fragmentShaderCode) {
		this.m_fragmentShaderCode = fragmentShaderCode;
	}
}
