package com.swinestudios.shapeshift;

import org.mini2Dx.core.geom.Rectangle;
import org.mini2Dx.core.graphics.Graphics;

import com.badlogic.gdx.graphics.Color;

public class BoundingBlock extends Rectangle{

	public boolean isActive;
	public String type;
	private Gameplay level;

	public BoundingBlock(float x, float y, float width, float height, Gameplay level){
		super(x, y, width, height);
		isActive = true;
		type = "BoundingBlock";
		this.level = level;
	}

	public void render(Graphics g){
		g.setColor(Color.GRAY);
		g.drawRect(x, y, width, height);
		g.drawString("BOUND", x, y);
	}

	public void update(float delta){
		//Empty for now
	}

}
