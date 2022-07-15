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
import com.jogamp.opengl.math.FloatUtil;
import com.jogamp.opengl.util.GLBuffers;

import components.ShaderComponent;
import framework.Semantic;
import objects.Cube;

public class MeshRenderer extends BaseRenderer  {
	
	private ShaderComponent m_ShaderComponent;
	private Cube cube = new Cube();
	
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
    
    private FloatBuffer vertexBuffer = GLBuffers.newDirectFloatBuffer(cube.getVertices());
    private ShortBuffer elementBuffer = GLBuffers.newDirectShortBuffer(cube.getIndices());
	
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
        getGLobject().glClearBufferfv(GL4.GL_COLOR, 0, clearColor.put(0, 1f).put(1, .5f).put(2, 0f).put(3, 1f));
        getGLobject().glClearBufferfv(GL4.GL_DEPTH, 0, clearDepth.put(0, 1f));
        
        getGLobject().glValidateProgram(m_ShaderComponent.name);

        getGLobject().glUseProgram(m_ShaderComponent.name);
        
        getGLobject().glBindVertexArray(vertexArrayName.get(0));

        getGLobject().glDrawElements(
        		GL4.GL_TRIANGLES,
                cube.getIndices().length,
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
	
	/*
	@Override
    public void keyPressed(KeyEvent e) {  
		GL4 gl = getGLobject();
		double convA = Math.toRadians( -rotAngle  + 90); 
        switch (e.getKeyCode()) {
        case KeyEvent.VK_ESCAPE:
        	new Thread(() -> {
                getWindow().destroy();
            }).start();
        	break;
        case KeyEvent.VK_PAGE_UP:
        	
        	setVertexBuffer(new float[] { 
        			-m_cube_size, -m_cube_size, m_cube_size, (float) Math.random(), (float) Math.random(), (float) Math.random(),
        			-m_cube_size, m_cube_size, m_cube_size, (float) Math.random(), (float) Math.random(), (float) Math.random(),
        			m_cube_size, -m_cube_size, m_cube_size, (float) Math.random(), (float) Math.random(), (float) Math.random(),
        			m_cube_size, m_cube_size, m_cube_size, (float) Math.random(), (float) Math.random(), (float) Math.random(),
        			
        			-m_cube_size, -m_cube_size, -m_cube_size, (float) Math.random(), (float) Math.random(), (float) Math.random(),
        			-m_cube_size, m_cube_size, -m_cube_size, (float) Math.random(), (float) Math.random(), (float) Math.random(),
        			m_cube_size, -m_cube_size, -m_cube_size, (float) Math.random(), (float) Math.random(), (float) Math.random(),
        			m_cube_size, m_cube_size, -m_cube_size, (float) Math.random(), (float) Math.random(), (float) Math.random(),
                });
        	
        	reloadBuffers(gl);
        	break;
        case KeyEvent.VK_D:
            transZ += Math.sin( convA - Math.PI / 2 );
            transX -= Math.cos( convA - Math.PI / 2 );
            break;
	    case KeyEvent.VK_A:
	            transZ -= Math.sin( convA - Math.PI / 2 );
	            transX += Math.cos( convA - Math.PI / 2 );
	            break;
	    case KeyEvent.VK_W:
	            transZ += Math.sin( convA );
	            transX -= Math.cos( convA );
	            break;
	    case KeyEvent.VK_S:
	            transZ -= Math.sin( convA );
	            transX += Math.cos( convA );
	            break;
	    case KeyEvent.VK_Z:
	             rotAngle -= (4);
	             if (rotAngle < 0){
	                     rotAngle += 360;
	             }
	
	            break;
	    case KeyEvent.VK_X:
	            rotAngle += (4);
	
	            break;
	    default:
	            break;
        }
   
        rotAngle %= 360; //mod 360

    }
    */
	
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

	
}
