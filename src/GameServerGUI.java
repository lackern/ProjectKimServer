/* CS3283/CS3284 Project
 * 
 * Game Server GUI: simple graphical user interface for the game server
 * 
 */

import java.awt.EventQueue;
import javax.swing.JFrame;
import java.awt.Button;
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

	Button startButton = new Button("Start Server");
	Button closeButton = new Button("Close Server");
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

		startButton.setBounds(new Rectangle(10, 29, 94, 22));
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					gameServerThread = new GameServerThread();
					gameServerThread.start();
					startButton.setEnabled(false);
					closeButton.setEnabled(true);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		closeButton.setBounds(10, 57, 94, 22);
		closeButton.setEnabled(false);
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					//connectThread.stop();
					gameServer.dc();
					startButton.setEnabled(true);
					closeButton.setEnabled(false);

				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		frame.getContentPane().setLayout(null);
		frame.getContentPane().add(startButton);
		frame.getContentPane().add(closeButton);

		JScrollPane JConsoleScrollPane = new JScrollPane();
		JConsoleScrollPane.setBounds(10, 198, 474, 261);

		DefaultCaret caret = (DefaultCaret)JConsole.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		JConsoleScrollPane.setBackground(Color.BLACK);
		frame.getContentPane().add(JConsoleScrollPane);
		JConsoleScrollPane.setViewportView(JConsole);
		JConsole.setMargin(new Insets(5, 5, 5, 5));
		JConsole.setRows(5);
		JConsole.setEditable(false);
		JConsole.setText("JConsole");
		JKeyCode.setBounds(110, 29, 433, 50);
		JKeyCode.setEditable(false);
		JKeyCode.setMargin(new Insets(5, 5, 5, 5));
		frame.getContentPane().add(JKeyCode);
		JKeyCode.setText("????");
		JPlayer.setBounds(199, 105, 344, 87);
	
		JPlayer.setEditable(false);		
		JPlayer.setMargin(new Insets(5, 5, 5, 5));
		frame.getContentPane().add(JPlayer);
		JPlayer.setText("Player 0:   logon: ?  Score: ?  keysHeld: ?  Location: ?\n" 
				+ "Player 1:   logon: ?  Score: ?  keysHeld: ?  Location: ?\n" 
				+ "Player 2:   logon: ?  Score: ?  keysHeld: ?  Location: ?\n" 
				+ "Player 3:   logon: ?  Score: ?  keysHeld: ?  Location: ?\n");
		JTreasure.setBounds(10, 105, 175, 87);
		
		frame.getContentPane().add(JTreasure);
		JTreasure.setEditable(false);
		JTreasure.setMargin(new Insets(11, 11, 11, 11));
		JTreasure.setText("  ?  ?  ?\n  ?  ?  ?\n  ?  ?  ?");
		
		Label labelPKServer = new Label("PK Server");
		labelPKServer.setBounds(25, 10, 63, 13);
		frame.getContentPane().add(labelPKServer);
		labelTreasureList.setBounds(26, 85, 78, 15);
		
		frame.getContentPane().add(labelTreasureList);
		labelKeyCodeList.setBounds(120, 10, 78, 13);
		
		frame.getContentPane().add(labelKeyCodeList);
		labelPlayerInfo.setBounds(214, 85, 65, 14);
		
		frame.getContentPane().add(labelPlayerInfo);
		
		JButton btnRightButton = new JButton(">");
		btnRightButton.setMargin(new Insets(2, 2, 2, 2));
		btnRightButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				gameServer.moveRight(1);
			}
		});
		btnRightButton.setBounds(507, 203, 36, 40);
		frame.getContentPane().add(btnRightButton);
	}


	class GameServerThread extends Thread {

		public void run() {
			System.out.println("GUI initiatised!");
			try {
				gameServer = new GameServer();
				gameServer.connect();
			} catch (Exception e) {
				// TODO Auto-generated catch block
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

		public void moveRight(int playerID) {
			// TODO Auto-generated method stub
			eventHandler.moveRight(playerID);
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
