package objects;

import datastructures.Position;

public class Chunk extends BaseObject{
	private BaseObject[] grid = new BaseObject[125];
	private int current_obj = 0;
	private int current_line = 0;
	
	private float[] bufferVertices;
	private short[] bufferIndices;
	
	
	public Chunk(String chunkid) {
		pos = new Position(0, 0, 0);
		m_indices = new short[0];
		m_vertices = new float[0];
		id = chunkid;
		
		for (int x = 0; x < 5; x++) {
			for (int y = 0; y < 5; y++) {
				for (int z = 0; z < 5; z++) {
					grid[current_obj] = new Cube(id + "_" + (current_obj), true, new Position(x + pos.x, y + pos.y, z + pos.z));
					m_vertices = grid[current_obj].verticesInsertBehind(m_vertices);
					m_indices = grid[current_obj].indicesInsertBehind(m_indices);
					current_obj++;
				}
			}
		}
		
		bufferIndices = m_indices;
		bufferVertices = m_vertices;
		
		current_obj = 0;
	}
	
	public void next_cube() {
		if (current_obj >= grid.length) {
			current_obj = 0;
		}
		if (current_obj == 0) {
			m_indices = new short[0];
			m_vertices = new float[0];
		}
		System.out.println(current_obj);
		m_vertices = grid[current_obj].verticesInsertBehind(m_vertices);
		m_indices = grid[current_obj].indicesInsertBehind(m_indices);
		current_obj++;
		System.out.println(m_vertices[0]);
	}
	
	public void next_line() {
		if (current_obj >= grid.length) {
			current_obj = 0;
		}
		if (current_line >= grid[current_obj].m_indices.length) {
			current_line = 0;
			current_obj++;
			
		}
		if (current_obj == 0) {
			m_indices = new short[0];
			m_vertices = new float[0];
		}
		if (current_line == 0) {
			m_vertices = grid[current_obj].verticesInsertBehind(m_vertices);
		}
		System.out.println(grid[current_obj].m_indices[current_line]);
		m_indices = grid[current_obj].singleIndiceInsertBehind(m_indices, current_line, 8*current_obj);
		
		current_line++;
	}
	
	public void restore(){
		current_obj = 0;
		m_indices = bufferIndices;
		m_vertices = bufferVertices;
	}
	
	public void debug_vert() {
		m_indices = new short[0];
		m_vertices = new float[0];
		for(int iter = 0; iter < grid.length; iter++) {
			grid[iter].debug = !grid[iter].debug;
			m_vertices = grid[iter].verticesInsertBehind(m_vertices);
			m_indices = grid[iter].indicesInsertBehind(m_indices);
		}
	}
}
