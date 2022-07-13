package renderers;

import static com.jogamp.opengl.GL.GL_FLOAT;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLDebugListener;
import com.jogamp.opengl.GLDebugMessage;
import com.jogamp.opengl.util.GLBuffers;

import components.ShaderComponent;
import framework.Semantic;

public class MeshRenderer extends BaseRenderer  {
	
	private ShaderComponent m_ShaderComponent;
	
	private float m_cube_size = 0.1f;
	
	private float[] m_vertices = { 
			-m_cube_size, -m_cube_size, m_cube_size, (float) Math.random(), (float) Math.random(), (float) Math.random(),
			-m_cube_size, m_cube_size, m_cube_size, (float) Math.random(), (float) Math.random(), (float) Math.random(),
			m_cube_size, -m_cube_size, m_cube_size, (float) Math.random(), (float) Math.random(), (float) Math.random(),
			m_cube_size, m_cube_size, m_cube_size, (float) Math.random(), (float) Math.random(), (float) Math.random(),
			
			-m_cube_size, -m_cube_size, -m_cube_size, (float) Math.random(), (float) Math.random(), (float) Math.random(),
			-m_cube_size, m_cube_size, -m_cube_size, (float) Math.random(), (float) Math.random(), (float) Math.random(),
			m_cube_size, -m_cube_size, -m_cube_size, (float) Math.random(), (float) Math.random(), (float) Math.random(),
			m_cube_size, m_cube_size, -m_cube_size, (float) Math.random(), (float) Math.random(), (float) Math.random(),
			/*
			-m_cube_size, m_cube_size, m_cube_size, (float) Math.random(), (float) Math.random(), (float) Math.random(),
			m_cube_size, -m_cube_size, -m_cube_size, (float) Math.random(), (float) Math.random(), (float) Math.random(),
			m_cube_size, -m_cube_size, m_cube_size, (float) Math.random(), (float) Math.random(), (float) Math.random(),
			m_cube_size, m_cube_size, -m_cube_size, (float) Math.random(), (float) Math.random(), (float) Math.random(),
			m_cube_size, m_cube_size, m_cube_size, (float) Math.random(), (float) Math.random(), (float) Math.random(),
			*/
        };
	
	private float[] m_colors = {
				0.0f, 0.0f, 1.0f,
	            0.0f, 0.0f, 1.0f,
	            0.0f, 0.0f, 1.0f,
	            0.0f, 0.0f, 1.0f 
        };
	
	private short[] m_indices = {
				1, 0, 2,
				3, 2, 1,
				
				5, 4, 6,
				7, 6, 5
            };
	
	
	 private interface Buffer {

	        int VERTEX = 0;
	        int ELEMENT = 1;
	        int GLOBAL_MATRICES = 2;
	        int MODEL_MATRIX = 3;
	        int MAX = 4;
	    }
	
	private IntBuffer bufferName = GLBuffers.newDirectIntBuffer(4);
    private IntBuffer vertexArrayName = GLBuffers.newDirectIntBuffer(1);

    private FloatBuffer clearColor = GLBuffers.newDirectFloatBuffer(4);
    private FloatBuffer clearDepth = GLBuffers.newDirectFloatBuffer(1);
	
	IntBuffer genericBuffer = Buffers.newDirectIntBuffer(2);
	
	private boolean bug1287 = true;
	
	private int m_positionAttribute;
	private int m_colorAttribute;
	
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
		
		initDebug(getGLobject());

        initBuffers(getGLobject());

        initVertexArray(getGLobject());
		
		m_ShaderComponent = new ShaderComponent(getGLobject(), "src/resources/shaders", "default", "default");
		
		setPositionAttribute(getGLobject().glGetAttribLocation(getProgramId(), "inPosition"));
		setColorAttribute(getGLobject().glGetAttribLocation(getProgramId(), "inColor"));
	}
	
	@Override
	public void display(GLAutoDrawable drawable) {

        getGLobject().glClearBufferfv(GL4.GL_COLOR, 0, clearColor.put(0, 1f).put(1, .5f).put(2, 0f).put(3, 1f));
        getGLobject().glClearBufferfv(GL4.GL_DEPTH, 0, clearDepth.put(0, 1f));
        
        

        getGLobject().glUseProgram(m_ShaderComponent.name);
        getGLobject().glBindVertexArray(vertexArrayName.get(0));

        getGLobject().glBindBufferBase(
                GL4.GL_UNIFORM_BUFFER,
                Semantic.Uniform.TRANSFORM0,
                bufferName.get(Buffer.GLOBAL_MATRICES));

        getGLobject().glBindBufferBase(
        		GL4.GL_UNIFORM_BUFFER,
                Semantic.Uniform.TRANSFORM1,
                bufferName.get(Buffer.MODEL_MATRIX));

        getGLobject().glDrawElements(
        		GL4.GL_TRIANGLES,
                m_indices.length,
                GL4.GL_UNSIGNED_SHORT,
                0);

        getGLobject().glUseProgram(m_ShaderComponent.name);
        getGLobject().glBindVertexArray(0);
		
	}
	
	@Override
    public void dispose(GLAutoDrawable drawable) {

        GL4 gl = drawable.getGL().getGL4();

        gl.glUnmapNamedBuffer(bufferName.get(Buffer.GLOBAL_MATRICES));
        gl.glUnmapNamedBuffer(bufferName.get(Buffer.MODEL_MATRIX));

        gl.glDeleteProgram(m_ShaderComponent.name);
        gl.glDeleteVertexArrays(1, vertexArrayName);
        gl.glDeleteBuffers(Buffer.MAX, bufferName);
    }
	
	private void initBuffers(GL4 gl) {

        FloatBuffer vertexBuffer = GLBuffers.newDirectFloatBuffer(m_vertices);
        ShortBuffer elementBuffer = GLBuffers.newDirectShortBuffer(m_indices);

        gl.glCreateBuffers(Buffer.MAX, bufferName);

        if (!bug1287) {

            gl.glNamedBufferStorage(bufferName.get(Buffer.VERTEX), vertexBuffer.capacity() * Float.BYTES, vertexBuffer,
                    GL4.GL_STATIC_DRAW);
            gl.glNamedBufferStorage(bufferName.get(Buffer.ELEMENT), elementBuffer.capacity() * Short.BYTES,
                    elementBuffer, GL4.GL_STATIC_DRAW);

            gl.glNamedBufferStorage(bufferName.get(Buffer.GLOBAL_MATRICES), 16 * 4 * 2, null, GL4.GL_MAP_WRITE_BIT);
            gl.glNamedBufferStorage(bufferName.get(Buffer.MODEL_MATRIX), 16 * 4, null, GL4.GL_MAP_WRITE_BIT);

        } else {

            gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, bufferName.get(Buffer.VERTEX));
            gl.glBufferStorage(GL4.GL_ARRAY_BUFFER, vertexBuffer.capacity() * Float.BYTES, vertexBuffer, 0);
            gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, 0);

            gl.glBindBuffer(GL4.GL_ELEMENT_ARRAY_BUFFER, bufferName.get(Buffer.ELEMENT));
            gl.glBufferStorage(GL4.GL_ELEMENT_ARRAY_BUFFER, elementBuffer.capacity() * Short.BYTES, elementBuffer, 0);
            gl.glBindBuffer(GL4.GL_ELEMENT_ARRAY_BUFFER, 0);


            IntBuffer uniformBufferOffset = GLBuffers.newDirectIntBuffer(1);
            gl.glGetIntegerv(GL4.GL_UNIFORM_BUFFER_OFFSET_ALIGNMENT, uniformBufferOffset);
            int globalBlockSize = Math.max(16 * 4 * 2, uniformBufferOffset.get(0));
            int modelBlockSize = Math.max(16 * 4, uniformBufferOffset.get(0));

            gl.glBindBuffer(GL4.GL_UNIFORM_BUFFER, bufferName.get(Buffer.GLOBAL_MATRICES));
            gl.glBufferStorage(GL4.GL_UNIFORM_BUFFER, globalBlockSize, null, GL4.GL_MAP_WRITE_BIT | GL4.GL_MAP_PERSISTENT_BIT | GL4.GL_MAP_COHERENT_BIT);
            gl.glBindBuffer(GL4.GL_UNIFORM_BUFFER, 0);

            gl.glBindBuffer(GL4.GL_UNIFORM_BUFFER, bufferName.get(Buffer.MODEL_MATRIX));
            gl.glBufferStorage(GL4.GL_UNIFORM_BUFFER, modelBlockSize, null, GL4.GL_MAP_WRITE_BIT | GL4.GL_MAP_PERSISTENT_BIT | GL4.GL_MAP_COHERENT_BIT);
            gl.glBindBuffer(GL4.GL_UNIFORM_BUFFER, 0);
        }

    }
	
	
	private void initVertexArray(GL4 gl) {

        gl.glCreateVertexArrays(1, vertexArrayName);

        gl.glVertexArrayAttribBinding(vertexArrayName.get(0), Semantic.Attr.POSITION, Semantic.Stream.A);
        gl.glVertexArrayAttribBinding(vertexArrayName.get(0), Semantic.Attr.COLOR, Semantic.Stream.A);

        gl.glVertexArrayAttribFormat(vertexArrayName.get(0), Semantic.Attr.POSITION, 3, GL_FLOAT, false, 0);
        gl.glVertexArrayAttribFormat(vertexArrayName.get(0), Semantic.Attr.COLOR, 3, GL_FLOAT, false, 3 * 4);

        gl.glEnableVertexArrayAttrib(vertexArrayName.get(0), Semantic.Attr.POSITION);
        gl.glEnableVertexArrayAttrib(vertexArrayName.get(0), Semantic.Attr.COLOR);

        gl.glVertexArrayElementBuffer(vertexArrayName.get(0), bufferName.get(Buffer.ELEMENT));
        gl.glVertexArrayVertexBuffer(vertexArrayName.get(0), Semantic.Stream.A, bufferName.get(Buffer.VERTEX), 0, (3 + 3) * 4);
    }
	
	private void initDebug(GL4 gl) {

        getWindow().getContext().addGLDebugListener(new GLDebugListener() {
            @Override
            public void messageSent(GLDebugMessage event) {
                System.out.println(event);
            }
        });

        gl.glDebugMessageControl(
                GL4.GL_DONT_CARE,
                GL4.GL_DONT_CARE,
                GL4.GL_DONT_CARE,
                0,
                null,
                false);

        gl.glDebugMessageControl(
        		GL4.GL_DONT_CARE,
                GL4.GL_DONT_CARE,
                GL4.GL_DEBUG_SEVERITY_HIGH,
                0,
                null,
                true);

        gl.glDebugMessageControl(
        		GL4.GL_DONT_CARE,
                GL4.GL_DONT_CARE,
                GL4.GL_DEBUG_SEVERITY_MEDIUM,
                0,
                null,
                true);
    }
	
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
