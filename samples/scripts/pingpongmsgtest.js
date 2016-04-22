function init(agent)
{
	agent.obj = new Object();
	if(agent.id == 0) agent.obj.messagelist = ["this will go away because of buffers... :(","olum ses verin","kimse yok mu :0","merhabaaa"];
	agent.obj.c = 4;
	leaders = agent.getLeaderLocs();
	printJavaArray(leaders,"agent "+agent.id+" leaders");
	agents = agent.getLocs();
	printJavaArray(agents,"agent "+agent.id+ "agents");
}

function printJavaArray(arr,brief)
{
	for(i = 0;i < arr.size;i++)
	{
		app.log("array - "+brief+ " - "+i," ( "+arr.get(i).x+","+arr.get(i).y+")");
	}
}

function printMsgs(arr,brief)
{
	for(i = 0;i < arr.size;i++)
	{
		app.log("messages -"+brief+ " - "+i," - from: "+arr.get(i).val.id+" - key: "+arr.get(i).key+" content: "+arr.get(i).val.con);
	}
}

function update(agent)
{
	if(agent.obj.c == 0) return;
	agent.obj.c--;
	if(agent.isLeader)
	{
		leaderUpdate(agent);
		return;
	}
	if(agent.id == 0)
	{
		var msg = new Object();
		msg.id = 0;
		msg.con = agent.obj.messagelist[agent.obj.c];
		agent.broadcastMsg(agent.obj.c,msg);
	}
	printMsgs(agent.msgs,"agent "+agent.id+"msgs recieved last turn");
	var msg = new Object();
	msg.id = agent.id;
	msg.con = "hulooog";
	agent.broadcastMsgToLeaders(agent.obj.c,msg);
}


function leaderUpdate(agent)
{
	printMsgs(agent.msgs,"leader "+agent.id+"msgs recieved last turn");
	var vaat = new Object();
	vaat.id = agent.id;
	vaat.con = "kardeslerim bakin bunlar cehapenin oyunu";
	agent.broadcastMsg(agent.obj.c,vaat);
}