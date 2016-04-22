COOPBOT - A multi agent path finding algorithm simulator. Has a JS interpreter embedded which allows users to run their agent scripts.

Quick-Start:

------

function init(agent)
{
	if(agent.isLeader) imspecial(agent);
}

function update(agent)
{
	if(agent.isLeader)
	{
		updatefabulously(agent);
		return;
	}
	agent.moveTo(agent.pos.x,agent.pos.y+1);
}

function imspecial(agent)
{
	app.log("LEADER SPEAKING","hi,I'm a leader and my id is " + agent.id );
}

function updatefabulously(agent)
{
	app.log("LEADER SPEAKING(AGAIN)","I'm so gonna move right!");
	agent.moveTo(agent.pos.x+1,agent.pos.y);
}

------

- The init(agent) function and update(agent) function has to be implemented in order for the script to work. If there is an syntax error or runtime  error/exception, the simulation will quit and an ERRORLOG.txt file will be generated.

- The init function is executed for each agent before the simulation starts.

- The update function is executed for each agent at every step of the simulation.

- app.log(str,str) may be used for logging purposes. To see the logs, run the jar file from the console.

- WIDTH and HEIGHT constants can be accessed from the script. They represent dimensions of the map.


------------using java types in the script----------------

Array<Msg>,Array<Pair>,Pair,Msg,Agent are java object types that can be accessed in the script:

- Array<T> has "get(int i)" method to access the indices and "size" field representing its size. I don't recommend using foreach loops, they are usually buggy. SUBSCRIPT NOTATION WONT WORK!

- Pair has two int fields , "x" and "y". usually represents position.

- Msg has one int field "key" and a NativeObject "msg" field. The NativeObject field can be assigned a JS Object, which can be freely attached fields/methods as a delegate. This allows the user to form the messaging system however they want. 

- Attaching fields/methods to the java objects in the script will fail.

Agent class has the following fields and methods:

class Agent
{
	public NativeObject obj; //a field for a JSObject to be assigned. This JS Object can be used as a delegate to attach fields/methods to the agent on // the fly.

	public Pair pos; // represents position. do not change manually.
	public final int id; // an unique id is assigned to each agent arbitrarily. 
	public final boolean isLeader; //

	public Array<Msg> msgs; // messages recieved last turn. The agents do not run their update methods concurrently,but in an arbitrary sequential way.To decouple the ordering of the agents double buffer pattern is used.(http://gameprogrammingpatterns.com/double-buffer.html)

	//Methods

	public int inspectBlock(int x,int y); //inspects the block if it is adjacent to the position of the agent (diagonal blocks can be also inspected). If the queried block is not adjacent, the method will return -1. Otherwise it will return:

	//EMPTY -> if the block is empty.
	//TARGET -> if the block is the target block
	//OBSTACLE -> if the block is an obstacle
	//AGENT -> if the block is occupied by a non-leader agent;
	//LEADER -> if the block is occupied by a leader.
	// all the above constants can be accessed in the scripts without any prefix.
	//IMPORTANT NOTE: do not use TARGET constant to check if the current block is the target, as the agent moves into the target, the inspectBlock method will return AGENT or LEADER and the search won't terminate. Use getTargets() to obtain target locations.

	public boolean moveTo(int x,int y); //moves to the Block if it is possible. The method calls inspectBlock to check if the BLOCK is EMPTY or TARGET. If the block is neither or it is too far to inspect the operation fails and the method returns zero. Otherwise the agent move to the specified location and its pos field will be updated accordingly.

	public Array<Pair> getLeaderLocs(); // returns the positions of leaders (except the position of the agent the method is being called from).
	public Array<Pair> getLocs(); // returns the positions of all agents except the position of the agent the method is being called from).
	public Array<Pair> getTargets(); // returns the positions of all targets in the map.
	public void broadcastMsg(int key,NativeObject val); // broadcasts the message to all agents except the agent the method is being called from. The messages will be received in the next turn.
	public void broadcastMsgToLeaders(int key,NativeObject val); // broadcasts the message to all leaders except the agent the method is being called from (if it is a leader). The messages will be received in the next turn.
	public void updateBlockVal(int x,int y,float h); // This method can be used to visually aid the simulation. The h value is assigned to the opacity of the block color at the coordinates (x,y). The method has no effect on the simulation mechanics.
}

-------constructing maps-----------
the maps can be constructed with any size of an image file. Every pixel is realized as a block in the simulation according to its RGB value:

#FF0000 (blue) -> obstacle
#00FF00 (agent) -> agent
#00FF00 (green) -> target
#FFFF00 (yellow) -> leader
all other color values -> empty
