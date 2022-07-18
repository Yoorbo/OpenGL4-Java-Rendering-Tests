package renderers;

import static com.jogamp.opengl.GL.GL_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_DEPTH_TEST;
import static com.jogamp.opengl.GL.GL_DONT_CARE;
import static com.jogamp.opengl.GL.GL_ELEMENT_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_FLOAT;
import static com.jogamp.opengl.GL.GL_MAP_INVALIDATE_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_MAP_WRITE_BIT;
import static com.jogamp.opengl.GL.GL_STATIC_DRAW;
import static com.jogamp.opengl.GL.GL_TRIANGLES;
import static com.jogamp.opengl.GL.GL_UNSIGNED_SHORT;
import static com.jogamp.opengl.GL2ES2.GL_DEBUG_SEVERITY_HIGH;
import static com.jogamp.opengl.GL2ES2.GL_DEBUG_SEVERITY_LOW;
import static com.jogamp.opengl.GL2ES2.GL_DEBUG_SEVERITY_MEDIUM;
import static com.jogamp.opengl.GL2ES2.GL_DEBUG_SEVERITY_NOTIFICATION;
import static com.jogamp.opengl.GL2ES2.GL_FRAGMENT_SHADER;
import static com.jogamp.opengl.GL2ES2.GL_VERTEX_SHADER;
import static com.jogamp.opengl.GL2ES3.GL_COLOR;
import static com.jogamp.opengl.GL2ES3.GL_DEPTH;
import static com.jogamp.opengl.GL2ES3.GL_UNIFORM_BUFFER;
import static com.jogamp.opengl.GL2ES3.GL_UNIFORM_BUFFER_OFFSET_ALIGNMENT;
import static com.jogamp.opengl.GL4.GL_MAP_COHERENT_BIT;
import static com.jogamp.opengl.GL4.GL_MAP_PERSISTENT_BIT;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLDebugListener;
import com.jogamp.opengl.GLDebugMessage;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.math.FloatUtil;
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.GLBuffers;
import com.jogamp.opengl.util.glsl.ShaderCode;
import com.jogamp.opengl.util.glsl.ShaderProgram;

import components.PhysicsComponent;
import components.ShaderComponent;
import datastructures.Position;
import datastructures.Scale;
import framework.Semantic;
import objects.Cube;

public class ArrayRenderer extends BaseRenderer {

	public ArrayRenderer(String title, ScreenDimension dimensions) {
		super(title, dimensions);
		// TODO Auto-generated constructor stub
	}
    
    private Cube cube;
    private PhysicsComponent m_PhysicsComponent;
	private ShaderComponent m_ShaderComponent;

    private interface Buffer {

        int VERTEX = 0;
        int ELEMENT = 1;
        int GLOBAL_MATRICES = 2;
        int MODEL_MATRIX = 3;
        int MAX = 4;
    }

    private IntBuffer bufferName = GLBuffers.newDirectIntBuffer(Buffer.MAX);
    private IntBuffer vertexArrayName = GLBuffers.newDirectIntBuffer(1);

    private FloatBuffer clearColor = GLBuffers.newDirectFloatBuffer(4);
    private FloatBuffer clearDepth = GLBuffers.newDirectFloatBuffer(1);

    private FloatBuffer matBuffer = GLBuffers.newDirectFloatBuffer(16);

    private ByteBuffer globalMatricesPointer, modelMatrixPointer;
    // https://jogamp.org/bugzilla/show_bug.cgi?id=1287
    private boolean bug1287 = true;

    private long start;


    @Override
    public void init(GLAutoDrawable drawable) {

        GL4 gl = drawable.getGL().getGL4();
        
        cube = new Cube();
        cube.getScale().setAll(0.2f);
		m_PhysicsComponent = new PhysicsComponent();

        initDebug(gl);

        initBuffers(gl);

        initVertexArray(gl);
        
        m_ShaderComponent = new ShaderComponent(gl, "src/resources/shaders", "secondversion", "secondversion");

        gl.glEnable(GL_DEPTH_TEST);

        start = System.currentTimeMillis();
    }

    private void initDebug(GL4 gl) {

        getWindow().getContext().addGLDebugListener(new GLDebugListener() {
            @Override
            public void messageSent(GLDebugMessage event) {
                System.out.println(event);
            }
        });

        gl.glDebugMessageControl(
                GL_DONT_CARE,
                GL_DONT_CARE,
                GL_DONT_CARE,
                0,
                null,
                false);

        gl.glDebugMessageControl(
                GL_DONT_CARE,
                GL_DONT_CARE,
                GL_DEBUG_SEVERITY_HIGH,
                0,
                null,
                true);

        gl.glDebugMessageControl(
                GL_DONT_CARE,
                GL_DONT_CARE,
                GL_DEBUG_SEVERITY_MEDIUM,
                0,
                null,
                true);
    }

    private void initBuffers(GL4 gl) {

        FloatBuffer vertexBuffer = GLBuffers.newDirectFloatBuffer(cube.getVertices());
        ShortBuffer elementBuffer = GLBuffers.newDirectShortBuffer(cube.getIndices());

        gl.glCreateBuffers(Buffer.MAX, bufferName);

        if (!bug1287) {

            gl.glNamedBufferStorage(bufferName.get(Buffer.VERTEX), vertexBuffer.capacity() * Float.BYTES, vertexBuffer,
                    GL_STATIC_DRAW);
            gl.glNamedBufferStorage(bufferName.get(Buffer.ELEMENT), elementBuffer.capacity() * Short.BYTES,
                    elementBuffer, GL_STATIC_DRAW);

            gl.glNamedBufferStorage(bufferName.get(Buffer.GLOBAL_MATRICES), 16 * 4 * 2, null, GL_MAP_WRITE_BIT);
            gl.glNamedBufferStorage(bufferName.get(Buffer.MODEL_MATRIX), 16 * 4, null, GL_MAP_WRITE_BIT);

        } else {

            gl.glBindBuffer(GL_ARRAY_BUFFER, bufferName.get(Buffer.VERTEX));
            gl.glBufferStorage(GL_ARRAY_BUFFER, vertexBuffer.capacity() * Float.BYTES, vertexBuffer, 0);
            gl.glBindBuffer(GL_ARRAY_BUFFER, 0);

            gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufferName.get(Buffer.ELEMENT));
            gl.glBufferStorage(GL_ELEMENT_ARRAY_BUFFER, elementBuffer.capacity() * Short.BYTES, elementBuffer, 0);
            gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);


            IntBuffer uniformBufferOffset = GLBuffers.newDirectIntBuffer(1);
            gl.glGetIntegerv(GL_UNIFORM_BUFFER_OFFSET_ALIGNMENT, uniformBufferOffset);
            int globalBlockSize = Math.max(16 * 4 * 2, uniformBufferOffset.get(0));
            int modelBlockSize = Math.max(16 * 4, uniformBufferOffset.get(0));

            gl.glBindBuffer(GL_UNIFORM_BUFFER, bufferName.get(Buffer.GLOBAL_MATRICES));
            gl.glBufferStorage(GL_UNIFORM_BUFFER, globalBlockSize, null, GL_MAP_WRITE_BIT | GL_MAP_PERSISTENT_BIT | GL_MAP_COHERENT_BIT);
            gl.glBindBuffer(GL_UNIFORM_BUFFER, 0);

            gl.glBindBuffer(GL_UNIFORM_BUFFER, bufferName.get(Buffer.MODEL_MATRIX));
            gl.glBufferStorage(GL_UNIFORM_BUFFER, modelBlockSize, null, GL_MAP_WRITE_BIT | GL_MAP_PERSISTENT_BIT | GL_MAP_COHERENT_BIT);
            gl.glBindBuffer(GL_UNIFORM_BUFFER, 0);
        }

        // map the transform buffers and keep them mapped
        globalMatricesPointer = gl.glMapNamedBufferRange(
                bufferName.get(Buffer.GLOBAL_MATRICES),
                0,
                16 * 4 * 2,
                GL_MAP_WRITE_BIT | GL_MAP_PERSISTENT_BIT | GL_MAP_COHERENT_BIT | GL_MAP_INVALIDATE_BUFFER_BIT); // flags

        modelMatrixPointer = gl.glMapNamedBufferRange(
                bufferName.get(Buffer.MODEL_MATRIX),
                0,
                16 * 4,
                GL_MAP_WRITE_BIT | GL_MAP_PERSISTENT_BIT | GL_MAP_COHERENT_BIT | GL_MAP_INVALIDATE_BUFFER_BIT);
    }

    private void initVertexArray(GL4 gl) {

        gl.glCreateVertexArrays(1, vertexArrayName);

        gl.glVertexArrayAttribBinding(vertexArrayName.get(0), Semantic.Attr.POSITION, Semantic.Stream.A);
        gl.glVertexArrayAttribBinding(vertexArrayName.get(0), Semantic.Attr.COLOR, Semantic.Stream.A);

        gl.glVertexArrayAttribFormat(vertexArrayName.get(0), Semantic.Attr.POSITION, 3, GL_FLOAT, false, 0);
        gl.glVertexArrayAttribFormat(vertexArrayName.get(0), Semantic.Attr.COLOR, 3, GL_FLOAT, false, 2 * 4);

        gl.glEnableVertexArrayAttrib(vertexArrayName.get(0), Semantic.Attr.POSITION);
        gl.glEnableVertexArrayAttrib(vertexArrayName.get(0), Semantic.Attr.COLOR);

        gl.glVertexArrayElementBuffer(vertexArrayName.get(0), bufferName.get(Buffer.ELEMENT));
        gl.glVertexArrayVertexBuffer(vertexArrayName.get(0), Semantic.Stream.A, bufferName.get(Buffer.VERTEX), 0, (3 + 3) * 4);
    }

    @Override
    public void display(GLAutoDrawable drawable) {

        GL4 gl = drawable.getGL().getGL4();
        
        m_PhysicsComponent.calculate();

        // view matrix
        {
            float[] view = FloatUtil.makeIdentity(new float[16]);
            for (int i = 0; i < 16; i++)
                globalMatricesPointer.putFloat(16 * 4 + i * 4, view[i]);
        }


        gl.glClearBufferfv(GL_COLOR, 0, clearColor.put(0, 1f).put(1, .5f).put(2, 0f).put(3, 1f));
        gl.glClearBufferfv(GL_DEPTH, 0, clearDepth.put(0, 1f));

        // model matrix
        {
            long now = System.currentTimeMillis();
            float diff = (float) (now - start) / 1_000;

            float[] scale = FloatUtil.makeScale(new float[16], true, 
            		m_PhysicsComponent.current_scale.x,
            		m_PhysicsComponent.current_scale.y,
            		m_PhysicsComponent.current_scale.z);
            /*
            System.out.println(m_PhysicsComponent.current_scale.x + " " +
            		m_PhysicsComponent.current_scale.y + " " +
            		m_PhysicsComponent.current_scale.z);
            		*/
            
            float[] translation = FloatUtil.makeTranslation(new float[16], true,
            		m_PhysicsComponent.current_pos.x,
            		m_PhysicsComponent.current_pos.y,
            		m_PhysicsComponent.current_pos.z
            		);
            float[] rotateZ = FloatUtil.makeRotationAxis(new float[16], 0, 
            		m_PhysicsComponent.current_rot.x,
            		m_PhysicsComponent.current_rot.y,
            		m_PhysicsComponent.current_rot.z, 1f, new float[3]);
            float[] model = FloatUtil.multMatrix(scale, rotateZ);
            model = FloatUtil.multMatrix(model, translation);
            modelMatrixPointer.asFloatBuffer().put(model);
        }

        gl.glUseProgram(m_ShaderComponent.name);
        gl.glBindVertexArray(vertexArrayName.get(0));

        gl.glBindBufferBase(
                GL_UNIFORM_BUFFER,
                Semantic.Uniform.TRANSFORM0,
                bufferName.get(Buffer.GLOBAL_MATRICES));

        gl.glBindBufferBase(
                GL_UNIFORM_BUFFER,
                Semantic.Uniform.TRANSFORM1,
                bufferName.get(Buffer.MODEL_MATRIX));

        gl.glDrawElements(
                GL4.GL_LINES,
                cube.getIndices().length,
                GL_UNSIGNED_SHORT,
                0);

        gl.glUseProgram(0);
        gl.glBindVertexArray(0);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

        GL4 gl = drawable.getGL().getGL4();

        // ortho matrix
        float[] ortho = FloatUtil.makeOrtho(new float[16], 0, false, -1f, 1f, -1f, 1f, 1f, -1f);
        globalMatricesPointer.asFloatBuffer().put(ortho);

        gl.glViewport(x, y, width, height);
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
    		m_PhysicsComponent.translateScale(0.2f, 0.2f, 0.2f, 2);
    	} if (e.getKeyCode() == KeyEvent.VK_S) {
    		m_PhysicsComponent.translateScale(-0.2f, -0.2f, -0.2f, 2);
    	} if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
    		m_PhysicsComponent.translateRot(90, 0, 0, 2);
    	} if (e.getKeyCode() == KeyEvent.VK_LEFT) {
    		m_PhysicsComponent.translateRot(-90, 0, 0, 2);
    	} if (e.getKeyCode() == KeyEvent.VK_UP) {
    		m_PhysicsComponent.translateRot(0, 90, 0, 2);
    	} if (e.getKeyCode() == KeyEvent.VK_DOWN) {
    		m_PhysicsComponent.translateRot(0, -90, 0, 2);
    	}
	}

    @Override
    public void keyReleased(KeyEvent e) {
    }
}