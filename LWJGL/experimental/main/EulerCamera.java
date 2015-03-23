package main;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.gluPerspective;
import main.World.Entity;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class EulerCamera {
	
	private static final float MOUSE_SENSITIVITY = 0.1f, WHEEL_SENSITIVITY = 0.0001f;
	
	private float fov, aspectRatio, zNear, zFar, pitch, yaw, speed = 0.01f;
	private Entity entity;
	
	/**
	 * x, y, z, fov, aspectRation, zNear, zFar
	 */
	public EulerCamera(Entity entity, float fov, float aspectRatio, float zNear, float zFar) {
		this.fov = fov;
		this.entity = entity;
		this.aspectRatio = aspectRatio;
		this.zNear = zNear;
		this.zFar = zFar;
	}
	
	public void applyPerspectiveMatrix() {
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
        gluPerspective(fov, aspectRatio, zNear, zFar);
        glMatrixMode(GL_MODELVIEW);
	}
	
	public void applyOrthogonalMatrix() {
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(-fov, fov, -fov, fov, zNear, zFar);
        glMatrixMode(GL_MODELVIEW);
	}

	public void applyTransitions() {
        glRotatef(pitch, 1, 0, 0);
        glRotatef(yaw, 0, 1, 0);
        glTranslatef(-entity.position.x, -entity.position.y, -entity.position.z);
	}
	
	public void processMouse() {
		pitch -= Mouse.getDY() * MOUSE_SENSITIVITY;
		yaw += Mouse.getDX() * MOUSE_SENSITIVITY;
		speed += Mouse.getDWheel() * WHEEL_SENSITIVITY;
	}
	
	public void processKeyboard() {
		if(Keyboard.isKeyDown(Keyboard.KEY_W)) moveFromLook(0, 0, -speed);
		if(Keyboard.isKeyDown(Keyboard.KEY_S)) moveFromLook(0, 0, speed);
		if(Keyboard.isKeyDown(Keyboard.KEY_A)) moveFromLook(-speed, 0, 0);
		if(Keyboard.isKeyDown(Keyboard.KEY_D)) moveFromLook(speed, 0, 0);
		if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) entity.velocity.y -= speed;
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)) entity.velocity.y += speed;
	}
	
	private void moveFromLook(float dx, float dy, float dz) {
		entity.velocity.z += dx * (float) Math.cos(Math.toRadians(yaw - 90)) + dz * Math.cos(Math.toRadians(yaw));
		entity.velocity.x -= dx * (float) Math.sin(Math.toRadians(yaw - 90)) + dz * Math.sin(Math.toRadians(yaw));
		entity.velocity.y += dy * (float) Math.sin(Math.toRadians(pitch - 90)) + dz * Math.sin(Math.toRadians(pitch));
	}
	
	@Override
	public String toString() {
		return "[" + entity.position.x + ", " + entity.position.y + ", " + entity.position.z + "]";
	}
	
}
