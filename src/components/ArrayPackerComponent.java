package components;

import static com.jogamp.opengl.GL.GL_FLOAT;

import java.nio.IntBuffer;

import com.jogamp.opengl.GL4;

import framework.Semantic;

public class ArrayPackerComponent extends BaseComponent{

    private IntBuffer vertexArray;
    private IntBuffer[] bufferArray;
    private ObjectBufferer[] objectBufferArray;


    public ArrayPackerComponent(GL4 gl, IntBuffer[] buffers, IntBuffer vertexArrayIN) {
    	vertexArray = vertexArrayIN;
    	gl.glCreateVertexArrays(buffers.length, vertexArray);
        bufferArray = buffers;
        reloadVertexArray(gl);
    }
    
    public ArrayPackerComponent(GL4 gl, ObjectBufferer[] buffers, IntBuffer vertexArrayIN) {
    	vertexArray = vertexArrayIN;
    	gl.glCreateVertexArrays(buffers.length, vertexArray);
    	objectBufferArray = buffers;
    	packBuffers();
        reloadVertexArray(gl);
    }
    
    private void packBuffers(){
    	bufferArray = new IntBuffer[objectBufferArray.length];
    	for (int i = 0; i < objectBufferArray.length; i++) {
        	bufferArray[i] = objectBufferArray[i].OBJECT_BUFFER;
        }
    }

    private void reloadVertexArray(GL4 gl) {

        for (int iter = 0; iter < bufferArray.length; iter++) {
        	gl.glVertexArrayAttribBinding(vertexArray.get(iter), Semantic.Attr.POSITION, Semantic.Stream.A);
            gl.glVertexArrayAttribBinding(vertexArray.get(iter), Semantic.Attr.COLOR, Semantic.Stream.A);

            gl.glVertexArrayAttribFormat(vertexArray.get(iter), Semantic.Attr.POSITION, 3, GL_FLOAT, false, 0);
            gl.glVertexArrayAttribFormat(vertexArray.get(iter), Semantic.Attr.COLOR, 3, GL_FLOAT, false, 2 * 4);

            gl.glEnableVertexArrayAttrib(vertexArray.get(iter), Semantic.Attr.POSITION);
            gl.glEnableVertexArrayAttrib(vertexArray.get(iter), Semantic.Attr.COLOR);

            gl.glVertexArrayElementBuffer(vertexArray.get(iter), bufferArray[iter].get(1));
            gl.glVertexArrayVertexBuffer(vertexArray.get(iter), Semantic.Stream.A, bufferArray[iter].get(0), 0, (3 + 3) * 4);
        }

        
    }

}