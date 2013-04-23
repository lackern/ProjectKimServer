/* CS3283/CS3284 Project
 * 
 * GameClient simulator
 * 
 */
public class GameClientSimulator {

	/**
	 * @param args
	 */
	private static  GameClient client;

	public static void main(String[] args) throws Exception {

		client = new GameClient();

		/* ******************* */
		/* UDP Network related */
		/* ******************* */

		/* mapUpdate tests */
		//client.mapUpdateEvent(0);

		/* Login test */
		//System.out.println(client.loginEvent(99));
		System.out.println(client.loginEvent(0));
		//		System.out.println(client.loginEvent(0));
		//
		/* add key event test */
		System.out.println(client.addKeyCodeEvent(0,1111));
		System.out.println(client.addKeyCodeEvent(0,2527));

		/* open treasure chest test */
		System.out.println(client.openTreasureEvent(0));

		/* score update test */
		System.out.println(client.scoreUpdateEvent(0,16));

		/* *********************** */
		/* NON-UDP Network related */
		/* *********************** */
		/* get ranking test test */
		System.out.println(client.getRankingOfGivenPlayer(0));

		/* get ranking test test */
		System.out.println(client.getGlobalEventStatus());

		/* ************************ */
		/* periodic mapUpdate tests */
		/* ************************ */
		while(true){
			client.mapUpdateEvent(0);
			Thread.sleep(500);
		}

	}
}

