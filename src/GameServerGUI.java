/* CS3283/CS3284 Project
 * 
 * Game Server GUI: simple graphical user interface for the game server
 * 
 */

import java.awt.EventQueue;
import javax.swing.JFrame;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.awt.Rectangle;
import javax.swing.JTextArea;
import java.awt.Insets;
import java.awt.Color;
import javax.swing.JScrollPane;
import javax.swing.text.DefaultCaret;
import java.awt.Label;
import javax.swing.JButton;

public class GameServerGUI {

	private JFrame frame;
	private GameServer gameServer;
	private GameServerThread gameServerThread;

	JButton startButton = new JButton("Start Server");
	JButton closeButton = new JButton("Close Server");
	JButton btnMoveRightButton = new JButton(">");
	JTextArea JConsole = new JTextArea();
	JTextArea JKeyCode = new JTextArea();
	JTextArea JPlayer = new JTextArea();
	JTextArea JTreasure = new JTextArea();
	private final Label labelTreasureList = new Label("Treasure List");
	private final Label labelKeyCodeList = new Label("KeyCode List");
	private final Label labelPlayerInfo = new Label("Player Info");
	
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
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame("Project Kim Server");
		frame.getContentPane().setBackground(Color.LIGHT_GRAY);
		frame.setResizable(false);
		frame.setBounds(100, 100, 559, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		startButton.setMargin(new Insets(2, 5, 2, 5));

		startButton.setBounds(new Rectangle(449, 217, 94, 22));
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					startButton.setEnabled(false);
					closeButton.setEnabled(true);
					btnMoveRightButton.setEnabled(true);
					gameServerThread = new GameServerThread();
					gameServerThread.start();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		closeButton.setMargin(new Insets(2, 5, 2, 5));
		closeButton.setBounds(449, 250, 94, 22);
		closeButton.setEnabled(false);
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					//connectThread.stop();
					startButton.setEnabled(true);
					closeButton.setEnabled(false);
					btnMoveRightButton.setEnabled(false);
					gameServer.dc();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		frame.getContentPane().setLayout(null);
		frame.getContentPane().add(startButton);
		frame.getContentPane().add(closeButton);

		JScrollPane JConsoleScrollPane = new JScrollPane();
		JConsoleScrollPane.setBounds(10, 196, 429, 263);

		DefaultCaret caret = (DefaultCaret)JConsole.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		JConsoleScrollPane.setBackground(Color.BLACK);
		frame.getContentPane().add(JConsoleScrollPane);
		
		JConsoleScrollPane.setViewportView(JConsole);
		JConsole.setMargin(new Insets(5, 5, 5, 5));
		JConsole.setRows(5);
		JConsole.setEditable(false);
		JConsole.setText("JConsole");
		
		JKeyCode.setBounds(10, 11, 533, 74);
		JKeyCode.setEditable(false);
		JKeyCode.setMargin(new Insets(5, 5, 5, 5));
		frame.getContentPane().add(JKeyCode);
		JKeyCode.setText("????");
		JPlayer.setBounds(185, 105, 358, 80);
	
		JPlayer.setEditable(false);		
		JPlayer.setMargin(new Insets(5, 5, 5, 5));
		frame.getContentPane().add(JPlayer);
		JPlayer.setText("Player 0:   logon: ?  Score: ?  keysHeld: ?  Location: ?\n" 
				+ "Player 1:   logon: ?  Score: ?  keysHeld: ?  Location: ?\n" 
				+ "Player 2:   logon: ?  Score: ?  keysHeld: ?  Location: ?\n" 
				+ "Player 3:   logon: ?  Score: ?  keysHeld: ?  Location: ?\n");
		JTreasure.setBounds(10, 105, 165, 80);
		
		frame.getContentPane().add(JTreasure);
		JTreasure.setEditable(false);
		JTreasure.setMargin(new Insets(5, 5, 5, 5));
		JTreasure.setText("  ?  ?  ?\n  ?  ?  ?\n  ?  ?  ?");
		
		Label labelPKControls = new Label("Controls");
		labelPKControls.setBounds(449, 196, 63, 13);
		frame.getContentPane().add(labelPKControls);
		labelTreasureList.setBounds(10, 84, 78, 15);
		
		frame.getContentPane().add(labelTreasureList);
		labelKeyCodeList.setBounds(447, 296, 78, 13);
		
		frame.getContentPane().add(labelKeyCodeList);
		labelPlayerInfo.setBounds(185, 85, 65, 14);
		
		frame.getContentPane().add(labelPlayerInfo);
		
		btnMoveRightButton.setMargin(new Insets(2, 2, 2, 2));
		btnMoveRightButton.setEnabled(false);
		btnMoveRightButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				gameServer.movePlayer(1,3);
			}
		});
		btnMoveRightButton.setBounds(507, 359, 36, 40);
		frame.getContentPane().add(btnMoveRightButton);
	}


	class GameServerThread extends Thread {

		public void run() {
			System.out.println("GUI initiatised!");
			try {
				gameServer = new GameServer();
				gameServer.connect();
			} catch (Exception e) {
				// 
				e.printStackTrace();
			}

		}
	}

	// Game Server: feedback to client's requests through UDP connections
	class GameServer {

		private  EventHandler eventHandler;
		private  DatagramSocket socket ;

		public void connect() throws Exception {

			System.out.println("Game Server starting up... ");
			JConsole.setText("JConsole\nGame Server starting up... ");

			/* *** Initialization *** */
			String reply = ""; // stores the reply info
			eventHandler = new EventHandler();
			
			JPlayer.setText(eventHandler.getPlayerInfoString());
			JKeyCode.setText(eventHandler.getKeyCodeString());
			JTreasure.setText(eventHandler.getTreasureInfoString());
			
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
				JConsole.setText(JConsole.getText() + "\nSent reply: " + reply);
				JPlayer.setText(eventHandler.getPlayerInfoString());
				JTreasure.setText(eventHandler.getTreasureInfoString());
			}

		}

		public void movePlayer(int playerID, int direction) {
			eventHandler.movePlayer(playerID, direction);
			JPlayer.setText(eventHandler.getPlayerInfoString());
			
		}

		String getPlayerInfoString(){	
			return eventHandler.getPlayerInfoString();
		}

		String getTreasureInfoString(){	
			return eventHandler.getTreasureInfoString();
		}

		public void dc() throws Exception {
			JConsole.setText(JConsole.getText() + "\nClosing server");
			//socket.disconnect();

			socket.close();
			//connectThread.destroy();
		}

	}
}
