package client;

// Client simulator

public class ClientTest {

	/**
	 * @param args
	 */
	private static  GameClient client;

	public static void main(String[] args) throws Exception {
		
		client = new GameClient();
		
		Thread thread = new Thread();
		thread.start();
		// Login test
		//System.out.print(client.loginEvent(99));
		//System.out.print(client.loginEvent(0));
		System.out.print(client.loginEvent(0));
		//System.out.print(client.scoreUpdateEvent(0,9));
		// periodic mapUpdate tests
		//client.addKeyEvent(0,1111);
		//client.addKeyEvent(0,6103);
		//client.getRankingOfGivenPlayer(0);
		while(true) {
			try {
	    	Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}           
			client.mapUpdateEvent(0);
			client.openTreasureEvent(0);
			int[][] treasureList2D = client.getTreasureList2D();
			for(int i = 0; i< 3; i++)
				for(int j = 0;j< 3; j++)
				System.out.print(treasureList2D[i][j]);
			
	    }
		
	/*	System.out.print(client.getPlayerX(0));
		System.out.println(client.getPlayerY(0));
		
		System.out.print(client.getPlayerX(1));
		System.out.println(client.getPlayerY(1));
		
		for( int i=0;i<9;i++)
		System.out.print(client.getTreasureList()[i]);*/
	}

}

