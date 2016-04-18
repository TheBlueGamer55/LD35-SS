package com.swinestudios.shapeshift;
import org.mini2Dx.core.geom.Rectangle;
import org.mini2Dx.core.graphics.Animation;
import org.mini2Dx.core.graphics.Graphics;
import org.mini2Dx.core.graphics.Sprite;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

public class Portal {
	public static final int PORTAL_WIDTH = 32;
	public static final int PORTAL_HEIGHT = 32;

	public static float linkOpacity = 1;
	public static Color linkColor = new Color(Color.GREEN);
	public static final float PERIOD = 5;//5 second cycle of opacity
	public static float cyclePoint = 0;

	public boolean drawing = false;//Whether or not this member of the pair
	//is drawing link lines

	public float teleportTimer;
	public final float maxTeleportTimer = 1f; //How long before a portal can teleport again
	public boolean canTeleport;

	public Rectangle hitbox;

	public Color color;

	public float xPos, yPos;

	boolean active;//Whether or not this portal is active

	PortalSystem system;//Not used in current implementation
	Portal link;//The portal this portal links to

	public Sprite frame0, frame1, frame2, idle;
	public Animation<Sprite> portalAnimation;
	public float animationSpeed = 0.1f; //How many seconds a frame lasts

	public Gameplay level;
	
	public static Sound linkSound = Gdx.audio.newSound(Gdx.files.internal("portal01.wav"));
	public static Sound activateSound = Gdx.audio.newSound(Gdx.files.internal("portal06.wav"));
	public static Sound deactivateSound = Gdx.audio.newSound(Gdx.files.internal("deactivate.wav"));

	public Portal( float xPos, float yPos, Gameplay level){

		this.color = Color.GRAY;

		this.xPos = xPos;
		this.yPos = yPos;

		this.level = level;

		this.active = false;
		this.canTeleport = false;

		frame0 = new Sprite(new Texture(Gdx.files.internal("portal00.png")));
		frame1 = new Sprite(new Texture(Gdx.files.internal("portal01.png")));
		frame2 = new Sprite(new Texture(Gdx.files.internal("portal02.png")));
		idle = new Sprite(new Texture(Gdx.files.internal("portal_idle.png")));

		portalAnimation = new Animation<Sprite>();
		portalAnimation.addFrame(frame0, animationSpeed);
		portalAnimation.addFrame(frame1, animationSpeed);
		portalAnimation.addFrame(frame2, animationSpeed);
		portalAnimation.setLooping(true);

		this.hitbox = new Rectangle(xPos, yPos, frame0.getWidth(), frame0.getHeight());

		this.system = null;
		this.link = null;
	}

	public void render(Graphics g){
		g.setColor(color);
		//debug message
		/*g.drawString(active ? "Active" : "Inactive", xPos, yPos);
		g.drawString(teleportTimer + "", xPos, yPos + 8);
		*/
		
		if(portalAnimation != null){
			if(active){
				portalAnimation.draw(g, xPos, yPos);
			}
			else{
				g.drawSprite(idle, xPos, yPos);
			}
		}
		else{
			g.drawRect(xPos, yPos, PORTAL_WIDTH, PORTAL_HEIGHT);
		}

		if(link!=null){
			g.setColor(linkColor);

			/*g.drawLineSegment(this.hitbox.x+this.hitbox.width/2, 
					this.hitbox.y+this.hitbox.height/2,
					link.hitbox.x+link.hitbox.height/2,
					link.hitbox.y+link.hitbox.height/2);*/
			
			float x1 = this.hitbox.x + this.hitbox.width / 2;
			float y1 = this.hitbox.y + this.hitbox.height / 2;
			float x2 = link.hitbox.x + link.hitbox.width / 2;
			float y2 = link.hitbox.y + link.hitbox.height / 2;
			float length = (float) Math.sqrt((x2 - x1)*(x2 - x1) + (y2 - y1)*(y2 - y1));
			float angle = (float) Math.toDegrees(Math.atan2(y2 - y1, x2 - x1));
			g.rotate(angle, x1, y1);
			g.drawRect(x1, y1, length, 1);
			g.rotate(-angle, x1, y1);
		}
	}

	public void update(float delta){
		checkWindowCollision();
		portalAnimation.update(delta);

		cyclePoint += delta;
		cyclePoint %= PERIOD;
		linkColor.a = (float) Math.pow( Math.sin(cyclePoint*2.0*Math.PI/PERIOD)
				, 2.0 );

		if( link != null && link.active==false){
			if(link!=null){
				link.link=null;
				link=null;
			}
		}

		if(!canTeleport){
			teleportTimer += delta;
			if(teleportTimer > maxTeleportTimer){
				canTeleport = true;
				teleportTimer = 0;
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
		activateSound.play(0.3f);
		//Call any other listener etc methods.
	}

	public void deactivate(){
		active = false;
		deactivateSound.play();
	}


	public Portal getLink() {
		return link;
	}


	public void setLink(Portal link) {
		this.link = link;
		linkSound.play(0.25f);
	}

	public void checkWindowCollision(){
		boolean isVisibleInWindow = false;
		for(int i = 0; i < level.windows.size(); i++){
			RubberBand window = level.windows.get(i);
			if(window.tempBox.contains(this.hitbox)){
				isVisibleInWindow = true;
			}
		}
		if(!active && isVisibleInWindow){
			activate();
		}
		if(active && !isVisibleInWindow){
			deactivate();
		}
	}	

}
