package server;
import java.util.Random;
import java.util.StringTokenizer;

/* CS3283 Project
 * 
 * Event handler: Handles game events upon request
 * Based on different request from client and respond accordingly
 * 
 */

public class EventHandler {

	final int ROW = 3;		// Number of rows of Nodes
	final int COLUMN = 3;	// Number of column of Nodes

	final int N_NUM = 9;	// Total number of Nodes
	final int T_NUM = 3;	// Number of treasures on the map
	final int P_NUM = 2;	// Number of players
	final int MG_NUM = 2;	// Number of mini-games

	// Event code
	final int invalidEvent = 0;
	final int loginEvent = 1; 
	final int mapUpdateEvent = 2;
	final int openTreasureEvent = 3;
	final int scoreUpdateEvent = 4;
	final int endOfGameEvent = 5;

	Random randomGenerator;

	// Stores when player informations, add player when they connects to the server for the first time
	// there is no playerID stored, the index of the array is equals to playerID 
	// Currently stored values are: logged on? and playerScore

	private int[][] playerList;

	private int[] treasureList;	// stores treasure location
	private int globalEvent;		// store when and where the global event happens

	// TinyOS
	//private TinyOsLoader tinyOsLoader; 

	// Constructor
	public EventHandler() {	

		//tinyOsLoader = new TinyOsLoader(P_NUM);
		randomGenerator = new Random();
		initializeTreasureList();
		initializePlayerList();
		globalEvent = 0;
	}

	// return 
	public String computeEventsReply(String request) {

		String reply = "Invalid";

		StringTokenizer requestToken;
		requestToken = new StringTokenizer(request, ";");

		int eventType = Integer.parseInt(requestToken.nextToken());
		int playerID = Integer.parseInt(requestToken.nextToken());

		System.out.println("eventType: " + eventType + " [Eventhandler.java]");
		System.out.println("playerID: " + playerID + " [Eventhandler.java]");

		switch (eventType)
		{
		case invalidEvent: reply = invalidEvent(playerID);
		break;

		case loginEvent: reply = loginEvent(playerID);
		break;

		case mapUpdateEvent: reply = mapUpdateEvent(playerID);
		break;

		case openTreasureEvent: reply = openTreasureEvent(playerID, Integer.parseInt(requestToken.nextToken() ));
		break;

		case scoreUpdateEvent: reply = scoreUpdateEvent(playerID, Integer.parseInt(requestToken.nextToken() ));
		break;

		case endOfGameEvent: reply = endOfGameEvent(playerID);
		break;

		default: reply = "Invalid";
		break;
		}

		return reply;
	}

	private void initializeTreasureList() {

		treasureList = new int[N_NUM];

		// Initialize all treasure attributes to zero
		for(int i = 0; i<N_NUM; i++)
			treasureList[i] = 0;

		int counter = 0;

		while(counter < T_NUM)
		{
			int i =  randomGenerator.nextInt(N_NUM);

			if (treasureList[i] == 0)
			{
				treasureList[i] = 1;
				counter++;	
			}
		}

		// prints current treasure layout 
		System.out.println("Treasure layout [EventHandler.java]");
		for(int i = 0; i<N_NUM; i++)
		{
			System.out.print(treasureList[i] + " ");
		}
		System.out.println("\nTreasure info initiatised ...[EventHandler.java]");
	}

	private void initializePlayerList() 
	{
		playerList = new int[P_NUM][2];

		for(int i = 0; i<P_NUM; i++)
		{
			playerList[i][0] = 0;
			playerList[i][1] = 0;
		}

		for(int i = 0; i<P_NUM; i++)
			System.out.println("Player " + i + ": " + " logged on: " + playerList[i][0] + " playerScore: " + playerList[i][1]);

		System.out.println("Player info initiatised ...[EventHandler.java]");
	}

	private String invalidEvent(int playerID) {
		return "Invalid";
	}

	private String loginEvent(int playerID) {
		// Game Server's loginEvent reply format: Failure or Successful or Already Logon
		String reply = "";

		if( playerID >= P_NUM)
			reply = "Failure";
		else 
			if( playerList[playerID][0] == 0)
			{
				playerList[playerID][0] = 1;
				reply = "Successful";
			}
			else 
				if(playerList[playerID][0] == 1)
					reply = "Already Logon";

		return reply;
	}

	// Return player's map location upon request
	private String mapUpdateEvent(int playerID) 
	{
		// reply format: p1 logon status, p1 score,p1 location, p2 ... pP_NUM, treasure0,1,2,3 ... N_NUM.
		String reply = "";

		String l_reply = "";
		String t_reply = "";
		String g_reply = "";

		//tinyOS, use only this only when tinyOs is available

/*			for( int i = 0; i < P_NUM; i++)
		  	{
		 	l_reply += playerList[i][0] + ";";
		 	l_reply += playerList[i][1] + ";";
			l_reply += tinyOsLoader.getPlayerLocation(i) + ";";
			}*/
		 

		// random player location for testing without tinyos
		for(int i = 0; i < P_NUM; i++)
		{
			l_reply += playerList[i][0] + ";";
			l_reply += playerList[i][1] + ";";
			l_reply += randomGenerator.nextInt(N_NUM) + ";";
		}

		// convert treasure info into a string separated by ";"
		for(int i = 0; i < N_NUM; i++)
		{
			t_reply += treasureList[i];
			t_reply +=";";
		}

		g_reply = globalEvent + ";";

		reply = l_reply + t_reply + g_reply;
		
		System.out.println("Player: " + playerID + " request for map");
		
		return reply;
	}

	private String openTreasureEvent(int playerID, int playerLocation) {
		// Game Server's openTreasureEvent reply format: a mini game ID: 0 1 2 ... < MG_NUM
		String reply = "";

		// generates a random mini gameID
		int mgID = randomGenerator.nextInt(MG_NUM);

		// Based on current treasure location, remove it and generate the treasure at some other location
		
		// tinyos
		//if(treasureList[tinyOsLoader.getPlayerLocation(playerID)]==1)
		
		// no tinyos
		if(treasureList[playerLocation] == 1)
		while(true)
		{
			int i =  randomGenerator.nextInt(N_NUM);

			if (treasureList[i] == 0)
			{
				treasureList[i] = 1;
				//treasureList[tinyOsLoader.getPlayerLocation(playerID)] = 0;
				treasureList[playerLocation] = 0;
				break;
			}
		}
		
		System.out.print(playerID +" :========================== open chest ====================================");
		
		reply += mgID;
		return reply;
	}

	private String scoreUpdateEvent(int playerID, int newScore) {
		// Game Server's scoreUpdateEvent reply format: Failure or Successful
		
		String reply = "";

		if (playerID < P_NUM){
			playerList[playerID][1] = newScore;
			reply = "Successful";
		}
		else 
			reply = "Failure";
		
		return reply;
	}

	private String endOfGameEvent(int playerID) {

		String playerScore = "";

		for( int i = 0; i < P_NUM; i++){
			playerScore += playerList[i][1];
			if( i !=  (P_NUM - 1))
				playerScore +=";";
		}

		return playerScore;
	}

}
