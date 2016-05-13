package com.cemonem.coopbot;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;

public class Simulation {
	
	Texture texture;
	Block[][] grid;
	int width,height;
	Array<Agent> agents;
	Array<Pair> targets;
	Array<Agent> leaders;
	ScriptManager scriptManager;
	Skin skin;
	
	public static int nextAgentId = 0;
	
	public Simulation(Pixmap pixmap,Texture texture,String scriptPath,Skin skin) {
		agents = new Array<Simulation.Agent>();
		leaders = new Array<Agent>();
		this.texture = texture;
		width = pixmap.getWidth();
		height = pixmap.getHeight();
		targets = new Array<Simulation.Pair>();
		grid = new Block[width][height];
		this.skin = skin;

		build(pixmap);
		scriptManager = new ScriptManager(scriptPath);
	}
	
	public void build(Pixmap pixmap)
	{
		for(int i = 0;i < width;i++)
		{
			for(int j = 0;j < height;j++)
			{
				grid[i][j] = new Block(texture,skin);
				int color = pixmap.getPixel(i, height - j - 1);
				if(color == 0xFF0000FF)
				{
					Agent agent = new Agent(i,j,false);
					agents.add(agent);
					grid[i][j].state = Block.AGENT;
				}
				if(color == 0xFFFF00FF)
				{
					Agent leader = new Agent(i,j,true);
					agents.add(leader);
					leaders.add(leader);
					grid[i][j].state = Block.LEADER;
				}
				else if(color == 0x0000FFFF)
				{
					grid[i][j].state = Block.OBSTACLE;
				}				
				else if(color == 0x00FF00FF)
				{
					targets.add(new Pair(i,j));
					grid[i][j].state = Block.TARGET;
				}
				grid[i][j].setPosition(i*16, j*16);	
			}
		}
	}
	
	public void update()
	{
		scriptManager.update();
	}
	
	public void draw(Batch batch)
	{
		for(Block[] r : grid)
		{
			for(Block b : r)
			{
				b.draw(batch);
			}
		}			
	}
	
	public class Pair
	{
		public int x,y;
		public Pair(int x,int y){this.x = x;this.y = y;}
	}
	
	public class Msg
	{
		public int key;
		public NativeObject val;
		public Msg(int key,NativeObject val){this.key = key;this.val = val;}
	}
	
	public class Agent
	{	
		
		private Array<Msg> nextMsgBuffer;
		public Array<Msg> msgs;
		public final int id;
		public final boolean isLeader;
		
		
		public NativeObject obj;
		public Pair pos;
		public Agent(int x,int y,boolean isLeader)
		{
			msgs = new Array<Msg>();
			nextMsgBuffer = new Array<Msg>();
			pos = new Pair(x,y);
			id = nextAgentId;
			nextAgentId++;
			this.isLeader = isLeader;
		}
		
		public int inspectBlock(int x,int y)
		{
			if(Math.abs(this.pos.x - x) > 1 || Math.abs(this.pos.y - y) > 1 || x < 0 || y < 0 || x >= width || y >= height) return -1;
			return grid[x][y].state;
		}
		
		public boolean moveTo(int x,int y)
		{
			if(inspectBlock(x,y) != Block.EMPTY && inspectBlock(x,y) != Block.TARGET) return false;
			if(Math.abs(pos.x - x) == 1 && Math.abs(pos.y-y) == 1) return false;
			grid[x][y].state  = grid[this.pos.x][this.pos.y].state;
			grid[this.pos.x][this.pos.y].state = Block.EMPTY;
			
			this.pos.x = x;
			this.pos.y = y;
			return true;
		}
		
		public Array<Pair> getLeaderLocs()
		{
			Array<Pair> result = new Array<Simulation.Pair>();
			for(Agent a : leaders)
			{
				result.add(a.pos);
			}
			
			return result;			
		}
		
		public Array<Pair> getLocs()
		{
			Array<Pair> result = new Array<Simulation.Pair>();
			for(Agent a : agents)
			{
				result.add(a.pos);
			}
			
			return result;
		}
		
		public Array<Pair> getTargets()
		{
			return targets;
		}
		
		public void broadcastMsg(int key,NativeObject val)
		{
			for(Agent a : agents)
			{
				a.nextMsgBuffer.add(new Msg(key, val));
			}
		}
		
		public void broadcastMsgToLeaders(int key,NativeObject val)
		{
			for(Agent a : leaders)
			{
				a.nextMsgBuffer.add(new Msg(key, val));
			}
		}
			
		public void switchBuffers()
		{
			msgs = nextMsgBuffer;
			nextMsgBuffer = new Array<Msg>();
		}
		
		public void setLabel(int x,int y,String h)
		{
			grid[x][y].label = h;
		}
		
		public void showLabel(int x,int y,boolean toggle)
		{
			grid[x][y].showLabel = toggle;
		}
	}
	
	public class ScriptManager
	{
		Context cx;
		Scriptable scope;
		Function init;
		Function update;
		Object[][] agentArgs;
		NativeObject common = new NativeObject();
		
		public ScriptManager(String scriptPath)
		{
			cx = Context.enter();
			scope = cx.initStandardObjects();
			
			agentArgs = new Object[agents.size][2];
			for(int i = 0;i < agents.size;i++)
			{
				agentArgs[i][0] = agents.get(i);
				agentArgs[i][1] = common;
			}
			
			try {
				cx.evaluateReader(scope, new FileReader(scriptPath), "agent-ai", 1, null);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ScriptableObject.putProperty(scope,"EMPTY",Context.javaToJS(Block.EMPTY, scope));
			ScriptableObject.putProperty(scope,"TARGET",Context.javaToJS(Block.TARGET, scope));
			ScriptableObject.putProperty(scope,"AGENT",Context.javaToJS(Block.AGENT, scope));
			ScriptableObject.putProperty(scope,"LEADER",Context.javaToJS(Block.LEADER, scope));
			ScriptableObject.putProperty(scope,"OBSTACLE",Context.javaToJS(Block.OBSTACLE, scope));
			ScriptableObject.putProperty(scope, "app", Context.javaToJS(Gdx.app, scope));
			ScriptableObject.putProperty(scope, "WIDTH", Context.javaToJS(width, scope));
			ScriptableObject.putProperty(scope, "HEIGHT", Context.javaToJS(height, scope));
			
			
			init = (Function)scope.get("init",scope);
			update = (Function)scope.get("update",scope);
			
			for(Object[] args : agentArgs)
			{
				init.call(cx, scope, scope, args);
			}
		}
		
		public void update()
		{
			for(Object[] args : agentArgs)
			{
				update.call(cx,scope,scope,args);
			}
			for(Agent a : agents)
			{
				a.switchBuffers();
			}
		}
		
		public void exit()
		{
			Context.exit();
		}
	}
}
