package com.cemonem.coopbot;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class Block extends Sprite {
	
	public static final int EMPTY = 0;
	public static final int TARGET = 1;
	public static final int AGENT = 2;
	public static final int OBSTACLE = 3;
	public static final int LEADER = 4;
	BitmapFont font;
	
	
	public int state = EMPTY;
	public String label = "";
	public boolean showLabel = false;
	
	
	public Block(Texture texture,Skin skin) {
		super(texture);
		font = skin.getFont("default-font");
		font.getData().setScale(0.4f);
		
	}
	@Override
	public void draw(Batch batch) {
		setColor(Color.BLUE);
		if(state == TARGET) setColor(Color.GREEN);
		if(state == AGENT) setColor(Color.RED);
		if(state == OBSTACLE) setColor(Color.BLACK);
		if(state == LEADER) setColor(Color.YELLOW);
		super.draw(batch);
		if(showLabel)
		{
			font.draw(batch,label,getX(),getY()+font.getLineHeight());
		}
	}
}
