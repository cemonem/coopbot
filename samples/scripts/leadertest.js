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