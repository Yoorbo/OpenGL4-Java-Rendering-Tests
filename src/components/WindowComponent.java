package components;

import java.nio.ByteBuffer;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseListener;
import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.Animator;

public class WindowComponent extends BaseComponent implements GLEventListener, KeyListener, MouseListener {
	
	private GLWindow m_window;
	private GLProfile m_glProfile;
    private GLCapabilities m_glCapabilities;
    private Animator m_animator;
    private GLAutoDrawable m_drawable;
    private int m_programId;
    
    protected ByteBuffer globalMatricesPointer, modelMatrixPointer;
    
    private static GL4 m_GLobject;
    
    
    
    private String m_title;
    private ScreenDimension m_dimensions;
    
    // Data Types
    
    public static class ScreenDimension{
    	public int height;
    	public int width;
    	
    	public ScreenDimension(int screenWidth, int screenHeight) {
    		height = screenHeight;
    		width = screenWidth;
    	}
    }
	
	// Constructor
    
    public WindowComponent(String title, ScreenDimension dimensions) {
    	this.setTitle(title);
    	this.setDimensions(dimensions);
    	init();
    }
    
    // Functions
    
    private void regenerate() {
    	getWindow().setTitle(getTitle());
        getWindow().setSize(getDimensions().width, getDimensions().height);
    }
	
	private void init() {
		this.setGlProfile(GLProfile.get(GLProfile.GL4));
		this.setGLCapabilities(new GLCapabilities(getGlProfile()));
		
        this.setWindow(GLWindow.create(this.getGLCapabilities()));
        
        this.regenerate();

        getWindow().setContextCreationFlags(GLContext.CTX_OPTION_DEBUG);
        getWindow().setVisible(true);

        getWindow().addGLEventListener(this);
        getWindow().addKeyListener(this);
        getWindow().addMouseListener(this);
        
        
        setAnimator(new Animator(getWindow()));
        getAnimator().start();


        getWindow().addWindowListener(new WindowAdapter() {
            @Override
            public void windowDestroyed(WindowEvent e) {
                getAnimator().stop();
                System.exit(1);
            }
        });
	}	
	
	
	

	@Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            new Thread(() -> {
                getWindow().destroy();
            }).start();
        }
    }

	@Override
	public void keyReleased(KeyEvent e) {};

	@Override
	public void display(GLAutoDrawable drawable) { };

	@Override
	public void dispose(GLAutoDrawable drawable) {};

	@Override
	public void init(GLAutoDrawable drawable) {
		setDrawable(drawable);
		setGLobject(drawable.getGL().getGL4());
		setProgramId(drawable.getGL().getGL4().glCreateProgram());
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		getGLobject().glViewport(((width-height)/2), 0, height, height);
	};
	
	
	// WindowComponent Getters & Setters
	
	/**
	 * @return the GLWindow
	 */
	public GLWindow getWindow() {
		return m_window;
	}
	
	/**
	 * @param window the GLWindow to set
	 */
	public void setWindow(GLWindow window) {
		this.m_window = window;
	}
	
	/**
	 * @return the GLProfile
	 */
	public GLProfile getGlProfile() {
		return m_glProfile;
	}
	
	/**
	 * @param glProfile the GLProfile to set
	 */
	public void setGlProfile(GLProfile glProfile) {
		this.m_glProfile = glProfile;
	}

	/**
	 * @return the GLCapabilities
	 */
	public GLCapabilities getGLCapabilities() {
		return m_glCapabilities;
	}

	/**
	 * @param glCapabilities the GLCapabilities to set
	 */
	public void setGLCapabilities(GLCapabilities glCapabilities) {
		this.m_glCapabilities = glCapabilities;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return m_title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.m_title = title;
	}

	/**
	 * @return the dimensions
	 */
	public ScreenDimension getDimensions() {
		return m_dimensions;
	}

	/**
	 * @param dimensions the dimensions to set
	 */
	public void setDimensions(ScreenDimension dimensions) {
		this.m_dimensions = dimensions;
	}

	/**
	 * @return the animator
	 */
	public Animator getAnimator() {
		return m_animator;
	}

	/**
	 * @param animator the animator to set
	 */
	public void setAnimator(Animator animator) {
		this.m_animator = animator;
	}

	/**
	 * @return the m_drawable
	 */
	public GLAutoDrawable getDrawable() {
		return m_drawable;
	}

	/**
	 * @param drawable the m_drawable to set
	 */
	public void setDrawable(GLAutoDrawable drawable) {
		this.m_drawable = drawable;
	}

	/**
	 * @return the gLobject
	 */
	public static GL4 getGLobject() {
		return m_GLobject;
	}

	/**
	 * @param gLobject the gLobject to set
	 */
	public static void setGLobject(GL4 gLobject) {
		m_GLobject = gLobject;
	}

	/**
	 * @return the programId
	 */
	public int getProgramId() {
		return m_programId;
	}

	/**
	 * @param programId the programId to set
	 */
	public void setProgramId(int programId) {
		this.m_programId = programId;
	}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mouseDragged(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mouseMoved(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseWheelMoved(MouseEvent e) {
	}
}
