package renderers;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;

import components.ShaderComponent;

public class MeshRenderer extends BaseRenderer  {
	
	private ShaderComponent m_ShaderComponent;
	
	private float[] m_vertices = { 
				0.0f, 0.0f, 0.0f,
	            1.0f, 0.0f, 0.0f,
	            1.0f, 1.0f, 0.0f,
	            0.0f, 1.0f, 0.0f 
            };
	
	private float[] m_colors = {
				0.0f, 0.0f, 1.0f,
	            0.0f, 0.0f, 1.0f,
	            0.0f, 0.0f, 1.0f,
	            0.0f, 0.0f, 1.0f 
        };
	
	private int[] m_indices = {
				0, 1, 2,
	            2, 0, 3 
            };
	
	
	FloatBuffer vertexBuffer = Buffers.newDirectFloatBuffer(m_vertices.length);
	IntBuffer indexBuffer = Buffers.newDirectIntBuffer(m_indices.length);
	FloatBuffer colorBuffer = Buffers.newDirectFloatBuffer(m_colors.length);
	
	IntBuffer genericBuffer = Buffers.newDirectIntBuffer(2);
	
	private int m_positionAttribute;
	private int m_colorAttribute;
	
	private IntBuffer vertexArray = IntBuffer.allocate(1);
	
	// Constructor

	public MeshRenderer(String title, ScreenDimension dimensions) {
		super(title, dimensions);
	}
	
	// Functions
	
	@Override
	public void init(GLAutoDrawable drawable) {
		setDrawable(drawable);
		setGLobject(drawable.getGL().getGL4());
		setProgramId(drawable.getGL().getGL4().glCreateProgram());
		
		m_ShaderComponent = new ShaderComponent(getGLobject(), "src/resources/shaders", "default", "default");
		
		vertexBuffer.put(m_vertices);
		indexBuffer.put(m_indices);
		colorBuffer.put(m_colors);
		
		setPositionAttribute(getGLobject().glGetAttribLocation(getProgramId(), "inPosition"));
		setColorAttribute(getGLobject().glGetAttribLocation(getProgramId(), "inColor"));
		
		getGLobject().glGenVertexArrays(1, vertexArray);
	}
	
	@Override
	public void display(GLAutoDrawable drawable) {
		getGLobject().glClear(GL4.GL_DEPTH_BUFFER_BIT | GL4.GL_COLOR_BUFFER_BIT);
		
		getGLobject().glBindVertexArray(vertexArray.get(0));

		getGLobject().glUseProgram(m_ShaderComponent.name);
		
		getGLobject().glGenBuffers(2, genericBuffer);
		
		getGLobject().glBindBuffer(GL4.GL_ARRAY_BUFFER, genericBuffer.get(0));
		
		getGLobject().glBufferData(GL4.GL_ARRAY_BUFFER, m_vertices.length, vertexBuffer, GL4.GL_STATIC_DRAW);
		
		getGLobject().glVertexAttribPointer(0, 3, GL4.GL_FLOAT, false, 0, 0);
		
		getGLobject().glEnableVertexAttribArray(0);
		
		getGLobject().glBindBuffer(GL4.GL_ARRAY_BUFFER, genericBuffer.get(1));
		
		getGLobject().glBufferData(GL4.GL_ARRAY_BUFFER, m_colors.length, colorBuffer, GL4.GL_STATIC_DRAW);
		
		getGLobject().glVertexAttribPointer(1, 3, GL4.GL_FLOAT, false, 0, 0);
		
		getGLobject().glEnableVertexAttribArray(1);
		
		getGLobject().glDrawArrays(GL4.GL_TRIANGLES, 0, 6);
		
	}
	
	
	/*
	private void render() {
		GL4 gl = getGLobject();
	    
	    gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
	    gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

	    // activating VBO
	    gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, bufID[0]);
	    int stride = 0;
	    gl.glVertexPointer(3, GL2.GL_FLOAT, stride, 0);
	    gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);

	    if(mode == 0) {
	      gl.glColor3f(1.0f,1.0f,1.0f);
	    }else{
	      gl.glColorPointer(3, GL2.GL_FLOAT, stride, 0);
	      gl.glEnableClientState(GL2.GL_COLOR_ARRAY);
	    }

	    // render VBO
	    gl.glDrawArrays(GL2.GL_POINTS, 0, bufSize);

	    gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
	    gl.glDisableClientState(GL2.GL_COLOR_ARRAY);
	   
	    gl.glFlush();
	  }
	}
	*/
	
	// MeshRenderer Getters & Setters

	/**
	 * @return the shaderComponent
	 */
	public ShaderComponent getShaderComponent() {
		return m_ShaderComponent;
	}

	/**
	 * @param shaderComponent the shaderComponent to set
	 */
	public void setShaderComponent(ShaderComponent shaderComponent) {
		this.m_ShaderComponent = shaderComponent;
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
	 * @return the indices
	 */
	public int[] getIndices() {
		return m_indices;
	}

	/**
	 * @param indices the indices to set
	 */
	public void setIndices(int[] indices) {
		this.m_indices = indices;
	}

	/**
	 * @return the colors
	 */
	public float[] getColors() {
		return m_colors;
	}

	/**
	 * @param colors the colors to set
	 */
	public void setColors(float[] colors) {
		this.m_colors = colors;
	}

	/**
	 * @return the positionAttribute
	 */
	public int getPositionAttribute() {
		return m_positionAttribute;
	}

	/**
	 * @param positionAttribute the positionAttribute to set
	 */
	public void setPositionAttribute(int positionAttribute) {
		this.m_positionAttribute = positionAttribute;
	}

	/**
	 * @return the colorAttribute
	 */
	public int getColorAttribute() {
		return m_colorAttribute;
	}

	/**
	 * @param colorAttribute the colorAttribute to set
	 */
	public void setColorAttribute(int colorAttribute) {
		this.m_colorAttribute = colorAttribute;
	}

	
}
