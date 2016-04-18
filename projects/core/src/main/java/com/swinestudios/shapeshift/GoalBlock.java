package com.swinestudios.shapeshift;

import org.mini2Dx.core.geom.Rectangle;
import org.mini2Dx.core.graphics.Graphics;
import org.mini2Dx.core.graphics.Sprite;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class GoalBlock extends Rectangle{

	public boolean isActive;
	public String type;
	private Gameplay level;
	
	public Sprite goalSprite;

	public GoalBlock(float x, float y, Gameplay level){
		super(x, y, 32, 32); 
		isActive = true;
		type = "GoalBlock";
		goalSprite = new Sprite(new Texture(Gdx.files.internal("goal2.png")));
		this.level = level;
	}

	public void render(Graphics g){
		//g.setColor(Color.GRAY);
		//g.drawRect(x, y, width, height);
		g.drawSprite(goalSprite, x, y);
	}

	public void update(float delta){
		//Empty for now
	}

}
