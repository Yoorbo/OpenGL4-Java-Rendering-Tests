package components;

import static com.jogamp.opengl.GL.GL_DEPTH_TEST;
import static com.jogamp.opengl.GL.GL_DONT_CARE;
import static com.jogamp.opengl.GL.GL_UNSIGNED_SHORT;
import static com.jogamp.opengl.GL2ES2.GL_DEBUG_SEVERITY_HIGH;
import static com.jogamp.opengl.GL2ES2.GL_DEBUG_SEVERITY_MEDIUM;
import static com.jogamp.opengl.GL2ES3.GL_COLOR;
import static com.jogamp.opengl.GL2ES3.GL_DEPTH;
import static com.jogamp.opengl.GL2ES3.GL_UNIFORM_BUFFER;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.MouseEvent;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLDebugListener;
import com.jogamp.opengl.GLDebugMessage;
import com.jogamp.opengl.math.FloatUtil;
import com.jogamp.opengl.util.GLBuffers;

import components.WindowComponent.ScreenDimension;
import framework.Semantic;
import objects.Chunk;
import renderers.BaseRenderer;

public class SceneComponent extends BaseRenderer{

	public SceneComponent(String title, ScreenDimension dimensions) {
		super(title, dimensions);
	}
    
    private Chunk chunk;
   
    private ObjectBufferer[] objectBufferArray;
    private ArrayPackerComponent arrayPacker;
    
    private IntBuffer vertexArray = GLBuffers.newDirectIntBuffer(1);
    
    private boolean lines = false;
    
    


    @Override
    public void init(GLAutoDrawable drawable) {

        GL4 gl = drawable.getGL().getGL4();
        
        chunk = new Chunk("1");
        
        objectBufferArray = new ObjectBufferer[] {
        		new ObjectBufferer(chunk, gl, vertexArray, 0)
        };
        
        arrayPacker = new ArrayPackerComponent(gl, objectBufferArray, vertexArray);
		
		initDebug(gl);

        gl.glEnable(GL_DEPTH_TEST);
        gl.glPointSize(10f);
    }

    @Override
    public void display(GLAutoDrawable drawable) {

        GL4 gl = drawable.getGL().getGL4();
        
        for (int i = 0; i < objectBufferArray.length; i++) {
        	objectBufferArray[i].rendercycle(gl);
        }
        
        if (lines) {
        	gl.glDrawElements(
	                GL4.GL_LINE_STRIP,
	                chunk.getIndices().length,
	                GL_UNSIGNED_SHORT,
	                0);
        } else {
	        gl.glDrawElements(
	                GL4.GL_TRIANGLES,
	                chunk.getIndices().length,
	                GL_UNSIGNED_SHORT,
	                0);
        }
        
        gl.glDrawElements(
                GL4.GL_POINTS,
                chunk.getIndices().length,
                GL_UNSIGNED_SHORT,
                0);
        
        gl.glUseProgram(0);
        gl.glBindVertexArray(0);
    }
    
    /*
    @Override
    public void keyPressed(KeyEvent e) {  
    	if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
    		new Thread(() -> {
                getWindow().destroy();
            }).start();
    	}	
        	
    	if (e.getKeyCode() == KeyEvent.VK_SPACE) {
    		m_PhysicsComponent.jumping = m_PhysicsComponent.jumping <= -20 ? 20 : m_PhysicsComponent.jumping;
    	} if (e.getKeyCode() == KeyEvent.VK_A) {
    		m_PhysicsComponent.x_movement = -20;
    	} if (e.getKeyCode() == KeyEvent.VK_D) {
    		m_PhysicsComponent.x_movement = 20;
    	} if (e.getKeyCode() == KeyEvent.VK_W) {
    		m_PhysicsComponent.translateScale(10, 10, 10, 2);
    	} if (e.getKeyCode() == KeyEvent.VK_S) {
    		m_PhysicsComponent.translateScale(-10, -10, -10, 2);
    	} if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
    		m_PhysicsComponent.translateRot(90, 0, 0, 2);
    	} if (e.getKeyCode() == KeyEvent.VK_LEFT) {
    		m_PhysicsComponent.translateRot(-90, 0, 0, 2);
    	} if (e.getKeyCode() == KeyEvent.VK_UP) {
    		m_PhysicsComponent.translateRot(0, 90, 0, 2);
    	} if (e.getKeyCode() == KeyEvent.VK_DOWN) {
    		m_PhysicsComponent.translateRot(0, -90, 0, 2);
    	} if (e.getKeyCode() == KeyEvent.VK_L) {
    		lines = !lines;
    	} if (e.getKeyCode() == KeyEvent.VK_N) {
    		chunk.next_cube();
    	} if (e.getKeyCode() == KeyEvent.VK_R) {
    		chunk.restore();
    	} if (e.getKeyCode() == KeyEvent.VK_I) {
    		chunk.next_line();
    	}
	}

    @Override
    public void keyReleased(KeyEvent e) {
    }
    
    @Override
	public void mouseClicked(MouseEvent e) {
		System.out.println(e.getX() + " " + e.getY());
		
	}
	*/
	
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
}