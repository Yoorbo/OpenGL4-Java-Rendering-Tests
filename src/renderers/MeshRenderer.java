package renderers;

import static com.jogamp.opengl.GL.GL_FLOAT;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.charset.StandardCharsets;

import com.jogamp.common.nio.Buffers;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLDebugListener;
import com.jogamp.opengl.GLDebugMessage;
import com.jogamp.opengl.math.FloatUtil;
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
	
	private int temp_toggle = 1;
	
	
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
    
    private FloatBuffer vertexBuffer = GLBuffers.newDirectFloatBuffer(m_vertices);
    private ShortBuffer elementBuffer = GLBuffers.newDirectShortBuffer(m_indices);
	
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
	}
	
	@Override
	public void display(GLAutoDrawable drawable) {
		
		// view matrix
        {
            float[] view = FloatUtil.makeIdentity(new float[16]);
            for (int i = 0; i < 16; i++)
                globalMatricesPointer.putFloat(16 * 4 + i * 4, view[i]);
        }

        getGLobject().glClearBufferfv(GL4.GL_COLOR, 0, clearColor.put(0, 1f).put(1, .5f).put(2, 0f).put(3, 1f));
        getGLobject().glClearBufferfv(GL4.GL_DEPTH, 0, clearDepth.put(0, 1f));
        
        {
            long now = System.currentTimeMillis();
            float diff = (float) (now - getStart()) / 1_000;

            float[] scale = FloatUtil.makeScale(new float[16], true, 0.5f, 0.5f, 0.5f);
            float[] rotateZ = FloatUtil.makeRotationAxis(new float[16], 0, diff, 0f, 0f, 1f, new float[3]);
            float[] model = FloatUtil.multMatrix(scale, rotateZ);
            modelMatrixPointer.asFloatBuffer().put(model);
        }
        

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

        getGLobject().glUseProgram(0);
        getGLobject().glBindVertexArray(0);
        
        getGLobject().glFlush();
		
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
	
	@Override
    public void keyPressed(KeyEvent e) {  
		GL4 gl = getGLobject();
        switch (e.getKeyCode()) {
        case KeyEvent.VK_ESCAPE:
        	new Thread(() -> {
                getWindow().destroy();
            }).start();
        	break;
        case KeyEvent.VK_PAGE_UP:
        	
        	getIndicesBuffer().rewind();
        	
        	switch(temp_toggle) {
        	case 1:
        		setIndicesBuffer(new short[] {
            			1, 0, 2,
        				//3, 2, 1,
                });
        		break;
        	case 2:
        		setIndicesBuffer(new short[] {
        				1, 0, 2,
        				3, 2, 1,
                });
        		break;
        	case 3:
        		setIndicesBuffer(new short[] {
        				5, 4, 6,
                });
        		break;
        	case 4:
        		setIndicesBuffer(new short[] {
        				5, 4, 6,
        				7, 6, 5
                });
        		temp_toggle = 0;
        		break;
        	}
        	temp_toggle += 1;
        	
        	System.out.println(getIndicesBuffer().get(1));
        	
        	getIndicesBuffer().rewind();
        	
        	
        	
        	
        	getVertexBuffer().rewind();
        	
        	setVertexBuffer(new float[] { 
        			-m_cube_size, -m_cube_size, m_cube_size, (float) Math.random(), (float) Math.random(), (float) Math.random(),
        			-m_cube_size, m_cube_size, m_cube_size, (float) Math.random(), (float) Math.random(), (float) Math.random(),
        			m_cube_size, -m_cube_size, m_cube_size, (float) Math.random(), (float) Math.random(), (float) Math.random(),
        			m_cube_size, m_cube_size, m_cube_size, (float) Math.random(), (float) Math.random(), (float) Math.random(),
        			
        			-m_cube_size*2, -m_cube_size*2, -m_cube_size, (float) Math.random(), (float) Math.random(), (float) Math.random(),
        			-m_cube_size*2, m_cube_size*2, -m_cube_size, (float) Math.random(), (float) Math.random(), (float) Math.random(),
        			m_cube_size*2, -m_cube_size*2, -m_cube_size, (float) Math.random(), (float) Math.random(), (float) Math.random(),
        			m_cube_size*2, m_cube_size*2, -m_cube_size, (float) Math.random(), (float) Math.random(), (float) Math.random(),
                });
        	
        	getVertexBuffer().rewind();
        	
        	System.out.println(getVertexBuffer().get(1));
        	
        	reloadBuffers(gl);
        }
    }
	
	private void initBuffers(GL4 gl) {

        gl.glCreateBuffers(Buffer.MAX, bufferName);

        if (!bug1287) {

            gl.glNamedBufferStorage(bufferName.get(Buffer.VERTEX), getVertexBuffer().capacity() * Float.BYTES, getVertexBuffer(),
                    GL4.GL_STREAM_DRAW);
            gl.glNamedBufferStorage(bufferName.get(Buffer.ELEMENT), getIndicesBuffer().capacity() * Short.BYTES,
            		getIndicesBuffer(), GL4.GL_STREAM_DRAW);

            gl.glNamedBufferStorage(bufferName.get(Buffer.GLOBAL_MATRICES), 16 * 4 * 2, null, GL4.GL_MAP_WRITE_BIT);
            gl.glNamedBufferStorage(bufferName.get(Buffer.MODEL_MATRIX), 16 * 4, null, GL4.GL_MAP_WRITE_BIT);

        } else {

            gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, bufferName.get(Buffer.VERTEX));
            gl.glBufferData(GL4.GL_ARRAY_BUFFER, getVertexBuffer().capacity() * Float.BYTES, getVertexBuffer(), GL4.GL_STREAM_DRAW);
            gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, 0);

            gl.glBindBuffer(GL4.GL_ELEMENT_ARRAY_BUFFER, bufferName.get(Buffer.ELEMENT));
            gl.glBufferData(GL4.GL_ELEMENT_ARRAY_BUFFER, getIndicesBuffer().capacity() * Short.BYTES, getIndicesBuffer(), GL4.GL_STREAM_DRAW);
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
            
         // map the transform buffers and keep them mapped
            globalMatricesPointer = gl.glMapNamedBufferRange(
                    bufferName.get(Buffer.GLOBAL_MATRICES),
                    0,
                    16 * 4 * 2,
                    GL4.GL_MAP_WRITE_BIT | GL4.GL_MAP_PERSISTENT_BIT | GL4.GL_MAP_COHERENT_BIT | GL4.GL_MAP_INVALIDATE_BUFFER_BIT); // flags

            modelMatrixPointer = gl.glMapNamedBufferRange(
                    bufferName.get(Buffer.MODEL_MATRIX),
                    0,
                    16 * 4,
                    GL4.GL_MAP_WRITE_BIT | GL4.GL_MAP_PERSISTENT_BIT | GL4.GL_MAP_COHERENT_BIT | GL4.GL_MAP_INVALIDATE_BUFFER_BIT);
            
            System.out.println(bufferName.get(Buffer.VERTEX));
        }

    }
	
	
	private void reloadBuffers(GL4 gl) {
		
		gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, bufferName.get(Buffer.VERTEX));
    	gl.glBufferSubData(GL4.GL_ARRAY_BUFFER, 0, getVertexBuffer().capacity() * Float.BYTES, getVertexBuffer());
    	gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, 0);

        gl.glBindBuffer(GL4.GL_ELEMENT_ARRAY_BUFFER, bufferName.get(Buffer.ELEMENT));
    	gl.glBufferSubData(GL4.GL_ARRAY_BUFFER, 0, getIndicesBuffer().capacity() * Short.BYTES, getIndicesBuffer());
    	gl.glBindBuffer(GL4.GL_ELEMENT_ARRAY_BUFFER, 0);
    	
    	bufferName.rewind();
    	vertexArrayName.rewind();
       
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

	/**
	 * @return the vertexBuffer
	 */
	public FloatBuffer getVertexBuffer() {
		return vertexBuffer;
	}

	/**
	 * @param vertexBuffer the vertexBuffer to set
	 */
	public void setVertexBuffer(float[] vertices) {
		setVertices(vertices);
		this.vertexBuffer = GLBuffers.newDirectFloatBuffer(vertices);
	}

	/**
	 * @return the elementBuffer
	 */
	public ShortBuffer getIndicesBuffer() {
		return elementBuffer;
	}

	/**
	 * @param elementBuffer the elementBuffer to set
	 */
	public void setIndicesBuffer(short[] indices) {
		setIndices(indices);
		this.elementBuffer = GLBuffers.newDirectShortBuffer(indices);
	}

	
}
