

import net.tinyos.message.*;
import net.tinyos.util.*;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;

public class TinyOsLoader implements MessageListener
{
	private int P_NUM;		// Number of players

	private int[] locationList;

	private MoteIF mote;

	//private LinkedList<Integer> queue;
	private ArrayList<LinkedList<Integer>> queueList;
	private final int[][] GRID = {
			{93,94,107},
			{100,97},
			{109,98,0},


			{91,92,0},
			{88,87,89},
			{99,108,0},

			{112,104,0},
			{90,0,0},
			{101,110,0}};

	//			{93,94,0,107},
	//			{100,98,88,0},
	//			{109,108,97,0},
	//			
	//			{0,89,87,0},
	//			{88,97,91,0},
	//			{110,108,99,0},
	//			
	//			{112,104,0,0},
	//			{89,90,101,0},
	//			{101,110,0,0}};

	// Constructor
	public TinyOsLoader(int p_input) {
		P_NUM = p_input;

		queueList = new ArrayList<LinkedList<Integer>>();
		for(int i = 0; i< P_NUM; i++){
			queueList.add(new LinkedList<Integer>());
			queueList.get(i).add(0);
		}

		locationList = new int[P_NUM];

		// initialize all to 0 first
		for( int i = 0; i < P_NUM; i++)
			locationList[i] = 0; 

		mote = new MoteIF(PrintStreamMessenger.err);
		mote.registerListener(new MapMsg(), this);

		System.out.println("TinyOs info initiatised ... [TinyOsLoader.java]");
	}

	public synchronized void messageReceived(int dest_addr, Message msg)
	{
		if (msg instanceof MapMsg)
		{
			MapMsg mmsg = (MapMsg)msg;

			// Print message.
			//locationList[mmsg.get_player_id()] = mmsg.get_location_id();

			addNewElement(mmsg.get_location_id(),mmsg.get_player_id());

			// Print
			//for(int i =0; i<P_NUM; i++ ){

			//System.out.println("Player: " + i);
			//System.out.println("queue:");
			//for(int j =0; j<queueList.get(i).size(); j++ ){
			//for(int j =0; j<queueList.get(1).size(); j++ ){
			//System.out.println(queueList.get(i).get(j));
			//System.out.println(queueList.get(1).get(j));

			//}
			//}

			checkGrid(mmsg.get_player_id());
			//System.out.println(locationList[1]);

			System.out.println("location_id: " + mmsg.get_location_id() + " [TinyOsLoader.java]");
			//System.out.println("player_id: " + mmsg.get_player_id()+ " [TinyOsLoader.java]");
		}
	}

	public void checkGrid(int playerNum){
		boolean isSubsetOfQueue;

		for(int i = 0; i <GRID.length; i++){
			//isSubsetOfQueue = true;
			for(int j = 0; j <GRID[i].length; j++){

				if(GRID[i][j]>0)
					if(isInQueue(GRID[i][j], playerNum))
						locationList[playerNum] = i/3 * 14 + 34 + i%3;

				//					if(!isInQueue(GRID[i][j], playerNum))
				////						isSubsetOfQueue = false;
			}
			//			if (isSubsetOfQueue)
			//				locationList[playerNum] = i/3 * 14 + 34 + i%3;
		}
	}

	public void addNewElement(int newElement,int playerNum) {
		if(!isInQueue(newElement, playerNum)){
			//queueList.get(playerNum).removeFirst();
			//queueList.get(playerNum).add(newElement);
			queueList.get(playerNum).set(0, newElement);
		}
	}

	public boolean isInQueue(int newElement, int playerNum) {
		if(queueList.get(playerNum).indexOf(newElement) > -1)
			return true;
		return false;
	}

	public int getPlayerLocation(int playerID) {
		return locationList[playerID];
	}

}