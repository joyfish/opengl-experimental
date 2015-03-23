package main;

import static org.lwjgl.opengl.GL11.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;


public final class Model {
	
	private List<Vector3f> vertices, normals;
	private List<Vector2f> textureCoordinates;
	private List<Face> faces;
	
	public int createDisplayList() {
		int displayList = glGenLists(1);
        glNewList(displayList, GL_COMPILE);
		glBegin(GL_TRIANGLES);
		Vector3f v, n;
		Vector2f tc;
        for(Face face : faces) {
        	n = normals.get(face.normals[0]);
        	glNormal3f(n.x, n.y, n.z);
        	tc = textureCoordinates.get(face.textureCoordinates[0]);
        	glTexCoord2f(tc.x, tc.y);
        	v = vertices.get(face.vertices[0]);
        	glVertex3f(v.x, v.y, v.z);
        	n = normals.get(face.normals[1]);
        	glNormal3f(n.x, n.y, n.z);
        	tc = textureCoordinates.get(face.textureCoordinates[1]);
        	glTexCoord2f(tc.x, tc.y);
        	v = vertices.get(face.vertices[1]);
        	glVertex3f(v.x, v.y, v.z);
        	n = normals.get(face.normals[2]);
        	glNormal3f(n.x, n.y, n.z);
        	tc = textureCoordinates.get(face.textureCoordinates[2]);
        	glTexCoord2f(tc.x, tc.y);
        	v = vertices.get(face.vertices[2]);
        	glVertex3f(v.x, v.y, v.z);
        }
    	glEnd();
        glEndList();
        return displayList;
	}
	
	public int createWireframeDisplayList() {
		int displayList = glGenLists(1);
        glNewList(displayList, GL_COMPILE);
        for(Face face : faces) {
        	glBegin(GL_LINE_LOOP);
        	Vector3f v1 = vertices.get(face.vertices[0]);
        	glVertex3f(v1.x, v1.y, v1.z);
        	Vector3f v2 = vertices.get(face.vertices[1]);
        	glVertex3f(v2.x, v2.y, v2.z);
        	Vector3f v3 = vertices.get(face.vertices[2]);
        	glVertex3f(v3.x, v3.y, v3.z);
        	glEnd();
        }
        glEndList();
        return displayList;
	}
	
	private Model(List<Vector3f> vertices, List<Vector3f> normals, List<Vector2f> textureCoordinates, List<Face> faces) {
		this.vertices = vertices;
		this.normals = normals;
		this.textureCoordinates = textureCoordinates;
		this.faces = faces;
	}
	
	public static Model fromOBJ(String file) throws IOException {
		ArrayList<Vector3f> vertices = new ArrayList<Vector3f>(), normals = new ArrayList<Vector3f>();
		ArrayList<Vector2f> textureCoordinates = new ArrayList<Vector2f>();
		ArrayList<Face> faces = new ArrayList<Face>();
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line;
		while((line = reader.readLine()) != null) {
			String[] splittedString = line.split(" ");
			if(splittedString.length == 0) continue;
			if(splittedString[0].equals("v")) vertices.add(new Vector3f(Float.parseFloat(splittedString[1]),
					Float.parseFloat(splittedString[2]), Float.parseFloat(splittedString[3])));
			else if(splittedString[0].equals("vn")) normals.add(new Vector3f(Float.parseFloat(splittedString[1]),
					Float.parseFloat(splittedString[2]), Float.parseFloat(splittedString[3])));
			else if(splittedString[0].equals("vt")) textureCoordinates.add(new Vector2f(Float.parseFloat(splittedString[1]),
					1 - Float.parseFloat(splittedString[2])));
			else if(splittedString[0].equals("f")) {
				int l = splittedString.length - 1;
				String[] indicies;
				int[] vertexIndicies = new int[l], normalIndicies = new int[l], textureIndicies = new int[l];
				for(int i = 0; i < l; i++) {
					indicies = splittedString[i + 1].split("/");
					vertexIndicies[i] = Integer.parseInt(indicies[0]) - 1;
					textureIndicies[i] = Integer.parseInt(indicies[1]) - 1;
					normalIndicies[i] = Integer.parseInt(indicies[2]) - 1;
				}
				faces.add(new Face(vertexIndicies, normalIndicies, textureIndicies));
			}
		}
		reader.close();
		return new Model(vertices, normals, textureCoordinates, faces);
	}
	
	public static class Face {

		private int[] vertices, normals, textureCoordinates;
		
		public Face(int[] vertices, int[] normals, int[] textureCoordinates) {
			this.vertices = vertices;
			this.normals = normals;
			this.textureCoordinates = textureCoordinates;
		}
		
	}
	
	@Override
	public String toString() {
		return "Model: " + vertices.size() + " vertices, " + normals.size() + " normals, " + faces.size() + " faces";
	}
	
}
