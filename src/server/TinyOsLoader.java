package server;
/* CS3283 Project
 * 
 * TinyOs: retrieve results from serial forwarder
 * 
 */

import net.tinyos.message.*;
import net.tinyos.util.*;
import java.io.*;

public class TinyOsLoader implements MessageListener
{
	private int P_NUM;		// Number of players
	
	private int[] locationList;

	private MoteIF mote;
	
	// Constructor
	public TinyOsLoader(int p_input) {
		
		P_NUM = p_input;
		
		locationList = new int[P_NUM];
		
		// initialize all to -1 first
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
            locationList[mmsg.get_player_id()] = mmsg.get_location_id();
            
            //System.out.println("location_id: " + mmsg.get_location_id() + "[TinyOsLoader.java]");
            //System.out.println("player_id: " + mmsg.get_player_id()+ "[TinyOsLoader.java]");
        }
    }

	public int getPlayerLocation(int playerID) {
		
		return locationList[playerID];
	}

}