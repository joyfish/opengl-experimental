package main;

import static org.lwjgl.opengl.EXTFramebufferObject.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.*;

public class Framebuffer {
	
	private int id, colorBufferId, depthTextureId;
	
	public void bind() {
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, id);
	}
	
	public void bindColorTexture() {
		glBindTexture(GL_TEXTURE_2D, colorBufferId);
	}
	
	public void bindDepthTexture() {
		glBindTexture(GL_TEXTURE_2D, depthTextureId);
	}
	
	public Framebuffer(int width, int height) {
		
        colorBufferId = glGenTextures();
        depthTextureId = glGenTextures();

        glBindTexture(GL_TEXTURE_2D, colorBufferId);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, (java.nio.ByteBuffer) null);

        glBindTexture(GL_TEXTURE_2D, depthTextureId);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_DEPTH_TEXTURE_MODE, GL_INTENSITY);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_MODE, GL_COMPARE_R_TO_TEXTURE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_FUNC, GL_LEQUAL);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT24, width, height, 0, GL_DEPTH_COMPONENT, GL_UNSIGNED_BYTE, (java.nio.ByteBuffer) null);
        
        glBindTexture(GL_TEXTURE_2D, 0);
        
        id = glGenFramebuffersEXT();
        bind();
        glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT0_EXT, GL_TEXTURE_2D, colorBufferId, 0);
        glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_DEPTH_ATTACHMENT_EXT, GL_TEXTURE_2D, depthTextureId, 0);    
        unbind();
        
	}
	
	public void delete() {
		glDeleteFramebuffersEXT(id);
	}
	
	public static void unbind() {
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
	}
	
}
