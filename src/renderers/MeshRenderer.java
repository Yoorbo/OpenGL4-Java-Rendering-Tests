package renderers;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import com.jogamp.common.nio.Buffers;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLDebugListener;
import com.jogamp.opengl.GLDebugMessage;
import com.jogamp.opengl.math.FloatUtil;
import com.jogamp.opengl.util.GLBuffers;

import components.PhysicsComponent;
import components.ShaderComponent;
import datastructures.Position;
import datastructures.Scale;
import framework.Semantic;
import objects.Cube;

public class MeshRenderer extends BaseRenderer  {
	
	private PhysicsComponent m_PhysicsComponent;
	private ShaderComponent m_ShaderComponent;
	private Cube cube;
	
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
    
    private FloatBuffer vertexBuffer;
    private ShortBuffer elementBuffer;
    
    private FloatBuffer matBuffer = GLBuffers.newDirectFloatBuffer(16);
    private ByteBuffer globalMatricesPointer, modelMatrixPointer;
    
    private long start;
    

	
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
		GL4 gl = drawable.getGL().getGL4();
		setDrawable(drawable);
		setGLobject(gl);
		setProgramId(gl.glCreateProgram());
		start = System.currentTimeMillis();
		
		
		cube = new Cube();
		cube.getScale().setAll(0.2f);
		m_PhysicsComponent = new PhysicsComponent();
	
		cube.setPos(new Position(0, 0, 0));
		
	    vertexBuffer = GLBuffers.newDirectFloatBuffer(cube.getVertices());
	    elementBuffer = GLBuffers.newDirectShortBuffer(cube.getIndices());
	    
		initDebug(gl);

        initBuffers(gl);

        initVertexArray(gl);
		
		m_ShaderComponent = new ShaderComponent(gl, "src/resources/shaders", "default", "default");
		
		gl.glEnable(GL4.GL_DEPTH_TEST);
	}
	
	@Override
	public void display(GLAutoDrawable drawable) {
		GL4 gl = drawable.getGL().getGL4();
		
		// view matrix
        {
            float[] view = FloatUtil.makeIdentity(new float[16]);
            for (int i = 0; i < 16; i++)
                globalMatricesPointer.putFloat(16 * 4 + i * 4, view[i]);
        }
		
        gl.glClearBufferfv(GL4.GL_COLOR, 0, clearColor.put(0, 1f).put(1, .5f).put(2, 0f).put(3, 1f));
        gl.glClearBufferfv(GL4.GL_DEPTH, 0, clearDepth.put(0, 1f));
        
        m_PhysicsComponent.calculate();
		
	    vertexBuffer = GLBuffers.newDirectFloatBuffer(cube.getVertices());
	    elementBuffer = GLBuffers.newDirectShortBuffer(cube.getIndices());

        initBuffers(getGLobject());

        initVertexArray(getGLobject());
        
        // model matrix
        {
            long now = System.currentTimeMillis();
            float diff = (float) (now - start) / 1_000;

            float[] scale = FloatUtil.makeScale(new float[16], true, 0.5f, 0.5f, 0.5f);
            float[] rotateZ = FloatUtil.makeRotationAxis(new float[16], 0, diff, 0f, 0f, 1f, new float[3]);
            float[] model = FloatUtil.multMatrix(scale, rotateZ);
            modelMatrixPointer.asFloatBuffer().put(model);
        }
        
        gl.glValidateProgram(m_ShaderComponent.name);

        gl.glUseProgram(m_ShaderComponent.name);
        
        gl.glBindVertexArray(vertexArrayName.get(0));
        

        gl.glBindBufferBase(
                GL4.GL_UNIFORM_BUFFER,
                Semantic.Uniform.TRANSFORM0,
                bufferName.get(Buffer.GLOBAL_MATRICES));

        gl.glBindBufferBase(
                GL4.GL_UNIFORM_BUFFER,
                Semantic.Uniform.TRANSFORM1,
                bufferName.get(Buffer.MODEL_MATRIX));

        gl.glDrawElements(
        		GL4.GL_TRIANGLES,
                cube.getIndices().length,
                GL4.GL_UNSIGNED_SHORT,
                0);

        gl.glUseProgram(0);
        gl.glBindVertexArray(0);
        
        gl.glFlush();
		
	}
	
	@Override
    public void dispose(GLAutoDrawable drawable) {

        GL4 gl = drawable.getGL().getGL4();

        gl.glDeleteProgram(m_ShaderComponent.name);
        gl.glDeleteVertexArrays(1, vertexArrayName);
        gl.glDeleteBuffers(Buffer.MAX, bufferName);
    }
	
	@Override
    public void keyPressed(KeyEvent e) {  
    	if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
    		new Thread(() -> {
                getWindow().destroy();
            }).start();
    	}	
        	
    	if (e.getKeyCode() == KeyEvent.VK_SPACE) {
    		m_PhysicsComponent.jumping = m_PhysicsComponent.jumping <= -20 ? 20 : m_PhysicsComponent.jumping;
    	} if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
    		cube.translatePos(new Position(0, -0.05f, 0));
    	} if (e.getKeyCode() == KeyEvent.VK_A) {
    		m_PhysicsComponent.x_movement = -20;
    	} if (e.getKeyCode() == KeyEvent.VK_D) {
    		m_PhysicsComponent.x_movement = 20;
    	} if (e.getKeyCode() == KeyEvent.VK_W) {
    		cube.changeScale(new Scale(0.01f, 0.01f, 0.01f));
    	} if (e.getKeyCode() == KeyEvent.VK_S) {
    		cube.changeScale(new Scale(-0.01f, -0.01f, -0.01f));
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
        }
        
        rotationBuffer(gl);
    }
	
	
	private void rotationBuffer(GL4 gl) {
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
	}
	
	private void initVertexArray(GL4 gl) {

        gl.glCreateVertexArrays(1, vertexArrayName);

        gl.glVertexArrayAttribBinding(vertexArrayName.get(0), Semantic.Attr.POSITION, Semantic.Stream.A);
        gl.glVertexArrayAttribBinding(vertexArrayName.get(0), Semantic.Attr.COLOR, Semantic.Stream.A);

        gl.glVertexArrayAttribFormat(vertexArrayName.get(0), Semantic.Attr.POSITION, 3, GL4.GL_FLOAT, false, 0);
        gl.glVertexArrayAttribFormat(vertexArrayName.get(0), Semantic.Attr.COLOR, 3, GL4.GL_FLOAT, false, 3 * 4);

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
		this.vertexBuffer.put(vertices);
		this.vertexBuffer.rewind();
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
		this.elementBuffer.put(indices);
		this.elementBuffer.rewind();
	}

	/**
	 * @return the physicsComponent
	 */
	public PhysicsComponent getPhysicsComponent() {
		return m_PhysicsComponent;
	}

	/**
	 * @param physicsComponent the physicsComponent to set
	 */
	public void setPhysicsComponent(PhysicsComponent physicsComponent) {
		this.m_PhysicsComponent = physicsComponent;
	}

	
}
