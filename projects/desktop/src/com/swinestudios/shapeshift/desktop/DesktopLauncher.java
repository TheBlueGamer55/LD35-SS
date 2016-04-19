package com.swinestudios.shapeshift.desktop;

import org.mini2Dx.desktop.DesktopMini2DxConfig;

import com.badlogic.gdx.backends.lwjgl.DesktopMini2DxGame;

import com.swinestudios.shapeshift.ShapeShift;

public class DesktopLauncher {
	public static void main(String[] arg){
		DesktopMini2DxConfig config = new DesktopMini2DxConfig(ShapeShift.GAME_IDENTIFIER);
		config.vSyncEnabled = true;
		config.width = 640;
		config.height = 480;
		config.resizable = false; 
		config.foregroundFPS = 30;
		config.backgroundFPS = 30;
		config.title = "0-/-0";
		new DesktopMini2DxGame(new ShapeShift(), config);
	}
}
