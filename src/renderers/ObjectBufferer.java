package renderers;

import static com.jogamp.opengl.GL.GL_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_ELEMENT_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_MAP_INVALIDATE_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_MAP_WRITE_BIT;
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

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.math.FloatUtil;
import com.jogamp.opengl.util.GLBuffers;

import components.PhysicsComponent;
import components.ShaderComponent;
import framework.Semantic;
import objects.BaseObject;

public class ObjectBufferer{
    
    private BaseObject object_to_buffer;
    private PhysicsComponent m_PhysicsComponent;
	private ShaderComponent m_ShaderComponent;

    private IntBuffer bufferName = GLBuffers.newDirectIntBuffer(Buffer.MAX);

    private FloatBuffer clearColor = GLBuffers.newDirectFloatBuffer(4);
    private FloatBuffer clearDepth = GLBuffers.newDirectFloatBuffer(1);

    private ByteBuffer modelMatrixPointer;
    private IntBuffer vertexArray;
    
    private int activeArraySlot = 0;
    
    private interface Buffer {

        int VERTEX = 0;
        int ELEMENT = 1;
        int GLOBAL_MATRICES = 2;
        int MODEL_MATRIX = 3;
        int MAX = 4;
    }


    public ObjectBufferer(BaseObject to_buffer, GL4 gl, IntBuffer vertexArrayIN) {        
        m_ShaderComponent = new ShaderComponent(gl, "src/resources/shaders", "secondversion", "secondversion");
        initBuffers(gl);
        vertexArray = vertexArrayIN;
    }
    
    public ObjectBufferer(BaseObject to_buffer, GL4 gl, IntBuffer vertexArrayIN, int activeSlot) {        
        m_ShaderComponent = new ShaderComponent(gl, "src/resources/shaders", "secondversion", "secondversion");
        initBuffers(gl);
        vertexArray = vertexArrayIN;
        activeArraySlot = activeSlot;
    }

    private void initBuffers(GL4 gl) {

        FloatBuffer vertexBuffer = GLBuffers.newDirectFloatBuffer(object_to_buffer.getVertices());
        ShortBuffer elementBuffer = GLBuffers.newDirectShortBuffer(object_to_buffer.getIndices());

        gl.glCreateBuffers(Buffer.MAX, bufferName);

        {

            gl.glBindBuffer(GL_ARRAY_BUFFER, bufferName.get(Buffer.VERTEX));
            gl.glBufferStorage(GL_ARRAY_BUFFER, vertexBuffer.capacity() * Float.BYTES, vertexBuffer, 0);
            gl.glBindBuffer(GL_ARRAY_BUFFER, 0);

            gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufferName.get(Buffer.ELEMENT));
            gl.glBufferStorage(GL_ELEMENT_ARRAY_BUFFER, elementBuffer.capacity() * Short.BYTES, elementBuffer, 0);
            gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);


            IntBuffer uniformBufferOffset = GLBuffers.newDirectIntBuffer(1);
            gl.glGetIntegerv(GL_UNIFORM_BUFFER_OFFSET_ALIGNMENT, uniformBufferOffset);
            int modelBlockSize = Math.max(16 * 4, uniformBufferOffset.get(0));

            gl.glBindBuffer(GL_UNIFORM_BUFFER, bufferName.get(Buffer.MODEL_MATRIX));
            gl.glBufferStorage(GL_UNIFORM_BUFFER, modelBlockSize, null, GL_MAP_WRITE_BIT | GL_MAP_PERSISTENT_BIT | GL_MAP_COHERENT_BIT);
            gl.glBindBuffer(GL_UNIFORM_BUFFER, 0);
        }

        modelMatrixPointer = gl.glMapNamedBufferRange(
                bufferName.get(Buffer.MODEL_MATRIX),
                0,
                16 * 4,
                GL_MAP_WRITE_BIT | GL_MAP_PERSISTENT_BIT | GL_MAP_COHERENT_BIT | GL_MAP_INVALIDATE_BUFFER_BIT);
    }

    public void rendercycle(GL4 gl) {
    	
    	m_PhysicsComponent.calculate();
    	
    	gl.glClearBufferfv(GL_COLOR, 0, clearColor.put(0, 1f).put(1, .5f).put(2, 0f).put(3, 1f));
        gl.glClearBufferfv(GL_DEPTH, 0, clearDepth.put(0, 1f));
    	
    	{ // VIEW MATRIX / MODEL MATRX / ROTATION MATRIX / TRANSLATION MATRIX

            float[] scale = FloatUtil.makeScale(
            		new float[16], true, 
            		m_PhysicsComponent.current_scale.x,
            		m_PhysicsComponent.current_scale.y,
            		m_PhysicsComponent.current_scale.z
            );
            
            float[] translation = FloatUtil.makeTranslation(
            		new float[16], true,
            		m_PhysicsComponent.current_pos.x,
            		m_PhysicsComponent.current_pos.y,
            		m_PhysicsComponent.current_pos.z
            );
            
            float[] rotateZ = FloatUtil.makeRotationAxis(
            		new float[16], 0, 
            		m_PhysicsComponent.current_rot.x,
            		m_PhysicsComponent.current_rot.y,
            		m_PhysicsComponent.current_rot.z, 1f,
            		new float[3]
            );
            
            float[] model = FloatUtil.multMatrix(scale, rotateZ);
            model = FloatUtil.multMatrix(model, translation);
            modelMatrixPointer.asFloatBuffer().put(model);
        } // VIEW MATRIX / MODEL MATRX / ROTATION MATRIX / TRANSLATION MATRIX
    	
    	
        gl.glUseProgram(m_ShaderComponent.name);
        gl.glBindVertexArray(vertexArray.get(activeArraySlot));

        gl.glBindBufferBase(
                GL_UNIFORM_BUFFER,
                Semantic.Uniform.TRANSFORM0,
                bufferName.get(Buffer.GLOBAL_MATRICES));

        gl.glBindBufferBase(
                GL_UNIFORM_BUFFER,
                Semantic.Uniform.TRANSFORM1,
                bufferName.get(Buffer.MODEL_MATRIX));
    	
        gl.glUseProgram(0);
        gl.glBindVertexArray(0);
    }
}