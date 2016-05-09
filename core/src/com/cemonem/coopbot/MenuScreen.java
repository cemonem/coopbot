package com.cemonem.coopbot;

import java.awt.FileDialog;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MenuScreen implements Screen {
	
	Game game;
	Stage stage;
	Skin skin;
	String mapPath;
	String scriptPath;
	Preferences prefs;
	
	public MenuScreen(Game game) {
		this.game = game;
		prefs = Gdx.app.getPreferences("coopbot_prefs");
		mapPath = prefs.getString("mapPath", "");
		scriptPath = prefs.getString("scriptPath","");
	}
	
	@Override
	public void show() {
		
		stage = new Stage(new ScreenViewport());
		Gdx.input.setInputProcessor(stage);
		
		skin = new Skin(Gdx.files.internal("uiskin.json"));
		
		Table table = new Table();
		table.setFillParent(true);
		table.bottom().pad(200);
		
		TextButton mapButton = new TextButton("Pick Map...",skin);
		final Label mapLabel = new Label("", skin);
		if(mapPath != "") mapLabel.setText((new File(mapPath)).getName());
		
		mapButton.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				
			    JFrame frame = new JFrame();
			    FileDialog d = new FileDialog(frame);
			    d.setTitle("Pick a PNG File");
			    d.setVisible(true);
				if(d.getFile() != "null")
				{
					mapPath = d.getDirectory() + d.getFile();
					prefs.putString("mapPath",mapPath);
					mapLabel.setText(d.getFile());
				}
			}
		});
		table.add(mapButton).pad(10);
		table.add(mapLabel);
		table.row();
		
		TextButton scriptButton = new TextButton("Pick JS Script...",skin);
		final Label scriptLabel = new Label("", skin);
		if(mapPath != "") scriptLabel.setText((new File(scriptPath)).getName());
		
		scriptButton.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				
			    JFrame frame = new JFrame();
			    FileDialog d = new FileDialog(frame);
			    d.setTitle("Pick a JS File");
			    d.setVisible(true);
				if(d.getFile() != "null")
				{
					scriptPath = d.getDirectory() + d.getFile();
					prefs.putString("mapPath",scriptPath);
					scriptLabel.setText(d.getFile());
				}
			}
		});
		table.add(scriptButton).pad(10);
		table.add(scriptLabel);
		table.row();
		
		TextButton startButton = new TextButton("Start!", skin);
		startButton.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				prefs.flush();
				if(mapPath != "" && scriptPath != "") game.setScreen(new SimulationScreen(mapPath,scriptPath,skin));
			}
		});
		table.add(startButton);
		stage.addActor(table);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1.0f, 1.0f, 1.0f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(delta);
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
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
		stage.dispose();
	}

}
