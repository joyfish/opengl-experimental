package main;

import org.lwjgl.util.vector.Vector3f;

import main.World.Bounds;
import static org.lwjgl.opengl.GL11.*;

public class AABB implements Bounds {
	
	public Vector3f d;
	
	public AABB(Vector3f d) {
		this.d = d;
	}

	public int createDisplayList() {
		
		int list = glGenLists(1);
        glNewList(list, GL_COMPILE);
		
		glBegin(GL_QUADS);
		
		glNormal3f(0, 0, -1);
		glVertex3f(0, 0, 0);
		glVertex3f(d.x, 0, 0);
		glVertex3f(d.x, d.y, 0);
		glVertex3f(0, d.y, 0);
		
		glNormal3f(0, 0, 1);
		glVertex3f(0, 0, d.z);
		glVertex3f(d.x, 0, d.z);
		glVertex3f(d.x, d.y, d.z);
		glVertex3f(0, d.y, d.z);
		
		glNormal3f(-1, 0, 0);
		glVertex3f(0, 0, 0);
		glVertex3f(0, 0, d.z);
		glVertex3f(0, d.y, d.z);
		glVertex3f(0, d.y, 0);
		
		glNormal3f(1, 0, 0);
		glVertex3f(d.x, 0, 0);
		glVertex3f(d.x, 0, d.z);
		glVertex3f(d.x, d.y, d.z);
		glVertex3f(d.x, d.y, 0);
		
		glNormal3f(0, -1, 0);
		glVertex3f(0, 0, 0);
		glVertex3f(d.x, 0, 0);
		glVertex3f(d.x, 0, d.z);
		glVertex3f(0, 0, d.z);
		
		glNormal3f(0, 1, 0);
		glVertex3f(0, d.y, 0);
		glVertex3f(d.x, d.y, 0);
		glVertex3f(d.x, d.y, d.z);
		glVertex3f(0, d.y, d.z);
		
		glEnd();
		glEndList();
		
		return list;
		
	}
	

}
