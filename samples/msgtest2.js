function init(agent,common)
{
	if(agent.isLeader)
	{
		agent.obj = new Object();
		agent.obj.array = [1,2,3,4];
		agent.obj.c = 4;
		return;
	}
	agent.obj = new Object();
	agent.obj.c = 4;
}	
function update(agent,common)
{
	if(agent.isLeader)
	{
		updateLeader(agent,common);
		return;
	}
	for(i = 0;i < agent.msgs.size;i++)
	{
		app.log("MESSAGE RECIEVED FROM leader?:",agent.msgs.get(i).key);
		if(agent.msgs.get(i).key == agent.obj.c)
		{
			var leadermsg = agent.msgs.get(i).val;
			var msg = new Object();
			var arr = [];
			for(i = 0;i < leadermsg.array.length - 1;i++)
			{
				arr.push(leadermsg.array[i]);
			}
			msg.array = arr;
			msg.id = agent.id;
			agent.broadcastMsgToLeaders(agent.obj.c,msg);
		}
	}
}

function updateLeader(agent,common)
{
	if(agent.obj.array.length != 0)
	{
		agent.broadcastMsg(agent.obj.c,agent.obj);
	}
	var a = ""
	for(i = 0;i < agent.obj.array.length;i++)
	{
		a = a + " " + agent.obj.array[i];
	}
	app.log("LEADER ARRAY",a);
	for(i = 0;i < agent.msgs.size;i++)
	{
		app.log("MESSAGE RECIEVED FROM:",agent.msgs.get(i).val.id);
		if(agent.msgs.get(i).key == agent.obj.c)
		{
			agent.obj.array = agent.msgs.get(i).val.array;
			
		}
	}

}
