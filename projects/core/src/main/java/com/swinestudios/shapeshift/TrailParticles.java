package com.swinestudios.shapeshift;

import java.util.ArrayList;

import org.mini2Dx.core.geom.Rectangle;
import org.mini2Dx.core.graphics.Graphics;

import com.badlogic.gdx.graphics.Color;

public class TrailParticles{

	public ArrayList<Particle> debris;
	public Color color;

	public boolean isActive;
	public float spawnTimer;
	public float maxSpawnTimer = 0.01f; //How often to spawn a trail particle

	public Gameplay level;
	public String type;

	public TrailParticles(Color color, Gameplay level){
		isActive = true;
		this.level = level;
		this.color = color;
		spawnTimer = 0;
		type = "TrailParticles";
		debris = new ArrayList<Particle>();
	}

	public void render(Graphics g){
		if(isActive){
			drawParticles(g);
		}
	}

	public void update(float delta){
		if(isActive){
			updateParticles(delta);
			
			spawnTimer += delta;
			if(spawnTimer >= maxSpawnTimer){
				spawnTimer = 0;
				//Leave a trail only when the player is moving
				if(level.player.velX != 0 || level.player.velY != 0){
					spawnTrail();
				}
			}
		}
	}

	public void drawParticles(Graphics g){
		for(int i = 0; i < debris.size(); i++){
			for(int j = 0; j < level.windows.size(); j++){
				if(level.windows.get(j).tempBox.contains(debris.get(i).hitbox)){
					debris.get(i).render(g);
				}
			}
		}
	}

	public void updateParticles(float delta){
		for(int i = 0; i < debris.size(); i++){
			debris.get(i).update(delta);
		}
	}
	
	public void resetParticles(float delta){
		for(int i = 0; i < debris.size(); i++){
			debris.get(i).isActive = false;
		}
		debris.clear();
	}
	
	public void spawnTrail(){
		debris.add(new Particle(level.player.x, level.player.y, color, this));
	}

}

class Particle{
	public float x, y;
	public TrailParticles parent;
	public Color color;
	public int size;
	public boolean isActive;
	public Rectangle hitbox;
	
	public float colorTimer;
	public float maxColorTimer = 0.7f; //How long before a trail particle fades away

	public Particle(float x, float y, Color color, TrailParticles parent){
		this.x = x;
		this.y = y;
		colorTimer = 0;
		size = 20; //Default size 20x20
		hitbox = new Rectangle(x, y, size, size);
		this.parent = parent;
		this.color = new Color(color);
		isActive = true;
	}

	public void render(Graphics g){
		if(isActive){
			g.setColor(color);
			g.fillRect(x, y, size, size);
		}
	}

	public void update(float delta){
		if(isActive){
			colorTimer += delta;
			
			color.a = (maxColorTimer - colorTimer) / maxColorTimer;
			
			if(colorTimer >= maxColorTimer){
				isActive = false;
				this.parent.debris.remove(this);
			}
		}
	}

	public void setSize(int size){
		this.size = size;
	}

}
