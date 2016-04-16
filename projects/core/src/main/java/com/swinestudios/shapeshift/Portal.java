package com.swinestudios.shapeshift;
import org.mini2Dx.core.geom.Rectangle;
import org.mini2Dx.core.graphics.Graphics;
import com.badlogic.gdx.graphics.Color;

public class Portal {
	public static final int PORTAL_WIDTH = 32;
	public static final int PORTAL_HEIGHT = 32;
	
	public static float linkOpacity = 1;
	
	public Rectangle hitbox;
	
	public Color color;
	
	public float xPos, yPos;
	
	boolean active;//Whether or not this portal is active
	
	PortalSystem system;//Not used in current implementation
	Portal link;//The portal this portal links to
	
	public Gameplay level;
	
	public Portal( float xPos, float yPos, Gameplay level){
		
		this.color = Color.GRAY;
		
		this.xPos = xPos;
		this.yPos = yPos;
		
		this.level = level;
		
		this.active = false;
		
		this.hitbox = new Rectangle(xPos, yPos, PORTAL_WIDTH, PORTAL_HEIGHT);
		
		this.system = null;
		this.link = null;
	}

	public void render(Graphics g){
		g.setColor(color);
		g.drawRect(xPos, yPos, PORTAL_WIDTH, PORTAL_HEIGHT);
		
	}
	
	public void update(float delta){
		if(active==false){
			if(link!=null){
				link.link=null;
				link=null;
			}
		}
	}
	
	public PortalSystem getSystem() {//Maybe not necessary?
		return system;
	}

	
	public void setSystem(PortalSystem system) {
		this.system = system;
	}

	public boolean isActive() {
		return active;
	}


	public void setActive(boolean active) {//This is a simple setter
		this.active = active;
	}
	
	public void activate(){
		this.active=true;
		//Call any other listener etc methods.
	}


	public Portal getLink() {
		return link;
	}


	public void setLink(Portal link) {
		this.link = link;
	}
	
	
	
	
}
