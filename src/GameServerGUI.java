

import java.awt.EventQueue;
import javax.swing.JFrame;
import java.awt.Button;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.awt.Rectangle;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;
import java.awt.Insets;
import java.awt.Color;
import javax.swing.JScrollPane;
import javax.swing.text.DefaultCaret;
import java.awt.Label;

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
		frame.setBounds(100, 100, 500, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		startButton.setBounds(new Rectangle(0, 0, 55, 55));
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
		
		SpringLayout springLayout = new SpringLayout();
		springLayout.putConstraint(SpringLayout.NORTH, labelPlayerInfo, 6, SpringLayout.SOUTH, JKeyCode);
		springLayout.putConstraint(SpringLayout.WEST, labelPlayerInfo, 10, SpringLayout.WEST, JKeyCode);
		springLayout.putConstraint(SpringLayout.SOUTH, labelPlayerInfo, -6, SpringLayout.NORTH, JPlayer);
		springLayout.putConstraint(SpringLayout.NORTH, labelKeyCodeList, -19, SpringLayout.NORTH, JKeyCode);
		springLayout.putConstraint(SpringLayout.SOUTH, labelKeyCodeList, -6, SpringLayout.NORTH, JKeyCode);
		springLayout.putConstraint(SpringLayout.NORTH, labelTreasureList, 6, SpringLayout.SOUTH, closeButton);
		springLayout.putConstraint(SpringLayout.SOUTH, labelTreasureList, -5, SpringLayout.NORTH, JTreasure);
		springLayout.putConstraint(SpringLayout.EAST, labelTreasureList, 0, SpringLayout.EAST, startButton);
		springLayout.putConstraint(SpringLayout.NORTH, JTreasure, 0, SpringLayout.NORTH, JPlayer);
		springLayout.putConstraint(SpringLayout.WEST, JTreasure, 0, SpringLayout.WEST, startButton);
		springLayout.putConstraint(SpringLayout.EAST, JTreasure, -6, SpringLayout.WEST, JPlayer);
		springLayout.putConstraint(SpringLayout.WEST, JPlayer, 0, SpringLayout.WEST, JKeyCode);
		springLayout.putConstraint(SpringLayout.NORTH, JPlayer, 105, SpringLayout.NORTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, JPlayer, -10, SpringLayout.EAST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, closeButton, 10, SpringLayout.WEST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, closeButton, -6, SpringLayout.WEST, JKeyCode);
		springLayout.putConstraint(SpringLayout.WEST, startButton, 10, SpringLayout.WEST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, startButton, -6, SpringLayout.WEST, JKeyCode);
		springLayout.putConstraint(SpringLayout.SOUTH, JKeyCode, 0, SpringLayout.SOUTH, closeButton);
		springLayout.putConstraint(SpringLayout.NORTH, JKeyCode, 29, SpringLayout.NORTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.NORTH, closeButton, 57, SpringLayout.NORTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, closeButton, -390, SpringLayout.SOUTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, startButton, -6, SpringLayout.NORTH, closeButton);
		springLayout.putConstraint(SpringLayout.WEST, JKeyCode, 110, SpringLayout.WEST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, JKeyCode, -10, SpringLayout.EAST, frame.getContentPane());
		frame.getContentPane().setLayout(springLayout);
		frame.getContentPane().add(startButton);
		frame.getContentPane().add(closeButton);

		JScrollPane JConsoleScrollPane = new JScrollPane();
		springLayout.putConstraint(SpringLayout.SOUTH, JTreasure, -6, SpringLayout.NORTH, JConsoleScrollPane);
		springLayout.putConstraint(SpringLayout.SOUTH, JPlayer, -6, SpringLayout.NORTH, JConsoleScrollPane);
		springLayout.putConstraint(SpringLayout.NORTH, JConsoleScrollPane, 198, SpringLayout.NORTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, JConsoleScrollPane, -10, SpringLayout.SOUTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, JConsoleScrollPane, 10, SpringLayout.WEST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, JConsoleScrollPane, -10, SpringLayout.EAST, frame.getContentPane());

		DefaultCaret caret = (DefaultCaret)JConsole.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		JConsoleScrollPane.setBackground(Color.BLACK);
		frame.getContentPane().add(JConsoleScrollPane);
		JConsoleScrollPane.setViewportView(JConsole);
		
		springLayout.putConstraint(SpringLayout.NORTH, JConsole, 84, SpringLayout.SOUTH, closeButton);
		springLayout.putConstraint(SpringLayout.WEST, JConsole, 0, SpringLayout.WEST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, JConsole, 0, SpringLayout.SOUTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, JConsole, -242, SpringLayout.EAST, frame.getContentPane());
		JConsole.setMargin(new Insets(5, 5, 5, 5));
		JConsole.setRows(5);
		JConsole.setEditable(false);
		JConsole.setText("JConsole");
		JKeyCode.setEditable(false);
		JKeyCode.setMargin(new Insets(5, 5, 5, 5));
		frame.getContentPane().add(JKeyCode);
		JKeyCode.setText("????");
	
		JPlayer.setEditable(false);		
		JPlayer.setMargin(new Insets(5, 5, 5, 5));
		frame.getContentPane().add(JPlayer);
		JPlayer.setText("Player 0:   logon: ?  Score: ?  keysHeld: ?  Location: ?\n" 
				+ "Player 1:   logon: ?  Score: ?  keysHeld: ?  Location: ?\n" 
				+ "Player 2:   logon: ?  Score: ?  keysHeld: ?  Location: ?\n" 
				+ "Player 3:   logon: ?  Score: ?  keysHeld: ?  Location: ?\n");
		
		frame.getContentPane().add(JTreasure);
		JTreasure.setEditable(false);
		JTreasure.setMargin(new Insets(11, 11, 11, 11));
		JTreasure.setText("  ?  ?  ?\n  ?  ?  ?\n  ?  ?  ?");
		
		Label labelPKServer = new Label("PK Server");
		springLayout.putConstraint(SpringLayout.WEST, labelKeyCodeList, 32, SpringLayout.EAST, labelPKServer);
		springLayout.putConstraint(SpringLayout.NORTH, labelPKServer, 10, SpringLayout.NORTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, labelPKServer, 25, SpringLayout.WEST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, labelPKServer, -6, SpringLayout.NORTH, startButton);
		frame.getContentPane().add(labelPKServer);
		
		frame.getContentPane().add(labelTreasureList);
		
		frame.getContentPane().add(labelKeyCodeList);
		
		frame.getContentPane().add(labelPlayerInfo);
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
