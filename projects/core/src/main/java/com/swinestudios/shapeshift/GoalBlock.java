package com.swinestudios.shapeshift;

import org.mini2Dx.core.geom.Rectangle;
import org.mini2Dx.core.graphics.Graphics;

import com.badlogic.gdx.graphics.Color;

public class GoalBlock extends Rectangle{

	public boolean isActive;
	public String type;
	private Gameplay level;

	public GoalBlock(float x, float y, Gameplay level){
		super(x, y, 32, 32); //TODO replace with sprite dimensions later
		isActive = true;
		type = "GoalBlock";
		this.level = level;
	}

	//TODO replace with sprite later
	public void render(Graphics g){
		g.setColor(Color.GRAY);
		g.drawRect(x, y, width, height);
	}

	public void update(float delta){
		//Empty for now
	}

}
