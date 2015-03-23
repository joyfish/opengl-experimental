package main;

import org.lwjgl.Sys;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.*;

public class GameObject extends Thread {
	
	private int width = 1100, height = 700;
	private float time, lastTime = getTime();
	private Framebuffer fbo;
	private GLSLProgram[] shaders;
	private Texture[] textures;
	private World world;
	private EulerCamera camera;
	
	private int getTime() {
        return (int) ((Sys.getTime() * 1000) / Sys.getTimerResolution());
    }
	
    public void run() {
    	
    	try {
    		
    		Display.setDisplayModeAndFullscreen(new DisplayMode(width, height));
    		Display.setVSyncEnabled(true);
    		Display.create();
    		Mouse.setGrabbed(true);

    		glEnable(GL_TEXTURE_2D);

    		fbo = new Framebuffer(width, height);
			textures = new Texture[2];
			textures[0] = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/texture/texture.png"));
			textures[1] = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/texture/normalmap.png"));
			shaders = new GLSLProgram[2];
			shaders[0] = new GLSLProgram(GLSLProgram.loadShader(GL_VERTEX_SHADER, "res/shader/fbo.vs"), GLSLProgram.loadShader(GL_FRAGMENT_SHADER, "res/shader/fbo.fs"));
			shaders[0].use();
			shaders[0].setUniform("depth", 1);
    		shaders[1] = new GLSLProgram(GLSLProgram.loadShader(GL_VERTEX_SHADER, "res/shader/lighting.vs"), GLSLProgram.loadShader(GL_FRAGMENT_SHADER, "res/shader/lighting.fs"));
    		shaders[1].use();
    		shaders[1].setUniform("normalMap", 1);
    		
    		world = new World(new Vector3f(0, 0, 0), 0.0000001f);
    		world.new Entity(null, new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), 0, Model.fromOBJ("res/model/untitled.obj").createDisplayList());
    		camera = new EulerCamera(world.new Entity(null, new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), 1, 0), 80, width / height, 0.001f, 4);
			
    		while(!Display.isCloseRequested()) {
    			
    			float currentTime = getTime(), delta = currentTime - lastTime;
    			time += delta;
    			lastTime = currentTime;
    			world.update(delta);
    			
    			while(Mouse.next()) {
    				if(Mouse.getEventButtonState()) Mouse.setGrabbed(!Mouse.isGrabbed());
    			}
    			
    			camera.processKeyboard();
    			if(Mouse.isGrabbed()) camera.processMouse();
    			
    			camera.applyPerspectiveMatrix();
    			glLoadIdentity();
    			camera.applyTransitions();
    			glEnable(GL_DEPTH_TEST);
    			
    			shaders[1].use();
    			fbo.bind();
 
    			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    			glBindTexture(GL_TEXTURE_2D, textures[0].getTextureID());
    			glActiveTexture(GL_TEXTURE1);
    			glBindTexture(GL_TEXTURE_2D, textures[1].getTextureID());
    			glActiveTexture(GL_TEXTURE0);
    			
    			world.draw();
    			
    			Framebuffer.unbind();
    			
    			glMatrixMode(GL_PROJECTION);
    			glLoadIdentity();
    			glOrtho(0, width, 0, height, -1, 1);
    			glMatrixMode(GL_MODELVIEW);
    			glDisable(GL_DEPTH_TEST);
    			glLoadIdentity();
    			
    			shaders[0].use();
    			shaders[0].setUniform("time", time);
    			
    			fbo.bindColorTexture();
    			glActiveTexture(GL_TEXTURE1);
    			fbo.bindDepthTexture();
    			glActiveTexture(GL_TEXTURE0);
    			
    			glBegin(GL_QUADS);
    			glTexCoord2f(0, 0);
    			glVertex2f(0, 0);
    			glTexCoord2f(1, 0);
    			glVertex2f(width, 0);
    			glTexCoord2f(1, 1);
    			glVertex2f(width, height);
    			glTexCoord2f(0, 1);
    			glVertex2f(0, height);
    			glEnd();
    			
    			Display.update();
    			Display.sync(80);
    			
    		}
    		
    	} catch (Exception e) {
			e.printStackTrace();
			for(GLSLProgram s : shaders) s.delete();
			for(Texture t : textures) t.release();
		} finally {
    		Display.destroy();
    		System.exit(0);
    	}
    	
    }
 
    public static void main(String[] args) {
        new GameObject().run();
    }
    
}
