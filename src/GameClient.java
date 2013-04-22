/* CS3283/CS3284 Project
 * 
 * Game UDP Client: communicates to with server through UDP connections to get informations
 * 
 */

import java.util.*;
import java.io.IOException;
import java.net.*;

public class GameClient {

	final int ROW = 7; // Number of rows of Nodes
	final int COLUMN = 14; // Number of column of Nodes

	final int N_NUM = ROW * COLUMN; // Total number of Nodes
	final int T_NUM = N_NUM / 3; // Number of treasures on the map
	final int P_NUM = 5; // Number of players
	final int K_NUM = N_NUM; // Number of key codes

	// Event code
	final int invalidEvent = 0;
	final int loginEvent = 1;
	final int mapUpdateEvent = 2;
	final int openTreasureEvent = 3;
	final int scoreUpdateEvent = 4;
	final int addKeyCodeEvent = 5;

	// Various event durations (seconds)
	private int totalcountdownDurations = 10;
	private int totalMiniGameDurations = 30;
	private int totalGameDurations = 150;
	private int currentPreGameTime = totalcountdownDurations;
	private int currentInGameTime = totalGameDurations;
	private int currentMiniGameTime = totalMiniGameDurations;
	//private int rebootDurations = 5;

	Random randomGenerator;

	// Stores when player informations, add player when they connects to the
	// server for the first time
	// there is no playerID stored, the index of the array is equals to playerID
	// Currently stored values are: logged on?, playerScore and number of keys

	private int[][] playerList;

	private int[] treasureList; // stores treasure location
	// globalEvent code: Stores the current global game status
	// 0 = pre-game
	// 1 = countdown stage, once the first player logon to the server
	// 2 = game starts
	// 3 = falling coins starts
	// 4 = falling coins ends
	// 5 = game end
	// 6 = server rebooting, Android gameclient DO NOT need to care about this
	private int globalEventStatus;

	private int[] playerLocation;

	// Client socket variables
	/*
	 * How to get IP on VM linux lackern@lackern-VirtualBox:~$ ifconfig eth0
	 * Link encap:Ethernet HWaddr 08:00:27:98:0c:c7 inet addr:192.168.1.29
	 * Bcast:192.168.1.255 Mask:255.255.255.0
	 */

	final int port = 9001;
	final int timeOutDuration = 500;
	//final String gameServerAddress = "localhost";
	final String gameServerAddress = "1.1.1.1";
	//final String gameServerAddress = "10.0.2.2";
	private DatagramSocket socket;
	InetAddress inetAddress;


	/* Constructor */
	public GameClient() throws Exception {
		randomGenerator = new Random();
		initializeSocket();
		initializeTreasureList();
		initializePlayerList();
		initializePlayerLocation();
		globalEventStatus = 0;
	}

	/* Various initializations */

	private void initializeSocket() throws Exception {
		socket = new DatagramSocket();
		socket.setSoTimeout(timeOutDuration);
		inetAddress = InetAddress.getByName(gameServerAddress);
	}

	public void getGameServerIP() throws IOException{
		// Receive port 8000
		MulticastSocket mSocket = new MulticastSocket(8000);
		InetAddress groupAddress = InetAddress.getByName("234.235.236.237");
		mSocket.joinGroup(groupAddress);

		DatagramPacket packet;
		byte[] buf = new byte[256];
		packet = new DatagramPacket(buf, buf.length);
		mSocket.receive(packet);

		inetAddress = packet.getAddress();
		System.out.println("packet rc: " + new String(packet.getData()));
		System.out.println("packet rc: " + inetAddress);
		
		mSocket.leaveGroup(groupAddress);
		mSocket.close();
	}


	private void initializeTreasureList() {
		treasureList = new int[N_NUM];
		for (int i = 0; i < N_NUM; i++)
			treasureList[i] = 0;
	}

	private void initializePlayerList() {
		playerList = new int[P_NUM][3];
		for (int i = 0; i < P_NUM; i++) {
			playerList[i][0] = 0;
			playerList[i][1] = 0;
			playerList[i][2] = 0;
		}
	}

	private void initializePlayerLocation() {
		playerLocation = new int[P_NUM];
		for (int i = 0; i < P_NUM; i++)
			playerLocation[i] = 0;
	}

	/* Game Server's loginEvent reply format: Failure or Successful or AlreadyLogon */
	public String loginEvent(int playerID) throws Exception {
		getGameServerIP();
		/*
		 * Use DataGramSocket for UDP connection convert string "request" to
		 * array of bytes, suitable for creation of DatagramPacket
		 */
		String request = loginEvent + ";" + playerID;

		/* Start of UDP protocol */
		byte outgoingBuffer[] = request.getBytes();

		// Now create a packet (with destination inetAddress and port)
		DatagramPacket outgoingPacket = new DatagramPacket(outgoingBuffer,
				outgoingBuffer.length, inetAddress, port);

		// sends request to game server
		socket.send(outgoingPacket);

		// create a packet buffer to store data from packets received.
		byte[] incomingBuffer = new byte[1000];
		DatagramPacket incomingPacket = new DatagramPacket(incomingBuffer,
				incomingBuffer.length);

		// receive the reply from server.
		socket.receive(incomingPacket);

		// convert reply to string and print to System.out
		String reply = new String(incomingPacket.getData(), 0,
				incomingPacket.getLength());
		System.out.println(reply + " [loginEvent: GameClient.java]");
		/* End of UDP protocol */

		return reply;
	}


	/* Primary event to be called by android game client at a fixed interval */
	/*
	 * Return player's game info upon request p1 logon status, p1 score, p1
	 * keys, p1 location, p2 ... pP_NUM, treasure0,1,2,3 ... N_NUM, globalEventStatus, currentPreGameTime, currentInGameTime, currentMiniGameTime
	 */
	public void mapUpdateEvent(int playerID) throws Exception {
		getGameServerIP();
		/*
		 * Use DataGramSocket for UDP connection convert string "request" to
		 * array of bytes, suitable for creation of DatagramPacket
		 */
		String request = mapUpdateEvent + ";" + playerID;

		/* Start of protocol */
		byte outgoingBuffer[] = request.getBytes();

		// Now create a packet (with destination inetAddress and port)
		DatagramPacket outgoingPacket = new DatagramPacket(outgoingBuffer,
				outgoingBuffer.length, inetAddress, port);

		// Sends request to game server
		socket.send(outgoingPacket);

		// Create a packet buffer to store data from packets received.
		byte[] incomingBuffer = new byte[1000];
		DatagramPacket incomingPacket = new DatagramPacket(incomingBuffer,
				incomingBuffer.length);

		// Receive the reply from server.
		socket.receive(incomingPacket);

		// Convert reply to string and print to System.out
		String reply = new String(incomingPacket.getData(), 0,
				incomingPacket.getLength());
		System.out.println(reply + " [mapUpdateEvent: GameClient.java]");

		// Tokenize the reply string
		StringTokenizer requestToken;
		requestToken = new StringTokenizer(reply, ";");
		/* End of protocol */

		// Game Server's mapUpdateEvent reply format:
		// p1 logon status, p1 score, p1 keys, p1 location, p2 ... pP_NUM,
		// treasure0,1,2,3 ... N_NUM, global event status

		// Update playerList and playerLocaiton array
		for (int i = 0; i < P_NUM; i++) {
			playerList[i][0] = Integer.parseInt(requestToken.nextToken());
			playerList[i][1] = Integer.parseInt(requestToken.nextToken());
			playerList[i][2] = Integer.parseInt(requestToken.nextToken());
			playerLocation[i] = Integer.parseInt(requestToken.nextToken());
		}

		// Update treasureList array
		for (int i = 0; i < N_NUM; i++)
			treasureList[i] = Integer.parseInt(requestToken.nextToken());

		// Update global event
		globalEventStatus = Integer.parseInt(requestToken.nextToken());
		currentPreGameTime = Integer.parseInt(requestToken.nextToken());
		currentInGameTime = Integer.parseInt(requestToken.nextToken());
		currentMiniGameTime = Integer.parseInt(requestToken.nextToken());
	}

	/* Game Server's openTreasureEvent reply format: NoChest, NoKey or Successful */
	public String openTreasureEvent(int playerID) throws Exception {
		getGameServerIP();
		/*
		 * Use DataGramSocket for UDP connection convert string "request" to
		 * array of bytes, suitable for creation of DatagramPacket
		 */
		String request = openTreasureEvent + ";" + playerID + ";"
				+ playerLocation[playerID];

		/* Start of UDP protocol */
		byte outgoingBuffer[] = request.getBytes();

		// Now create a packet (with destination inetAddress and port)
		DatagramPacket outgoingPacket = new DatagramPacket(outgoingBuffer,
				outgoingBuffer.length, inetAddress, port);

		// sends request to game server
		socket.send(outgoingPacket);

		// create a packet buffer to store data from packets received.
		byte[] incomingBuffer = new byte[1000];
		DatagramPacket incomingPacket = new DatagramPacket(incomingBuffer,
				incomingBuffer.length);

		// receive the reply from server.
		socket.receive(incomingPacket);

		// convert reply to string and print to System.out
		String reply = new String(incomingPacket.getData(), 0,
				incomingPacket.getLength());
		System.out.println(reply + " [openTreasureEvent: GameClient.java]");
		/* End of UDP protocol */

		return reply;
	}

	/* Game Server's scoreUpdateEvent reply format: Failure or Successful */
	public String scoreUpdateEvent(int playerID, int newScore) throws Exception {
		getGameServerIP();
		/*
		 * Use DataGramSocket for UDP connection convert string "request" to
		 * array of bytes, suitable for creation of DatagramPacket
		 */
		String request = scoreUpdateEvent + ";" + playerID + ";" + newScore;

		/* Start of UDP protocol */
		byte outgoingBuffer[] = request.getBytes();

		// Now create a packet (with destination inetAddress and port)
		DatagramPacket outgoingPacket = new DatagramPacket(outgoingBuffer,
				outgoingBuffer.length, inetAddress, port);

		// sends request to game server
		socket.send(outgoingPacket);

		// create a packet buffer to store data from packets received.
		byte[] incomingBuffer = new byte[1000];
		DatagramPacket incomingPacket = new DatagramPacket(incomingBuffer,
				incomingBuffer.length);

		// receive the reply from server.
		socket.receive(incomingPacket);

		// convert reply to string and print to System.out
		String reply = new String(incomingPacket.getData(), 0,
				incomingPacket.getLength());
		System.out.println(reply + " [scoreUpdateEvent: GameClient.java]");
		/* End of UDP protocol */

		return reply;
	}

	/* Game Server's addKeyCodeEvent reply format: InvalidKeyCode, InvalidLocation, KeyCodeUsed, Successful */
	public String addKeyCodeEvent(int playerID, int keyCode) throws Exception {
		getGameServerIP();
		/*
		 * Use DataGramSocket for UDP connection convert string "request" to
		 * array of bytes, suitable for creation of DatagramPacket
		 */
		String request = addKeyCodeEvent + ";" + playerID + ";" + keyCode;

		/* Start of UDP protocol */
		byte outgoingBuffer[] = request.getBytes();

		// Now create a packet (with destination inetAddress and port)
		DatagramPacket outgoingPacket = new DatagramPacket(outgoingBuffer,
				outgoingBuffer.length, inetAddress, port);

		// sends request to game server
		socket.send(outgoingPacket);

		// create a packet buffer to store data from packets received.
		byte[] incomingBuffer = new byte[1000];
		DatagramPacket incomingPacket = new DatagramPacket(incomingBuffer,
				incomingBuffer.length);

		// receive the reply from server.
		socket.receive(incomingPacket);

		// convert reply to string and print to System.out
		String reply = new String(incomingPacket.getData(), 0,
				incomingPacket.getLength());
		System.out.println(reply + " [addKeyEvent: GameClient.java]");
		/* End of UDP protocol */

		return reply;
	}

	/* Returns the X position of a playerID */
	public int getPlayerX(int playerID) {
		int x = playerLocation[playerID] / COLUMN;
		return x;
	}

	/* Returns the Y position of a playerID */
	public int getPlayerY(int playerID) {
		int y = playerLocation[playerID] % COLUMN;
		return y;
	}

	/* Returns the Y position of a playerID */
	public int getPlayerLocation(int playerID) {
		int y = playerLocation[playerID];
		return y;
	}

	/* Returns a 1D array of treasure info */
	public int[] getTreasureList() {
		return treasureList;
	}

	/* Returns a 2D array of treasure info */
	public int[][] getTreasureList2D() {
		int[][] treasureList2D = new int[ROW][COLUMN];
		for (int i = 0; i < N_NUM; i++)
			treasureList2D[i / COLUMN][i % COLUMN] = treasureList[i];
		return treasureList2D;
	}

	/* Returns the logon status of playerID */
	public boolean getLogonStatus(int playerID) {
		return playerList[playerID][0] == 1;
	}

	/* Returns current score of a playerID */
	public int getPlayerScore(int playerID) {
		return playerList[playerID][1];
	}

	/* Returns the numbers of key held by a player */
	public int getPlayerKeyNum(int playerID) {
		return playerList[playerID][2];
	}

	/* Returns player's ranking, example input Ranking (1,2,3,4), output: PlayerID (0,1,2,3) */
	public int getPlayerOfGivenRanking(int ranking) {
		int[] scoreList = new int[P_NUM];
		int[] tempList = new int[P_NUM];
		System.out.println("rr: " + ranking);
		// copy out the new score for sorting
		for (int i = 0; i < P_NUM; i++) {
			scoreList[i] = playerList[i][1];
			tempList[i] = i + 1;
		}

		// Bubblesort scoreList
		int n = P_NUM;
		while (n != 0) {
			int newn = 0;
			for (int i = 1; i < n; i++)
				if (scoreList[i - 1] < scoreList[i]) {
					int temp = scoreList[i - 1];
					scoreList[i - 1] = scoreList[i];
					scoreList[i] = temp;

					int temp2 = tempList[i - 1];
					tempList[i - 1] = tempList[i];
					tempList[i] = temp2;
					newn = i;
				}
			n = newn;
		}

		return tempList[ranking - 1];
	}

	/* Returns player's ranking, example input PlayerID (0,1,2,3), output: Ranking (1,2,3,4) */
	public int getRankingOfGivenPlayer(int PlayerID) {
		int[] scoreList = new int[P_NUM];
		int[] tempList = new int[P_NUM];

		// copy out the new score for sorting
		for (int i = 0; i < P_NUM; i++) {
			scoreList[i] = playerList[i][1];
			tempList[i] = i + 1;
		}

		// Bubblesort scoreList
		int n = P_NUM;
		while (n != 0) {
			int newn = 0;
			for (int i = 1; i < n; i++)
				if (scoreList[i - 1] < scoreList[i]) {
					int temp = scoreList[i - 1];
					scoreList[i - 1] = scoreList[i];
					scoreList[i] = temp;

					int temp2 = tempList[i - 1];
					tempList[i - 1] = tempList[i];
					tempList[i] = temp2;
					newn = i;
				}
			n = newn;
		}

		int[] tempList2 = new int[P_NUM];

		for (int i = 0; i < P_NUM; i++)
			tempList2[tempList[i] - 1] = i + 1;

		return tempList2[PlayerID];
	}

	/* Returns the global event status of the game */
	public int getGlobalEventStatus() {
		return globalEventStatus;
	}

	/* Returns the current pre-game time of the game */
	public int getCurrentPreGameTime(){
		return currentPreGameTime;
	}

	/* Returns the current  in game time of the game */
	public int getCurrentInGameTime(){
		return currentInGameTime;
	}

	/* Returns the current  mini game time of the game */
	public int getCurrentMiniGameTime(){
		return currentMiniGameTime;
	}

	/* Returns the number of players that are currently logon to the game */
	public int getNumPlayerLogon(){
		int counter = 0;
		for(int i = 0; i < P_NUM; i++){
			counter += playerList[i][0];
		}

		return counter;
	}

	/* Returns the total number of players supported by the game server */
	public int getTotalNumOfPlayersSupported(){
		return P_NUM-1;
	}
	
	/* Returns boolean if a player is currently logon to the game */
	public boolean checkPlayerLogonStatus(int playerID){
		if(playerList[playerID][0] == 1)
			return true;
		else return false;
	}

	/* Closes the UDP socket */
	public void closeSocket() {
		socket.close();
	}

}