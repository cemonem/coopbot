
//init gets called for every agent before the simulation starts
function init(agent)
{
	agent.obj = new Object();
	// this has to be done as we can't create a genuine javascript object in java with rhino (or I don't know how to :))
	// now we can use this object as a delegate for the agent object to attach whatever method/field we would like.
	agent.obj.h = [];
	for(i = 0;i < WIDTH;i++) agent.obj.h[i] = [];
	// heuristic, this will be initially the euclidean distance between target and the current block and will converge to real path cost onwards

	agent.obj.r = [];
	for(i = 0;i < WIDTH;i++) agent.obj.r[i] = [];
	// result array, will hold possible moves from current block

	agent.obj.prev = undefined;
	// previous move

	var targets = agent.getTargets();
	var target = targets.get(0);
	var minDist = dist(agent.pos,target);
	// not targets[0]! this is a java Array<Pair> and .get method will return a pair with fields x and y. The array size is in the size field.
	// setting the target as the target with the minimum distance, also assuming there is at least one target in the map
	// also avoid using foreach loops, they are evil and buggy as an amazonian jungle
	for(i = 0;i < targets.size;i++)
	{
		var tmp = dist(agent.pos,targets.get(i))
		if(minDist > tmp)
		{
			target = targets.get(i);
			minDist = tmp;
		}
	}

	agent.obj.target = target;
	agent.obj.h[agent.pos.x][agent.pos.y] = dist(agent.pos,target);
	// set the heuristic of the first block

}

//this function gets called in every step of the simulation
function update(agent)
{
	//creating local vars to avoid prefixing with agent.obj everytime
	var pos = agent.pos;
	var h = agent.obj.h;
	var r = agent.obj.r;
	var target = agent.obj.target;
	var prev = agent.obj.prev;

	if(pos.x == target.x && pos.y == target.y)
	{
		app.log("SUCCESS","yay!");
		return;
	}

	r[pos.x][pos.y] = generateMovables(agent,pos,h,target);
	//recalculate possible moves as the agent might have been blocked with another agent before


	//improve the heuristic of the previous block
	// if the blocks reachable from the previous block has the minumum path cost of c_curr, 
	// the previous block must have at least path cost of c_curr+1 as blocks are 1 units far from each other
	if(prev != undefined)
	{
		var min = lrtaCost(r[prev.x][prev.y][0],h);
		//  assume any block is reachable by at least one block otherwise the agent would be trapped
		for(i = 0;i < r[prev.x][prev.y].length;i++)
		{
			var tmp = lrtaCost(r[prev.x][prev.y][i],h);
			min = tmp < min ? tmp : min;
		}
		h[prev.x][prev.y] = min;
	}

	// picking the move with the lowest heuristic
	var move = r[pos.x][pos.y][0];
	var minH = h[move.x][move.y];
	for(i = 0;i < r[pos.x][pos.y].length;i++)
	{
		var tmpMove = r[pos.x][pos.y][i];
		var tmp = h[tmpMove.x][tmpMove.y];
		if(minH > tmp)
		{
			move = tmpMove;
			minH = tmp;
		}
	}

	agent.obj.prev = pos;

	agent.moveTo(move.x,move.y);
}

function lrtaCost(pos,h)
{
	return 1+h[pos.x][pos.y];
}

function generateMovables(agent,pos,h,target)
{
	var result = [];
	for(i = -1;i <= 1;i++)
	{
		for(j = -1;j <= 1;j++)
		{
			// no diagonal moves!
			if(i == j) continue;
			if(-i == j) continue;
			// cannot move into an obstacle or an agent or a leader!
			if(agent.inspectBlock(pos.x+i,pos.y+j) == EMPTY || agent.inspectBlock(pos.x+i,pos.y+j) == TARGET)
			{
				var newpos = new Object();
				newpos.x = pos.x+i;
				newpos.y = pos.y+j;
				if(h[newpos.x][newpos.y] == undefined) h[newpos.x][newpos.y] = dist(newpos,target);
				result.push(newpos);
			}
		}
	}
	return result;
}

function dist(pair1,pair2)
{
	return Math.sqrt((pair1.x-pair2.x)*(pair1.x-pair2.x)+(pair1.y-pair2.y)*(pair1.y-pair2.y));
}