package server;

import java.util.Random;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

/* CS3283 Project
 * 
 * Event handler: Handles game events upon request
 * Based on different request from client and respond accordingly
 * 
 */

public class EventHandler {

	final int ROW = 3; // Number of rows of Nodes
	final int COLUMN = 3; // Number of column of Nodes

	final int N_NUM = 9; // Total number of Nodes
	final int T_NUM = 3; // Number of treasures on the map
	final int P_NUM = 4; // Number of players
	final int K_NUM = 20; // Number of key codes

	// Event code
	final int invalidEvent = 0;
	final int loginEvent = 1;
	final int mapUpdateEvent = 2;
	final int openTreasureEvent = 3;
	final int scoreUpdateEvent = 4;
	final int addKeyEvent = 5;
	final int endOfGameEvent = 6;

	// Various event durations (seconds)
	int countdownDurations = 6;
	int beforeFallingCoinsDurations = 6;
	int fallingCoinsDurations = 6;
	int totalGameDurations = 600;
	
	Random randomGenerator;

	// Stores when player informations, add player when they connects to the
	// server for the first time
	// there is no playerID stored, the index of the array is equals to playerID
	// Currently stored values are: logged on?, playerScore and number of keys
	private int[][] playerList;

	private int[] treasureList; // stores treasure location
	private int[] keyCodeList;  // stores the key codes

	// globalEvent code: Stores the current global game status
	// 0 = pre-game
	// 1 = countdown once the first player logon to the server
	// 2 = game starts
	// 3 = falling coins starts
	// 4 = falling coins ends
	// 5 = game end
	private int globalEvent;
	Timer timer;


	// for testing purpose
	private static int[] playerLocation;

	// TinyOS
	// private TinyOsLoader tinyOsLoader;

	// Constructor
	public EventHandler() {

		// TinyOS
		// tinyOsLoader = new TinyOsLoader(P_NUM);
		randomGenerator = new Random();
		initializeTreasureList();
		initializePlayerList();
		initializeKeyCodeList();
		globalEvent = 0;
		
	    timer = new Timer();
		
		initializePlayerLocation();
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

		switch (eventType) {
		case invalidEvent:
			reply = invalidEvent(playerID);
			break;

		case loginEvent:
			reply = loginEvent(playerID);
			break;

		case mapUpdateEvent:
			reply = mapUpdateEvent(playerID);
			break;

		case openTreasureEvent:
			reply = openTreasureEvent(playerID,
					Integer.parseInt(requestToken.nextToken()));
			break;

		case scoreUpdateEvent:
			reply = scoreUpdateEvent(playerID,
					Integer.parseInt(requestToken.nextToken()));
			break;

		case addKeyEvent:
			reply = addKeyEvent(playerID,
					Integer.parseInt(requestToken.nextToken()));
			break;

		case endOfGameEvent:
			reply = endOfGameEvent(playerID);
			break;

		default:
			reply = "Invalid";
			break;
		}

		return reply;
	}

	private void initializeTreasureList() {
		
		treasureList = new int[N_NUM];

		// Initialize all treasure attributes to zero
		for (int i = 0; i < N_NUM; i++)
			treasureList[i] = 0;

		int counter = 0;

		while (counter < T_NUM) {
			int i = randomGenerator.nextInt(N_NUM);

			if (treasureList[i] == 0) {
				treasureList[i] = 1;
				counter++;
			}
		}

		// prints current treasure layout
		System.out.println("Treasure layout [EventHandler.java]");
		
		for (int i = 0; i < N_NUM; i++) {
			System.out.print(treasureList[i] + " ");
		}
		
		
		System.out
				.println("\nTreasure info initiatised ...[EventHandler.java]");
	}

	private void initializePlayerList() {
		playerList = new int[P_NUM][3];

		for (int i = 0; i < P_NUM; i++) {
			playerList[i][0] = 0;
			playerList[i][1] = 0;
			playerList[i][2] = 0;
		}

		for (int i = 0; i < P_NUM; i++)
			System.out.println("Player " + i + ": " + " logon: "
					+ playerList[i][0] + " Score: " + playerList[i][1]
					+ " keysHeld: " + playerList[i][2]);

		System.out.println("Player info initiatised ...[EventHandler.java]");
	}

	private void initializeKeyCodeList() {
		keyCodeList = new int[K_NUM];

		for (int i = 0; i < K_NUM; i++) {
			keyCodeList[i] = randomGenerator.nextInt(9000) + 1000;
		}

		System.out.println(getKeyCodeString());

		System.out.println("KeyCode info initiatised/loaded ...[EventHandler.java]");
	}

	private void initializePlayerLocation() {
		playerLocation = new int[P_NUM];
		for (int i = 0; i < P_NUM; i++)
			playerLocation[i] = 4;
	}

	private String invalidEvent(int playerID) {
		return "Invalid";
	}

	private String loginEvent(int playerID) {
		// Game Server's loginEvent reply format: Failure or Successful or
		// Already Logon
		String reply = "";

		if (playerID >= P_NUM)
			reply = "Failure";
		else if (playerList[playerID][0] == 0) {
			playerList[playerID][0] = 1;
			reply = "Successful";
		} else if (playerList[playerID][0] == 1)
			reply = "Already Logon";

		// globalEvent controlling
		if (globalEvent == 0 && playerList[playerID][0] == 1){
			
			// starts countdown once the first player logon to the server
			System.out.println("First player logon, \nCountdown starts now");
			globalEvent = 1; 
			timer.schedule(new CountdownTask(), countdownDurations * 1000);
		}

		return reply;
	}

	// Return player's map location upon request
	private String mapUpdateEvent(int playerID) {
		// p1 logon status, p1 score, p1 keys, p1 location, p2 ... pP_NUM,
		// treasure0,1,2,3 ... N_NUM, global event status

		String reply = "";

		String l_reply = "";
		String t_reply = "";
		String g_reply = "";

		// tinyOS, use only this only when tinyOs is available

		/*
		 * for( int i = 0; i < P_NUM; i++) { l_reply += playerList[i][0] + ";";
		 * l_reply += playerList[i][1] + ";"; l_reply += playerList[i][2] + ";";
		 * l_reply += tinyOsLoader.getPlayerLocation(i) + ";"; }
		 */

		int[][] newMove = new int[4][2];
		newMove[0][0] = 0;
		newMove[0][1] = -1;
		newMove[1][0] = 0;
		newMove[1][1] = 1;
		newMove[2][0] = -1;
		newMove[2][1] = 0;
		newMove[3][0] = 1;
		newMove[3][1] = 0;

		// random player location for testing without tinyos
		for (int i = 0; i < P_NUM; i++) {
			l_reply += playerList[i][0] + ";";
			l_reply += playerList[i][1] + ";";
			l_reply += playerList[i][2] + ";";

			int curX = playerLocation[i] / COLUMN;
			int curY = playerLocation[i] % COLUMN;

			while (true) {
				int x = randomGenerator.nextInt(4);

				if (!(curX + newMove[x][0] < 0
						|| curX + newMove[x][0] >= COLUMN
						|| curY + newMove[x][1] < 0 || curY + newMove[x][1] >= ROW)) {
					curX += newMove[x][0];
					curY += newMove[x][1];
					break;
				}

			}

			playerLocation[i] = curX * COLUMN + curY;
			l_reply += playerLocation[i] + ";";
		}

		// convert treasure info into a string separated by ";"
		for (int i = 0; i < N_NUM; i++) {
			t_reply += treasureList[i];
			t_reply += ";";
		}

		g_reply = globalEvent + ";";

		reply = l_reply + t_reply + g_reply;

		System.out.println("Player: " + playerID + " request for map");

		return reply;
	}

	private String openTreasureEvent(int playerID, int playerLocation) {
		// Game Server's openTreasureEvent reply format: NoChest, NoKey or
		// Successful
		String reply = "";

		// check if player has >1 key to open chest
		if (playerList[playerID][2] >= 1) {

			// tinyos
			// if(treasureList[tinyOsLoader.getPlayerLocation(playerID)]==1)

			// no tinyos
			if (treasureList[playerLocation] == 1) {
				while (true) {
					// Based on current treasure location, remove it and
					// generate the treasure at some other location
					int i = randomGenerator.nextInt(N_NUM);

					if (treasureList[i] == 0) {
						treasureList[i] = 1;
						// tinyOS
						// treasureList[tinyOsLoader.getPlayerLocation(playerID)]
						// = 0;

						// no TinyOs
						treasureList[playerLocation] = 0;
						break;
					}
				}

				// score +50 for each opened chest
				playerList[playerID][1] += 50;

				// number of keys -1
				playerList[playerID][2] -= 1;
				reply = "Successful";
			} else
				reply = "NoChest";
		} else
			reply = "NoKey";

		return reply;
	}

	private String scoreUpdateEvent(int playerID, int newScore) {
		// Game Server's scoreUpdateEvent reply format: Failure or Successful

		String reply = "";

		if (playerID < P_NUM) {
			playerList[playerID][1] = newScore;
			reply = "Successful";
		} else
			reply = "Failure";

		return reply;
	}

	private boolean checkKeyCodeValidity(int keyCode) {

		for (int i = 0; i < K_NUM; i++) {
			if (keyCodeList[i] == keyCode)
				return true;
		}

		return false;
	}

	private String addKeyEvent(int playerID, int keyCode) {

		// Game Server's addKeyEvent based on input keyCode, reply format:
		// Failure or Successful

		String reply = "";

		if (checkKeyCodeValidity(keyCode) == true) {
			playerList[playerID][2] += 1;
			reply = "Successful";
		} else
			reply = "Failure";

		System.out.println("Player: " + playerID + " Keycode: " + keyCode + " request for addKeyCode");
		return reply;
	}

	private String endOfGameEvent(int playerID) {

		String playerScore = "";

		for (int i = 0; i < P_NUM; i++) {
			playerScore += playerList[i][1];
			if (i != (P_NUM - 1))
				playerScore += ";";
		}

		return playerScore;
	}
	
	 String getKeyCodeString() {

		String keyCodeString = "";

		for (int i = 0; i < K_NUM; i++) {
			keyCodeString = keyCodeString + keyCodeList[i] + "   ";
			
			if ((i+1)% 10 == 0)
				keyCodeString = keyCodeString + "\n";
		}
		
		return keyCodeString;
	}
	 
	 String getPlayerInfoString() {

		String playerInfoString = "";

		for (int i = 0; i < P_NUM; i++)
			playerInfoString = playerInfoString + "Player " + i + " : " + "  logon: "
					+ playerList[i][0] + "  Score: " + playerList[i][1]
					+ "  keysHeld: " + playerList[i][2] + "  Location: " + playerLocation[i] + "\n";
		
		return playerInfoString;
	}
	 
	 String getTreasureInfoString() {

		String getTreasureInfoString = "";

		for (int i = 0; i < N_NUM; i++) {
			getTreasureInfoString = getTreasureInfoString + treasureList[i] + "   ";
		if((i+1)% COLUMN == 0)
			getTreasureInfoString = getTreasureInfoString + "\n";
		}
		
		return getTreasureInfoString;
	}
	 
	 
	
	// global event scheduling class
	class CountdownTask extends TimerTask {
	    public void run() {
	      System.out.println("countdown Time's up!");
	      System.out.println("Game starts now!");
	      globalEvent = 2;
	  	  timer.schedule(new FallingCoinStartTask(), beforeFallingCoinsDurations * 1000);
	      //timer.cancel(); //Not necessary because we call System.exit
	     // System.exit(0); //Stops the AWT thread (and everything else)
	    }
	  }
	
	class FallingCoinStartTask extends TimerTask {
	    public void run() {
	      System.out.println("Falling Coins starts now!");
	      globalEvent = 3;
	  	  timer.schedule(new FallingCoinEndTask(), fallingCoinsDurations * 1000);
	    }
	  }

	class FallingCoinEndTask extends TimerTask {
	    public void run() {
	      System.out.println("Falling Coins ends now!");
	      globalEvent = 4;
	  	  timer.schedule(new EndofGameTask(), countdownDurations * 1000);
	    }
	  }
	
	class EndofGameTask extends TimerTask {
	    public void run() {
	      System.out.println("END OF GAME!");
	      globalEvent = 5;
	    }
	  }
}
