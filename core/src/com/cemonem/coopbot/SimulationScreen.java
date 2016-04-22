package com.cemonem.coopbot;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class SimulationScreen implements Screen {
	
	float updateTime = 0;
	float threshold = 1f;
	boolean pause = false;
	SpriteBatch batch;
	String mapPath;
	String scriptPath;
	Simulation simulation;
	Texture tile;
	OrthographicCamera camera;
	
	public SimulationScreen(String mapPath, String scriptPath) {
		this.mapPath = mapPath;
		this.scriptPath = scriptPath;
	}
	

	@Override
	public void show() {
		updateTime = 0.5f;
		batch = new SpriteBatch();
		try
		{
			tile = new Texture(Gdx.files.internal("tile.png"));
			Pixmap pixmap = new Pixmap(Gdx.files.absolute(mapPath));
			simulation = new Simulation(pixmap, tile,scriptPath);
			pixmap.dispose();
			camera = new OrthographicCamera(16*pixmap.getWidth(),16*pixmap.getHeight());
			camera.translate(16*pixmap.getWidth()/2.0f, 16*pixmap.getHeight()/2.0f);;
		}
		catch(RuntimeException e)
		{
			FileHandle errorlog = Gdx.files.local("ERRORLOG.txt");
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			errorlog.writeString(sw.toString(),false);
			throw e;
		}


	}

	@Override
	public void render(float delta) {
		
		if(Gdx.input.isKeyPressed(Input.Keys.X))
		{
			threshold += 0.005f;
			threshold = MathUtils.clamp(threshold, 0.001f, 2f);
		}
		if(Gdx.input.isKeyPressed(Input.Keys.Z))
		{
			threshold -= 0.005f;
			threshold = MathUtils.clamp(threshold, 0, 2f);
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.C))
		{
			pause = !pause;
		}
		
		updateTime += delta;
		if(updateTime > threshold && !pause)
		{
			updateTime = 0;
			try
			{
				simulation.update();
			}
			catch(RuntimeException e)
			{
				FileHandle errorlog = Gdx.files.local("ERRORLOG.txt");
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);
				errorlog.writeString(sw.toString(),false);
				throw e;
			}
		}
		
		Gdx.gl.glClearColor(1.0f, 1.0f, 1.0f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(tile,100,100);
		simulation.draw(batch);
		batch.end();

	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		dispose();
	}

	@Override
	public void dispose() {
		tile.dispose();
		batch.dispose();

	}

}
