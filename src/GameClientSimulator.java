

/* CS3283/CS3284 Project
 * 
 * Client simulator
 * 
 */
public class GameClientSimulator {

	/**
	 * @param args
	 */
	private static  GameClient client;

	public static void main(String[] args) throws Exception {

		client = new GameClient();

		// periodic mapUpdate tests
		new Thread(new Runnable() 
		{ 
			public void run() 
			{  
					while(true){ 
						try {
							client.mapUpdateEvent(0);
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						try{Thread.sleep(1000);} // 1 second pause 
						catch(Exception e){} 
					}
			} 
		}).start();
		
		// Login test
		//System.out.print(client.loginEvent(99));
		//System.out.print(client.loginEvent(0));
		//System.out.print(client.loginEvent(0));
		//System.out.print(client.scoreUpdateEvent(0,9));
	
		//		client.addKeyEvent(0,1111);
		//		client.addKeyEvent(0,2527);
		//		client.getRankingOfGivenPlayer(0);
		//		System.out.print("ppp"+client.addKeyEvent(0,7278));
	}
}

