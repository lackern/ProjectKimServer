/* CS3283/CS3284 Project
 * 
 * Event handler: Handles game events upon request
 * Based on different request from client and respond accordingly
 * 
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

public class EventHandler {

	final int ROW = 7; // Number of rows of Nodes
	final int COLUMN = 14; // Number of column of Nodes

	final int N_NUM = ROW * COLUMN; // Total number of Nodes
	final int T_NUM = N_NUM / 3; // Number of treasures on the map
	final int P_NUM = 4; // Number of players
	final int K_NUM = N_NUM; // Number of key codes

	// Event code
	final int invalidEvent = 0;
	final int loginEvent = 1;
	final int mapUpdateEvent = 2;
	final int openTreasureEvent = 3;
	final int scoreUpdateEvent = 4;
	final int addKeyCodeEvent = 5;
	final int endOfGameEvent = 6;

	// Various event durations (seconds)
	int totalcountdownDurations = 10;
	int beforeFallingCoinsDurations = 10;
	int fallingCoinsDurations = 10;
	int totalGameDurations = 60;
	int currentPreGameTime = totalcountdownDurations;
	int currentInGameTime = totalGameDurations;
	int rebootDurations = 5;

	Random randomGenerator;

	// Stores when player informations, add player when they connects to the
	// server for the first time
	// there is no playerID stored, the index of the array is equals to playerID
	// Currently stored values are: logged on?, playerScore and number of keys
	private int[][] playerList;
	private int[] treasureList; // stores treasure location
	private int[] generatedkeyCodeList; // stores the generated key codes
	private int[] loadedKeyCodeList; // stores the key codes
	private int[][] keyCodeLocationPairingList; // stores the pairing
	// information and number of
	// times the key code have been
	// used

	// globalEvent code: Stores the current global game status
	// 0 = pre-game
	// 1 = countdown once the first player logon to the server
	// 2 = game starts
	// 3 = falling coins starts
	// 4 = falling coins ends
	// 5 = game end
	// 6 = server rebooting
	private int globalEventStatus;
	Timer timer;

	// for testing purpose
	boolean generateNewKeyCodeList = false;
	private static int[] playerLocation;

	// TinyOS
	// private TinyOsLoader tinyOsLoader;

	// Constructor
	public EventHandler() throws IOException {

		// TinyOS
		// tinyOsLoader = new TinyOsLoader(P_NUM);
		randomGenerator = new Random();
		if (generateNewKeyCodeList)
			generateRandomKeyCodeList();

		initializeTreasureList();
		initializePlayerList();
		globalEventStatus = 0;
		timer = new Timer();

		loadKeyCodeList();

		// For testing purpose when TinyOS is not in use.
		initializePlayerLocation();
	}

	/* Return reply for each request */
	public String computeEventsReply(String request) {
		String reply = "invalidEvent";

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

		case addKeyCodeEvent:
			reply = addKeyCodeEvent(playerID,
					Integer.parseInt(requestToken.nextToken()));
			break;

		case endOfGameEvent:
			reply = endOfGameEvent(playerID);
			break;

		default:
			reply = "invalidEvent";
			break;
		}

		return reply;
	}

	public void movePlayer(int playerID, int newDirection) {
		int[][] newMove = new int[4][2];
		newMove[0][0] = -1;
		newMove[0][1] = 0;
		newMove[1][0] = 1;
		newMove[1][1] = 0;
		newMove[2][0] = 0;
		newMove[2][1] = -1;
		newMove[3][0] = 0;
		newMove[3][1] = 1;

		int curX = playerLocation[playerID] / COLUMN;
		int curY = playerLocation[playerID] % COLUMN;

		if (!(curX + newMove[newDirection][0] < 0
				|| curX + newMove[newDirection][0] >= ROW
				|| curY + newMove[newDirection][1] < 0 || curY
				+ newMove[newDirection][1] >= COLUMN)) {
			curX += newMove[newDirection][0];
			curY += newMove[newDirection][1];
		}

		playerLocation[playerID] = curX * COLUMN + curY;
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
			if ((i + 1) % COLUMN == 0)
				System.out.print("\n");
		}

		System.out.println("Treasure info initiatised ...[EventHandler.java]");
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

	private void generateRandomKeyCodeList() {
		generatedkeyCodeList = new int[K_NUM];
		for (int i = 0; i < K_NUM; i++) {
			generatedkeyCodeList[i] = randomGenerator.nextInt(9000) + 1000;
			System.out.println(generatedkeyCodeList[i] + ";" + i + ";");
		}
		System.out
		.println("KeyCode info randomly generated ...[EventHandler.java]");
	}

	 void loadKeyCodeList() throws IOException    {

		loadedKeyCodeList = new int[K_NUM];
		keyCodeLocationPairingList = new int[K_NUM][P_NUM + 1];
		// initialize number of tries keyCode used by player to 0
		for (int i = 0; i < K_NUM; i++) {
			for (int j = 0; j < P_NUM; j++) {
				keyCodeLocationPairingList[i][j] = 0;
			}
		}

		BufferedReader in = new BufferedReader(new FileReader("src/keyCodeList.txt"));
		StringTokenizer stringToken;

		for (int i = 0; i < K_NUM; i++) {
			String string = in.readLine();
			stringToken = new StringTokenizer(string, ";");
			loadedKeyCodeList[i] = Integer.parseInt(stringToken.nextToken());
			keyCodeLocationPairingList[i][P_NUM] = Integer.parseInt(stringToken
					.nextToken());
		}

		in.close();
		System.out.print(getKeyCodeString());
		System.out.println("KeyCode info loaded ...[EventHandler.java]");

	}

	private void initializePlayerLocation() {
		playerLocation = new int[P_NUM];
		for (int i = 0; i < P_NUM; i++)
			playerLocation[i] = (int) (COLUMN * 1.5);
	}

	private String invalidEvent(int playerID) {
		return "invalidEvent";
	}

	/* Game Server's loginEvent reply format: Failure or Successful or AlreadyLogon */
	private String loginEvent(int playerID) {

		String reply = "";

		if (playerID >= P_NUM)
			reply = "Failure";
		else if (playerList[playerID][0] == 0) {
			playerList[playerID][0] = 1;
			reply = "Successful";
		} else if (playerList[playerID][0] == 1)
			reply = "AlreadyLogon";

		// globalEvent controlling
		if (globalEventStatus == 0 && playerList[playerID][0] == 1) {

			// starts countdown once the first player logon to the server
			System.out.println("First player logon, \nCountdown starts now");
			globalEventStatus = 1;
			timer.schedule(new CountdownTask(), 1000);
			System.out.println("countdown Time: " + currentPreGameTime);
		}

		return reply;
	}

	/*
	 * Return player's game info upon request p1 logon status, p1 score, p1
	 * keys, p1 location, p2 ... pP_NUM, treasure0,1,2,3 ... N_NUM, globalEventStatus, preGameCountdownTime, inGameCountdownTime
	 */
	private String mapUpdateEvent(int playerID) {

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
				int newDirection = randomGenerator.nextInt(4);

				if (!(curX + newMove[newDirection][0] < 0
						|| curX + newMove[newDirection][0] >= ROW
						|| curY + newMove[newDirection][1] < 0 || curY
						+ newMove[newDirection][1] >= COLUMN)) {
					curX += newMove[newDirection][0];
					curY += newMove[newDirection][1];
					break;
				}

			}

			// playerLocation[i] = curX * COLUMN + curY;
			l_reply += playerLocation[i] + ";";
		}

		// convert treasure info into a string separated by ";"
		for (int i = 0; i < N_NUM; i++) {
			t_reply += treasureList[i];
			t_reply += ";";
		}

		g_reply = globalEventStatus + ";" + currentPreGameTime + ";" + currentInGameTime + ";";

		reply = l_reply + t_reply + g_reply;

		System.out.println("Player: " + playerID + " request for map");

		return reply;
	}

	/* Game Server's openTreasureEvent reply format: NoChest, NoKey or Successful */
	private String openTreasureEvent(int playerID, int playerLocation) {

		String reply = "";

		// tinyos
		// if(treasureList[tinyOsLoader.getPlayerLocation(playerID)]==1)

		// no tinyos
		if (treasureList[playerLocation] == 1) {

			// check if player has >1 key to open chest
			if (playerList[playerID][2] >= 1) {
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
				reply = "NoKey";
		} else
			reply = "NoChest";

		return reply;
	}

	/* Game Server's scoreUpdateEvent reply format: Failure or Successful */
	private String scoreUpdateEvent(int playerID, int newScore) {
		String reply = "";

		if (playerID < P_NUM) {
			playerList[playerID][1] = newScore;
			reply = "Successful";
		} else
			reply = "Failure";
		return reply;
	}

	/* Game Server's addKeyCodeEvent reply format: InvalidKeyCode, InvalidLocation, KeyCodeUsed or Successful */
	private String addKeyCodeEvent(int playerID, int keyCode) {
		String reply = "";

		for (int i = 0; i < K_NUM; i++) {
			if (loadedKeyCodeList[i] == keyCode) { // KeyCode found
				//TinyOS tinyOsLoader.getPlayerLocation(playerID);
				//if(playerLocation[playerID] == keyCodeLocationPairingList[i][P_NUM]){ // player at the right location
				if(playerLocation[playerID] == keyCodeLocationPairingList[i][P_NUM]){ // player at the right location
					if(keyCodeLocationPairingList[i][playerID] == 0){// KeyCode not used before
						keyCodeLocationPairingList[i][playerID] = 1;
						playerList[playerID][2] += 1;
						reply = "Successful";
						break;
					}
					else reply = "KeyCodeUsed";
					break;
				}
				else reply = "InvalidLocation";
				break;
			} else
				reply = "InvalidKeyCode";
		}

		System.out.println("Player: " + playerID + " Keycode: " + keyCode + " request for addKeyCode");
		return reply;
	}

	/* not in use atm */
	private String endOfGameEvent(int playerID) {
		String playerScore = "";
		for (int i = 0; i < P_NUM; i++) {
			playerScore += playerList[i][1];
			if (i != (P_NUM - 1))
				playerScore += ";";
		}
		return playerScore;
	}

	/* Returns a string for GUI display */
	String getKeyCodeString() {
		String keyCodeString = "";
		for (int i = 0; i < K_NUM; i++) {
			if(i < 10)
				keyCodeString += loadedKeyCodeList[i] + "[0"	+ keyCodeLocationPairingList[i][P_NUM] + "]  ";
			else keyCodeString += loadedKeyCodeList[i] + "["	+ keyCodeLocationPairingList[i][P_NUM] + "]  ";

			if ((i + 1) % 10 == 0)
				keyCodeString += "\n";
		}
		return keyCodeString;
	}

	/* Returns a string for GUI display */
	String getPlayerInfoString() {
		String playerInfoString = "";
		for (int i = 0; i < P_NUM; i++) {
			playerInfoString += "Player " + i + " : " + "  logon: "
					+ playerList[i][0] + "  Score: " + playerList[i][1]
							+ "  keysHeld: " + playerList[i][2] + "  Location: "
							+ playerLocation[i] + "\n";
		}
		return playerInfoString;
	}

	/* Returns a string for GUI display */
	String getTreasureInfoString() {
		String getTreasureInfoString = "";
		for (int i = 0; i < N_NUM; i++) {
			getTreasureInfoString += treasureList[i] + "   ";
			if ((i + 1) % COLUMN == 0)
				getTreasureInfoString += "\n";
		}
		return getTreasureInfoString;
	}

	int getGlobalEventStatus(){
		return globalEventStatus;
	}

	/*
	 * global event scheduling classes
	 * 
	 * /
	 * 
	 * /* pre-game countdown to start of actual game
	 */
	class CountdownTask extends TimerTask {
		public void run() {
			currentPreGameTime -= 1;
			System.out.println("Pre-game countdown Time: " + currentPreGameTime);

			if( currentPreGameTime == 0){
				System.out.println("Pre-game countdown Time's up!");
				System.out.println("Game starts now!");
				globalEventStatus = 2;
				System.out.println("In-gamecountdown time: " + currentInGameTime);
				timer.schedule(new EndofGameTask(), 1000);
				timer.schedule(new FallingCoinStartTask(),
						beforeFallingCoinsDurations * 1000);
			}
			else { 	
				timer.schedule(new CountdownTask(), 1000);
			}
		}
	}

	/* Start of falling coin event */
	class FallingCoinStartTask extends TimerTask {
		public void run() {
			System.out.println("Falling Coins starts now!");
			globalEventStatus = 3;
			timer.schedule(new FallingCoinEndTask(),
					fallingCoinsDurations * 1000);
		}
	}

	/* End of falling coin event, back to normal treasure hunting */
	class FallingCoinEndTask extends TimerTask {
		public void run() {
			System.out.println("Falling Coins ends now!");
			globalEventStatus = 4;
		}
	}

	/* End of whole game */
	class EndofGameTask extends TimerTask {		
		public void run() {
			currentInGameTime -= 1;
			System.out.println("In-gamecountdown time: " + currentInGameTime);

			if( currentInGameTime == 0){
				System.out.println("In-game countdown Time's up!");
				System.out.println("End of game!");
				System.out.println("Rebooting GameServerGui in 5 seconds!");
				globalEventStatus = 5;
				timer.schedule(new GameServerRebootTask(),
						rebootDurations * 1000);
			}
			else { 	
				timer.schedule(new EndofGameTask(), 1000);
			}
		}
		
	}

	class GameServerRebootTask extends TimerTask {
		public void run() {
			System.out.println("Rebooting gameserver!");
			globalEventStatus = 6;
			timer.cancel();
			// System.exit(0); //Stops the AWT thread (and everything else)
		}
	}

}
