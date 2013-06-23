public interface LevelsData
{
	//total number of levels
	static final int totalLevels = 2;
	
	static final int subFactor = 200;
	
	//total number of enemies
	static final int enemyNo[] = {2,3};
	
	//total number of BreakableWalls
	static final int wallBNo[] = {48,32};
	
	//total number of UnBreakableWalls
	static final int wallUNo[] = {48,2};
	
	//starting position of each enemy
	static final int enemyStartPositionX[][]={{984,984,0},
									   {924,924,924}};
	
	static final int enemyStartPositionY[][]={{192,576,0},
									   {1,384,668}};
	
	//position of the BreakableWall
	static final int wallBPositionX[][] = {{200,240,280,320,360,400,440,480,520,560,600,640,680,720,760,800,
											200,240,280,320,360,400,440,480,520,560,600,640,680,720,760,800,
											200,240,280,320,360,400,440,480,520,560,600,640,680,720,760,800,
	},			 						  {360-subFactor,400-subFactor,440-subFactor,480-subFactor,840-subFactor,880-subFactor,920-subFactor,960-subFactor,640-subFactor,680-subFactor,640-subFactor,680-subFactor,640-subFactor,680-subFactor,640-subFactor,680-subFactor,480-subFactor,520-subFactor,560-subFactor,760-subFactor,800-subFactor,840-subFactor,520-subFactor,560-subFactor,600-subFactor,720-subFactor,760-subFactor,800-subFactor,600-subFactor,640-subFactor,680-subFactor,720-subFactor}};
	
	static final int wallBPositionY[][] = {{142,142,142,142,142,142,142,142,142,142,142,142,142,142,142,142,322,322,322,322,322,322,322,322,322,322,322,322,322,322,322,322,
											446,446,446,446,446,446,446,446,446,446,446,446,446,446,446,446},
										  {120,120,120,120,120,120,120,120,200,200,240,240,280,280,320,320,520,520,520,520,520,520,560,560,560,560,560,560,600,600,600,600}};;
	
	//position of the UnBreakableWall
	static final int wallUPositionX[][] = {{200,240,280,320,360,400,440,480,520,560,600,640,680,720,760,800,
											200,240,280,320,360,400,440,480,520,560,600,640,680,720,760,800,
											200,240,280,320,360,400,440,480,520,560,600,640,680,720,760,800},
	        								{900,900,900}};;
	static final int wallUPositionY[][] = {{102,102,102,102,102,102,102,102,102,102,102,102,102,102,102,102,
											282,282,282,282,282,282,282,282,282,282,282,282,282,282,282,282,
											486,486,486,486,486,486,486,486,486,486,486,486,486,486,486,486},
			  						{900,900,900}};;
	
	//starting position of the player
	static final int playerStartPositionX[] = {40,40};
	static final int playerStartPositionY[] = {384,384};
}
