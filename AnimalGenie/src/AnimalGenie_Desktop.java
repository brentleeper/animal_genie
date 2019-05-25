
/*-----------------------------------------------------------------------------------
Name: 			Brent Leeper
Date: 			12/01/17
Course: 			2336.02
Semester: 		Fall 2017
Assignment #: 	5
Description: 	Main Class, attempts to load saved game-state from file into the GameTree,
				a binary tree holding all TreeNodes with data saved from previous sessions. 
				If the file cannot be found, a new GameTree is created with the default item
				"Dog." The game asks the player to think of an animal and asks the player to
				answer a series of yes/no questions to determine what animal the player is 
				thinking of. If the game guesses incorrectly, the player is asked to input
				what animal they were playing and a question that distinguishes it from 
				the incorrect guess, the distinguishing characteristic and the new animal are
				then added to the GameTree for future play.
--------------------------------------------------------------------------------------*/

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.binarytree.gametree.*;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import java.awt.event.ActionListener;

@SuppressWarnings("serial")
public class AnimalGenie_Desktop extends JFrame implements Serializable {

	private static JFrame frame;
	private static boolean inGame, passwordLockOut;
	private static Font gameTextFont;
	private static GameEditor editor;
	private static JLabel gameText;
	private static GameTree gameTree;
	private static TreeNode currRoot;
	private static String gameTreeFile, thinkingOf, questionMark;
	private ImageIcon genieIcon, winIcon;
	private JButton yesBtn, noBtn, editorBtn;
	private static AnimalGenie_Desktop game;

	/**
	 * @wbp.parser.entryPoint
	 */
	public static void main(String[] args) throws IOException { // main class
		gameTreeFile = "gameTree.dat";
		passwordLockOut = false;
		game = new AnimalGenie_Desktop();
		game.play(); // starts the game, builds user interface
	}

	public void play() throws FileNotFoundException, IOException { // builds user interface and displays game data
																	// relative the current play

		thinkingOf = "Are you thinking of a ";
		questionMark = "?";
		currRoot = null;
		inGame = false; // indicates current game state to the user interface
		gameTextFont = new Font("Lucida Grande", Font.PLAIN, 17);

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frame = new JFrame();
					frame.setVisible(true);
					frame.setSize(new Dimension(525, 225));
					frame.setResizable(false);
					frame.setLocationRelativeTo(null);
					frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					frame.getContentPane().setLayout(null);

					JLabel lblAnimalGenie = new JLabel("Animal Genie");
					lblAnimalGenie.setHorizontalAlignment(SwingConstants.CENTER);
					lblAnimalGenie.setFont(new Font("Lucida Grande", Font.BOLD, 22));
					lblAnimalGenie.setBounds(6, 6, 513, 39);
					frame.getContentPane().add(lblAnimalGenie);

					gameText = new JLabel("Welcome to animal genie!"); // initial state, "Welcome Screen"
					gameText.setFont(new Font("Lucida Grande", Font.PLAIN, 17));
					gameText.setHorizontalAlignment(SwingConstants.CENTER);
					gameText.setBounds(6, 71, 513, 40);
					frame.getContentPane().add(gameText);

					yesBtn = new JButton("Play");
					yesBtn.addActionListener(new ActionListener() { // when the player clicks the 'Yes' button
						public void actionPerformed(ActionEvent e) {
							if (inGame == false) // previous to this, the "Welcome Screen" is shown, if user clicks
													// "Yes," the game starts
							{
								loadGame();
								inGame = true; // change state to indicate to all future button clicks that the game is
												// in progress
								currRoot = gameTree.getRoot(); // get the root from the game tree
								JOptionPane.showMessageDialog(frame,
										"Think of an animal, I will try to read your mind!", null,
										JOptionPane.INFORMATION_MESSAGE, genieIcon); // prompts player to think of an
																						// animal
								yesBtn.setText("Yes");
								noBtn.setText("No");
								editorBtn.setEnabled(false);
								editorBtn.setVisible(false);
							} else // if inGame is true, indicating the game is in progress
							{
								if (currRoot.getItem().isQuestion()) // determine if the current root's item is a
																		// question(see Item Class for further detail on
																		// isQuestion())
									currRoot = currRoot.getLeft(); // if so, get the current root's left (the TreeNode
																	// correlating to a "Yes" answer to the current
																	// roots question.)
								else // if not, the current root is a guess to what animal the player is thinking of,
										// a yes response from the player indicates the game guessed correctly
								{
									currRoot = new TreeNode(new Item("Would you like to play again?")); // change game
																										// text to
																										// prompt user
																										// if they want
																										// to play again
									currRoot.getItem().setQuestion(true); // ^
									gameText.setText(currRoot.getItem().getName()); // ^
									gameText.setFont(gameTextFont); // ^
									inGame = false; // change inGame to false, indicating the game is no longer in
													// progress.
									JOptionPane.showMessageDialog(frame, "I read your mind!", null,
											JOptionPane.INFORMATION_MESSAGE, winIcon); // display "I Win"
									yesBtn.setText("Play");
									noBtn.setText("Exit");
									editorBtn.setEnabled(true);
									editorBtn.setVisible(true);
									if (passwordLockOut == true)
										editorBtn.setEnabled(false);
								}

							}
							if (currRoot.getItem().isQuestion()) // if the current root is a question, change the game
																	// text to just the item's name
							{
								gameText.setText(currRoot.getItem().getName());
								gameText.setFont(gameTextFont);
							} else // if the current root is not a question (but is a guess of the player animal)
							{
								gameText.setText(thinkingOf + currRoot.getItem().getName() + questionMark); // concatenate
																											// the
																											// item's
																											// name to
																											// thinkingOf
																											// and
																											// concatenate
																											// questionMark
																											// to the
																											// end
								gameText.setFont(gameTextFont);
							}
						}
					});
					yesBtn.setBounds(142, 142, 117, 29);
					frame.getContentPane().add(yesBtn); // add the 'Yes' button to the user interface

					noBtn = new JButton("Exit");
					noBtn.addActionListener(new ActionListener() { // when the player clicks the 'No' button
						public void actionPerformed(ActionEvent e) {
							if (inGame == false) // if a game is not in progress and the player clicks 'No'
							{
								System.exit(0);
							} else // if a game is in progress
							{
								if (currRoot.getItem().isQuestion()) // determine if the current root's item is a
																		// question(see Item Class for further detail on
																		// isQuestion())
									currRoot = currRoot.getRight(); // if so, get the current root's right (the TreeNode
																	// correlating to a "No" answer to the current roots
																	// question.)
								else // if not, the current root is a guess to what animal the player is thinking of,
										// a no response from the player indicates the game guessed incorrectly
								{
									try {
										addNewAnimal(); // call addNewAnimal
									} catch (IOException e1) {
										JOptionPane.showMessageDialog(null,
												"Something broke, BAD!! (Error: Could Not Add Animal)"); // this should
																											// never
																											// happen...
																											// hopefully
									}
									currRoot = new TreeNode(new Item("Would you like to play again?")); // change game
																										// text to
																										// prompt user
																										// if they want
																										// to play again
									currRoot.getItem().setQuestion(true); // ^
									gameText.setText(currRoot.getItem().getName()); // ^
									gameText.setFont(gameTextFont); // ^
									inGame = false; // change inGame to false, indicating the game is no longer in
													// progress.
									yesBtn.setText("Play");
									noBtn.setText("Exit");
									editorBtn.setEnabled(true);
									editorBtn.setVisible(true);
									if (passwordLockOut == true)
										editorBtn.setEnabled(false);
								}
							}

							if (currRoot.getItem().isQuestion()) // if the current root is a question, change the game
																	// text to just the item's name
							{
								gameText.setText(currRoot.getItem().getName());
								gameText.setFont(gameTextFont);
							} else // if the current root is not a question (but is a guess of the player animal)
							{
								gameText.setText(thinkingOf + currRoot.getItem().getName() + questionMark); // concatenate
																											// the
																											// item's
																											// name to
																											// thinkingOf
																											// and
																											// concatenate
																											// questionMark
																											// to the
																											// end
								gameText.setFont(gameTextFont);
							}
						}
					});
					noBtn.setBounds(268, 142, 117, 29);
					frame.getContentPane().add(noBtn); // add the "No" button to the user interface

					editorBtn = new JButton("...");
					editorBtn.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							try {
								runGameEditor(); // Call runGameEditor
							} catch (Exception e1) {
								JOptionPane.showMessageDialog(frame, "The Game Editor Failed To Load");
							}
						}
					});
					editorBtn.setBounds(472, 168, 47, 29);
					if (passwordLockOut == true)
						editorBtn.setEnabled(false);
					frame.getContentPane().add(editorBtn);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void runGameEditor() throws FileNotFoundException, IOException, ClassNotFoundException { // asks the player
																									// if they would
																									// like to enter the
																									// game editor, if
																									// so, prompt for
																									// password. If
																									// password is
																									// correct, create a
																									// GameEditor Object
		int option = JOptionPane.showConfirmDialog(frame, "Would you like to start the Game Editor.", "Animal Genie", 0,
				JOptionPane.YES_NO_OPTION, genieIcon);
		frame.dispose(); // kill the game JFrame
		if (option == JOptionPane.YES_OPTION) // if the user responds "Yes" to the above prompt
		{
			boolean badPassword = false; // used to determine if the password was entered incorrectly
			int tryCt = 3; // number of password attempts

			Box box = Box.createHorizontalBox(); // Create box with password field for secure password entry
			// ^
			JLabel jl = new JLabel("Password: "); // ^
			box.add(jl); // ^
			// ^
			JPasswordField jpf = new JPasswordField(24); // ^
			box.add(jpf); // ^

			do {
				tryCt--;
				int button = JOptionPane.showConfirmDialog(null, box, "Enter Game Editor Password",
						JOptionPane.OK_CANCEL_OPTION, JOptionPane.OK_CANCEL_OPTION, genieIcon); // This JOptionPane uses
																								// the Box object
																								// created just above

				if (button == JOptionPane.OK_OPTION) // if the user clicks cancel or 'X' quit the program
				{
					@SuppressWarnings("deprecation")
					String enteredPassword = jpf.getText(); // not secure, I know.
					String password = "nopeaking";

					if (enteredPassword.equals(password)) // if the entered password matches the stored password, make a
															// GameEditor object and call loadData to display the game
															// editor user interface
					{
						badPassword = false;

						FileInputStream fis = new FileInputStream(gameTreeFile);
						ObjectInputStream ois = new ObjectInputStream(fis);
						gameTree = (GameTree) ois.readObject();
						ois.close();
						System.out.println("Game loaded from: local");

						editor = new GameEditor();
						editor.loadData(gameTree);
					} else // otherwise
					{
						badPassword = true;
						if (tryCt > 0) {
							JOptionPane.showMessageDialog(null, "Invalid Password.\nAttempts Remaining: " + tryCt,
									"Game Editor Login", JOptionPane.OK_CANCEL_OPTION, genieIcon);
						} else {
							passwordLockOut = true;
							game.play();
						}
					}
					jpf.setText(null);
					password = null;
					enteredPassword = null;
				} else {
					game.play();
				}
			} while (badPassword && tryCt > 0);
		} else // if the user responds "No" to the prompt (Would you like to start the Game
				// Editor)
		{
			game.play();
		}
	}

	public void addNewAnimal() throws FileNotFoundException, IOException { // asks player for what animal they are
																			// thinking of and distinguishing
																			// characteristic, creates new TreeNodes
																			// with player's inputs and calls
																			// GameTree.addNode
		String newAnimal = (String) JOptionPane.showInputDialog(frame,
				"You Win...\nBUT...What Animal Are You Thinking Of?", "Animal Genie", 0, genieIcon, null, null);

		if (newAnimal != null && !newAnimal.trim().equals("")) // if user enters any text and does not click "Cancel" in
																// the prompt above
		{
			String difference = (String) JOptionPane.showInputDialog(frame, "Enter a yes/no question distinguishing "
					+ newAnimal + " From " + currRoot.getItem().getName() + ".", "Animal Genie", 0, genieIcon, null,
					null);
			if (difference != null && !difference.trim().equals("")) // if user enters any text and does not click
																		// "Cancel" in the prompt above
			{
				gameTree.addNode(currRoot, new TreeNode(new Item(newAnimal)), new TreeNode(new Item(difference))); // call
																													// GameTree
																													// addNode
																													// method
																													// passing
																													// TreeNodes
																													// with
																													// items
																													// from
																													// user
																													// input
				saveGame(false); // call saveGame
			}
		}
	}

	public void saveGame(boolean wait) throws FileNotFoundException, IOException { // saves the serialized GameTree
																					// object to a file
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(gameTreeFile));
		oos.writeObject(gameTree);
		oos.close();

		Thread upload = new Thread(new Runnable() {
			public void run() {
				/*
				 * String host = "74.194.202.77"; int port = 2022; String user = "pi"; String
				 * pass = "123qwe"; String directory ="/var/www/html/game_data/";
				 * 
				 * Session session = null; Channel channel = null; ChannelSftp channelSftp =
				 * null;
				 * 
				 * try { System.out.println("Attempting to transfer game-state to: " + host +
				 * ":" + port + directory); JSch jsch = new JSch(); session =
				 * jsch.getSession(user, host, port); session.setPassword(pass);
				 * java.util.Properties config = new java.util.Properties();
				 * config.put("StrictHostKeyChecking", "no"); session.setConfig(config);
				 * session.connect(); channel = session.openChannel("sftp"); channel.connect();
				 * channelSftp = (ChannelSftp) channel; channelSftp.cd(directory); File f = new
				 * File(gameTreeFile); channelSftp.put(new FileInputStream(f), f.getName());
				 * System.out.println("The host accepted the file"); } catch (Exception ex) {
				 * ex.printStackTrace(); System.err.println("The host rejected the file"); }
				 */

				Socket smtpSocket = null;
				DataOutputStream os = null;
				ObjectOutputStream obs = null;

				try {
					smtpSocket = new Socket("74.113.215.53", 2148);
					os = new DataOutputStream(smtpSocket.getOutputStream());

				} catch (SocketException se) {
					se.printStackTrace();
					// System.exit(0);
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (smtpSocket != null && os != null) {
					try {
						String connectionType = "-u";
						String filePath = "~/game_data";
						os.writeUTF(connectionType);
						os.writeUTF(filePath);

						obs = new ObjectOutputStream(smtpSocket.getOutputStream());
						obs.writeObject(gameTree);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
		upload.start();
		if (wait) {
			try {
				upload.join();
			} catch (InterruptedException e1) {

			}
		}
	}

	public void loadGame() { // attempts to load the game tree file into the GameTree object.
		gameTreeFile = "gameTree.dat";

		Thread download = new Thread(new Runnable() {
			public void run() {
				/*
				 * try { URL website = new
				 * URL("http://74.194.202.77:8080/game_data/gameTree.dat");
				 * System.out.println("Requesting game-state transfer from: " +
				 * website.toString()); FileUtils.copyURLToFile(website, new
				 * File(gameTreeFile)); //ReadableByteChannel rbc =
				 * Channels.newChannel(website.openStream()); //FileOutputStream fos = new
				 * FileOutputStream(gameTreeFile); //fos.getChannel().transferFrom(rbc, 0,
				 * Long.MAX_VALUE); System.out.println("File received from host"); }
				 * catch(Exception e) { System.err.println("The host rejected the request"); }
				 */

				boolean downloadFailed = true;
				Socket smtpSocket = null;
				DataOutputStream os = null;
				ObjectInputStream is = null;
				try {
					smtpSocket = new Socket("74.113.215.53", 2148);
					os = new DataOutputStream(smtpSocket.getOutputStream());
				} catch (UnknownHostException e) {
					System.err.println("Don't know about host: hostname");
				} catch (IOException e) {
					System.err.println("Couldn't get I/O for the connection...loading from local backup");
				}
				if (smtpSocket != null && os != null) {
					try {
						String connectionType = "-d";
						String filePath = "~/game_data";
						os.writeUTF(connectionType);
						os.writeUTF(filePath);
						is = new ObjectInputStream(smtpSocket.getInputStream());
						gameTree = (GameTree) is.readObject();
						System.out.println("Game loaded from: host");
						os.close();
						is.close();
						smtpSocket.close();
						downloadFailed = false;
					} catch (UnknownHostException e) {
						System.err.println("Trying to connect to unknown host: " + e);
					} catch (IOException e) {
						System.err.println("IOException:  " + e);
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (downloadFailed) {
					try {
						FileInputStream fis = new FileInputStream(gameTreeFile);
						ObjectInputStream ois = new ObjectInputStream(fis);
						gameTree = (GameTree) ois.readObject();
						ois.close();
						System.out.println("Game loaded from: local");
					} catch (ClassNotFoundException | IOException e) { // if the object cannot be loaded, make a new
																		// Game Tree with the default "Dog"
						gameTree = new GameTree(new TreeNode(new Item("Dog")));
					}
				}
			}
		});

		download.start();

		try {
			java.net.URL genie = getClass().getResource("genie.png"); // tries to load image elements
			java.net.URL win = getClass().getResource("win.jpg"); // ^
			genieIcon = new ImageIcon(genie); // ^
			winIcon = new ImageIcon(win); // ^
		} catch (Exception e) {
			System.err.println("Image Load Failed, Continuing...");
		}

		try {
			download.join();
		} catch (InterruptedException e1) {
		}

	}
}
