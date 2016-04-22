package com.cemonem.coopbot;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;

public class Block extends Sprite {
	
	public static final int EMPTY = 0;
	public static final int TARGET = 1;
	public static final int AGENT = 2;
	public static final int OBSTACLE = 3;
	public static final int LEADER = 4;
	
	
	public int state = EMPTY;
	public float value = 0;
	
	
	public Block(Texture texture) {
		super(texture);
	}
	@Override
	public void draw(Batch batch) {
		setColor(Color.BLUE);
		if(state != EMPTY) setAlpha(1.0f);
		else setAlpha(1.0f - value);
		if(state == TARGET) setColor(Color.GREEN);
		if(state == AGENT) setColor(Color.RED);
		if(state == OBSTACLE) setColor(Color.BLACK);
		if(state == LEADER) setColor(Color.YELLOW);
		super.draw(batch);
	}
}
