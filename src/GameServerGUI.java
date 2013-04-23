/* CS3283/CS3284 Project
 * 
 * Game Server GUI: simple graphical user interface for the game server
 * 
 */

import java.awt.EventQueue;
import javax.swing.JFrame;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.swing.JTextArea;
import java.awt.Insets;
import java.awt.Color;
import javax.swing.JScrollPane;
import javax.swing.text.DefaultCaret;
import javax.swing.JButton;
import javax.swing.border.TitledBorder;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JTabbedPane;

public class GameServerGUI {

	private long startTime = System.currentTimeMillis();
	private boolean serverIsOn = false;

	private JFrame frame;
	private GameServer gameServer;
	private GameServerCasting gameServerCasting;
	private GameServerThread gameServerThread;
	private GameServerCastingThread gameServerCastingThread;
	private GameServerRebootThread gameServerRebootThread;

	JButton startButton = new JButton("Start Server");
	JButton closeButton = new JButton("Close Server");

	JTextArea JConsole = new JTextArea();
	JTextArea JKeyCode1 = new JTextArea();
	JTextArea JKeyCode2 = new JTextArea();
	JTextArea JKeyCode3 = new JTextArea();
	JTextArea JKeyCode4 = new JTextArea();
	JTextArea JPlayer = new JTextArea();
	JTextArea JTreasure = new JTextArea();
	JTextArea JTimerInfo = new JTextArea();
	JPanel panelTreasureList = new JPanel();
	JPanel panelPlayerInfo = new JPanel();
	JPanel panelControls = new JPanel();

	JButton btnMoveUp = new JButton("^");
	JButton btnMoveDown = new JButton("v");
	JButton btnMoveLeft = new JButton("<");
	JButton btnMoveRight = new JButton(">");
	private JTextField textFieldPlayerID;



	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					System.out.println("Starting ProjectKim Server GUI");
					GameServerGUI window = new GameServerGUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * @throws Exception 
	 */
	public GameServerGUI() throws Exception {
		initialize();
		gameServerCastingThread = new GameServerCastingThread();
		gameServerCastingThread.start();
		gameServerRebootThread = new GameServerRebootThread();
		gameServerRebootThread.start();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame("Project Kim Server");
		frame.setResizable(false);
		frame.getContentPane().setBackground(Color.LIGHT_GRAY);
		frame.setBounds(100, 100, 600, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JScrollPane JConsoleScrollPane = new JScrollPane();
		JConsoleScrollPane.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Console", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		JConsoleScrollPane.setBounds(10, 341, 429, 217);

		DefaultCaret caret = (DefaultCaret)JConsole.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		JConsoleScrollPane.setBackground(Color.LIGHT_GRAY);
		frame.getContentPane().add(JConsoleScrollPane);

		JConsoleScrollPane.setViewportView(JConsole);
		JConsole.setMargin(new Insets(5, 5, 5, 5));
		JConsole.setRows(5);
		JConsole.setEditable(false);
		JConsole.setText("Press Start to continue...");
		panelTreasureList.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panelTreasureList.setBounds(10, 124, 233, 161);

		frame.getContentPane().add(panelTreasureList);
		panelTreasureList.setLayout(new BorderLayout(0, 0));
		JTreasure.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Treasure List", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelTreasureList.add(JTreasure, BorderLayout.CENTER);
		JTreasure.setEditable(false);
		JTreasure.setMargin(new Insets(5, 5, 5, 5));
		JTreasure.setText("Displays treasure chests\nlocated at each Node\n0: no treasure chest\n1: has treasure chest\nX: Out of Bound");
		panelPlayerInfo.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panelPlayerInfo.setBounds(253, 124, 331, 100);

		frame.getContentPane().add(panelPlayerInfo);
		panelPlayerInfo.setLayout(new BorderLayout(0, 0));
		JPlayer.setBorder(new TitledBorder(null, "Player Infomation", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelPlayerInfo.add(JPlayer, BorderLayout.CENTER);

		JPlayer.setEditable(false);		
		JPlayer.setMargin(new Insets(5, 5, 5, 5));
		JPlayer.setText("Display infomation of all players in the game\n" +"Example:\n"
				+"Player 1:   logon: ?  Score: ?  keys: ?  Location: ?\n");
		panelControls.setBackground(Color.LIGHT_GRAY);
		panelControls.setBorder(new TitledBorder(null, "Controls", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelControls.setBounds(449, 341, 135, 217);

		frame.getContentPane().add(panelControls);
		btnMoveUp.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnMoveUp.setBounds(52, 89, 30, 30);
		btnMoveUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int playerID = Integer.parseInt(textFieldPlayerID.getText());
				if(playerID >= 0 && playerID <=3 )
					gameServer.movePlayer(playerID,0);
			}
		});
		panelControls.setLayout(null);
		panelControls.add(btnMoveUp);
		btnMoveUp.setMargin(new Insets(2, 2, 2, 2));
		btnMoveUp.setEnabled(false);
		btnMoveDown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int playerID = Integer.parseInt(textFieldPlayerID.getText());
				if(playerID >= 0 && playerID <=3 )
					gameServer.movePlayer(playerID,1);
			}
		});
		btnMoveDown.setBounds(52, 171, 30, 30);
		panelControls.add(btnMoveDown);
		btnMoveDown.setMargin(new Insets(2, 2, 2, 2));
		btnMoveDown.setEnabled(false);
		btnMoveLeft.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int playerID = Integer.parseInt(textFieldPlayerID.getText());
				if(playerID >= 0 && playerID <=3 )
					gameServer.movePlayer(playerID,2);
			}
		});
		btnMoveLeft.setBounds(10, 130, 30, 30);
		panelControls.add(btnMoveLeft);
		btnMoveLeft.setMargin(new Insets(2, 2, 2, 2));
		btnMoveLeft.setEnabled(false);
		btnMoveRight.setBounds(95, 130, 30, 30);
		panelControls.add(btnMoveRight);

		btnMoveRight.setMargin(new Insets(2, 2, 2, 2));
		btnMoveRight.setEnabled(false);
		closeButton.setBounds(10, 55, 115, 23);
		panelControls.add(closeButton);
		closeButton.setMargin(new Insets(2, 5, 2, 5));
		closeButton.setEnabled(false);
		startButton.setBounds(10, 21, 115, 23);
		panelControls.add(startButton);
		startButton.setMargin(new Insets(2, 5, 2, 5));

		textFieldPlayerID = new JTextField();
		textFieldPlayerID.setText("1");
		textFieldPlayerID.setEnabled(false);
		textFieldPlayerID.setMargin(new Insets(5, 9, 5, 9));
		textFieldPlayerID.setBounds(52, 130, 30, 30);
		panelControls.add(textFieldPlayerID);
		textFieldPlayerID.setColumns(10);

		JTabbedPane tabbedPaneKeyCode = new JTabbedPane(JTabbedPane.TOP);
		tabbedPaneKeyCode.setBounds(10, 5, 574, 108);
		frame.getContentPane().add(tabbedPaneKeyCode);

		tabbedPaneKeyCode.addTab("00-28", null, JKeyCode1, null);
		JKeyCode1.setText("Displays all KeyCodes paired with their respective Node\r\nKeyCode: 1234    Node: 16\tKeyCode pair: 1234[16]\r\nEach KeyCode can only be use once by each player");
		JKeyCode1.setMargin(new Insets(5, 5, 5, 5));
		JKeyCode1.setLineWrap(true);
		JKeyCode1.setEditable(false);
		JKeyCode1.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Key Code pairing list", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		tabbedPaneKeyCode.addTab("31-66", null, JKeyCode2, null);
		JKeyCode2.setText("Displays all KeyCodes paired with their respective Node\r\nKeyCode: 1234    Node: 16\tKeyCode pair: 1234[16]\r\nEach KeyCode can only be use once by each player");
		JKeyCode2.setMargin(new Insets(5, 5, 5, 5));
		JKeyCode2.setLineWrap(true);
		JKeyCode2.setEditable(false);
		JKeyCode2.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Key Code pairing list", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		tabbedPaneKeyCode.addTab("68-92", null, JKeyCode3, null);
		JKeyCode3.setText("Displays all KeyCodes paired with their respective Node\r\nKeyCode: 1234    Node: 16\tKeyCode pair: 1234[16]\r\nEach KeyCode can only be use once by each player");
		JKeyCode3.setMargin(new Insets(5, 5, 5, 5));
		JKeyCode3.setLineWrap(true);
		JKeyCode3.setEditable(false);
		JKeyCode3.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Key Code pairing list", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		tabbedPaneKeyCode.addTab("93-97", null, JKeyCode4, null);
		JKeyCode4.setText("Displays all KeyCodes paired with their respective Node\r\nKeyCode: 1234    Node: 16\tKeyCode pair: 1234[16]\r\nEach KeyCode can only be use once by each player");
		JKeyCode4.setMargin(new Insets(5, 5, 5, 5));
		JKeyCode4.setLineWrap(true);
		JKeyCode4.setEditable(false);
		JKeyCode4.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Key Code pairing list", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Game Information", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(253, 235, 331, 100);
		frame.getContentPane().add(panel);
		panel.setLayout(new BorderLayout(0, 0));
		panel.add(JTimerInfo, BorderLayout.NORTH);
		JTimerInfo.setMargin(new Insets(2, 5, 2, 5));
		JTimerInfo.setEditable(false);
		JTimerInfo.setText("Displays current game status\r\ncurrentPreGameTime =\r\ncurrentInGameTime =\r\ncurrentMiniGameTime =");

		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					serverIsOn=true;
					startButton.setEnabled(false);
					closeButton.setEnabled(true);
					btnMoveUp.setEnabled(true);
					btnMoveDown.setEnabled(true);
					btnMoveLeft.setEnabled(true);
					btnMoveRight.setEnabled(true);
					textFieldPlayerID.setEnabled(true);
					gameServerThread = new GameServerThread();
					gameServerThread.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					serverIsOn = false;
					//connectThread.stop();
					startButton.setEnabled(true);
					closeButton.setEnabled(false);
					btnMoveUp.setEnabled(false);
					btnMoveDown.setEnabled(false);
					btnMoveLeft.setEnabled(false);
					btnMoveRight.setEnabled(false);
					textFieldPlayerID.setEnabled(false);

					JKeyCode1.setText("Displays all KeyCodes paired with their respective Node\r\nKeyCode: 1234    Node: 16\tKeyCode pair: 1234[16]\r\nEach KeyCode can only be use once by each player");
					JKeyCode2.setText("Displays all KeyCodes paired with their respective Node\r\nKeyCode: 1234    Node: 16\tKeyCode pair: 1234[16]\r\nEach KeyCode can only be use once by each player");
					JKeyCode3.setText("Displays all KeyCodes paired with their respective Node\r\nKeyCode: 1234    Node: 16\tKeyCode pair: 1234[16]\r\nEach KeyCode can only be use once by each player");
					JKeyCode4.setText("Displays all KeyCodes paired with their respective Node\r\nKeyCode: 1234    Node: 16\tKeyCode pair: 1234[16]\r\nEach KeyCode can only be use once by each player");

					JTreasure.setText("Displays treasure chests\nlocated at each Node\n0: no treasure chest\n1: has treasure chest\nX: Out of Bound");
					JPlayer.setText("Display infomation of all players in the game\n" +"Example:\n"
							+"Player 1:   logon: ?  Score: ?  keys: ?  Location: ?\n");

					JTimerInfo.setText("Displays current game status\r\ncurrentPreGameTime =\r\ncurrentInGameTime =\r\ncurrentMiniGameTime =");
					gameServer.disconnect();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		btnMoveRight.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int playerID = Integer.parseInt(textFieldPlayerID.getText());
				if(playerID >= 0 && playerID <=3 )
					gameServer.movePlayer(playerID,3);
			}
		});
	}


	class GameServerThread extends Thread {

		public void run() {
			System.out.println("GUI initiatised!");
			try {
				gameServer = new GameServer();
				JKeyCode1.setText(gameServer.getKeyCodeInfoString().substring(0,302));
				JKeyCode2.setText(gameServer.getKeyCodeInfoString().substring(303,605));
				JKeyCode3.setText(gameServer.getKeyCodeInfoString().substring(606, 908));
				int length = gameServer.getKeyCodeInfoString().length();
				JKeyCode4.setText(gameServer.getKeyCodeInfoString().substring(909,length));
				gameServer.connect();

			} catch (Exception e) {
				// 
				e.printStackTrace();
			}

		}
	}

	class GameServerCastingThread extends Thread {

		public void run() {
			System.out.println("Game initiatised!");
			try {
				gameServerCasting = new GameServerCasting();
				gameServerCasting.startCasting();
			} catch (Exception e) {
				// 
				e.printStackTrace();
			}

		}
	}

	class GameServerRebootThread extends Thread {

		public void run() {
			System.out.println("GameServerRebootThread running");
			while(true){
				if (System.currentTimeMillis() - startTime > 500 && serverIsOn){
					try {
						startTime = System.currentTimeMillis();
						JPlayer.setText(gameServer.getPlayerInfoString());
						JTreasure.setText(gameServer.getTreasureInfoString());
						JTimerInfo.setText(gameServer.getTimerInfoString()); 

						if(gameServer.getGlobalEventStatus() == 6){
							System.out.println("Reboot thread: closing gameserver");
							closeButton.doClick();
							System.out.println("Reboot thread: starting gameserver");
							startButton.doClick();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	// Game Server: feedback to client's requests through UDP connections
	class GameServer {

		private  EventHandler eventHandler;
		private  DatagramSocket socket ;

		GameServer() throws IOException{
			eventHandler = new EventHandler();
		}

		public void connect() throws Exception {

			System.out.println("Game Server starting up... ");
			JConsole.setText("Press Start to continue...\nGame Server starting up... ");

			/* *** Initialization *** */
			String reply = ""; // stores the reply info

			//use DatagramSocket for UDP connection
			//@SuppressWarnings("resource")
			socket = new DatagramSocket(9001);
			byte[] incomingBuffer = new byte[1000];

			// Constantly receiving incoming packets
			while (true)
			{								
				JConsole.setText(JConsole.getText() + "\nWaiting for incoming packet from game client... ");
				DatagramPacket incomingPacket = new DatagramPacket(incomingBuffer, incomingBuffer.length); 

				socket.receive(incomingPacket);

				// convert content of packet into a string 
				String request = new String(incomingPacket.getData(), 0, incomingPacket.getLength() );

				/* ----------------------------------------------------- */
				// pass client request to event handler to compute results

				reply = eventHandler.computeEventsReply(request);

				/* ----------------------------------------------------- */

				// convert reply into array of bytes (output buffer)
				byte[] outputBuffer = new byte[1000];
				outputBuffer = reply.getBytes();

				// create reply packet using output buffer.
				// Note: destination address/port is retrieved from incomingPacket
				DatagramPacket outPacket = new DatagramPacket(outputBuffer, outputBuffer.length, incomingPacket.getAddress(), incomingPacket.getPort());

				// finally, send the packet
				socket.send(outPacket);
				System.out.println("Sent reply: " + reply + " [GamerServer.java]");
				JConsole.setText(JConsole.getText() + "\n" + eventHandler.getReplyInfoString());
			}

		}

		public void movePlayer(int playerID, int direction) {
			eventHandler.movePlayer(playerID, direction);
			//JPlayer.setText(eventHandler.getPlayerInfoString());
		}

		public String getKeyCodeInfoString() {
			return eventHandler.getKeyCodeInfoString();
		}
		String getPlayerInfoString(){	
			return eventHandler.getPlayerInfoString();
		}

		String getTreasureInfoString(){	
			return eventHandler.getTreasureInfoString();
		}

		String getTimerInfoString(){	
			return eventHandler.getGameStatusInfoString();
		}

		int getGlobalEventStatus(){
			return eventHandler.getGlobalEventStatus();
		}

		public void disconnect() throws Exception {
			JConsole.setText(JConsole.getText() + "\nClosing server\nPress Start to continue...");
			eventHandler.stopGameTimer();
			socket.close();
		}

	}

	// multicast testing
	class GameServerCasting {

		private static final long boardCastInterval = 500;

		public void startCasting() throws Exception 
		{
			DatagramSocket socket = new DatagramSocket();
			boolean moreQuotes = true;

			while (moreQuotes) {
				try {
					// don't wait for request...just send a quote
					String dString = "RealIPAddressTest";
					byte[] buf = new byte[256];

					buf = dString.getBytes();
					InetAddress group = InetAddress.getByName("224.0.0.251");

					DatagramPacket packet;
					packet = new DatagramPacket(buf, buf.length, group, 8000);
					socket.send(packet);
					//System.out.println("mc testing: ");
					try {
						Thread.sleep(boardCastInterval);
					} 
					catch (InterruptedException e) { }
				}
				catch (IOException e) {
					e.printStackTrace();
					moreQuotes = false;
				}
			}
			socket.close();
		}
	}
}
