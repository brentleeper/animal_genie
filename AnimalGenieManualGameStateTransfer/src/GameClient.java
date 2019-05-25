import java.io.*;
import java.net.*;
import java.util.Scanner;
import com.binarytree.gametree.*;

public class GameClient {
	public static void main(String[] args) throws ClassNotFoundException {
		// declaration section:
		// smtpClient: our client socket
		// os: output stream
		// is: input stream
		String gameTreeFile = "gameTree.dat";
		boolean upload = false;
		Scanner scan = new Scanner(System.in);
		System.out.print("Upload('u') or Download('d') ? : ");
		String input = scan.next();
		if (input.equals("d")) {
			Socket smtpSocket = null;
			DataOutputStream os = null;
			ObjectInputStream is = null;
			GameTree gameTree = null;
			try {
				smtpSocket = new Socket("74.113.215.53", 2148);
				os = new DataOutputStream(smtpSocket.getOutputStream());
			} catch (UnknownHostException e) {
				System.err.println("Don't know about host: hostname");
			} catch (IOException e) {
				System.err.println("Couldn't get I/O for the connection to: hostname");
			}
			if (smtpSocket != null && os != null) {
				try {
					String connectionType = "-d";
					String filePath = "~/game_data";
					os.writeUTF(connectionType);
					os.writeUTF(filePath);
					is = new ObjectInputStream(smtpSocket.getInputStream());
					gameTree = (GameTree) is.readObject();
					TreeNode node = gameTree.getRoot();
					System.out.print("Got object, Sample: ");
					System.out.println(node.getItem().getName());
					os.close();
					is.close();
					smtpSocket.close();
					ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(gameTreeFile));
					oos.writeObject(gameTree);
					oos.close();
				} catch (UnknownHostException e) {
					System.err.println("Trying to connect to unknown host: " + e);
				} catch (IOException e) {
					System.err.println("IOException:  " + e);
				}
			}
		} else if (input.equals("u")) {
			Socket smtpSocket = null;
			DataOutputStream os = null;
			ObjectOutputStream obs = null;
			GameTree gameTree = null;
			try {
				FileInputStream fis = new FileInputStream(gameTreeFile);
				ObjectInputStream ois = new ObjectInputStream(fis);
				gameTree = (GameTree) ois.readObject();
				ois.close();
			} catch (ClassNotFoundException | IOException e) { // if the object cannot be loaded, make a new Game Tree
																// with the default "Dog"
				e.printStackTrace();
				System.exit(0);
			}

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
					
					System.out.println("Object Broadcasted to Server");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else if (input.equals("sd")) {
			Socket smtpSocket = null;
			DataOutputStream os = null;
			ObjectInputStream is = null;
			GameTree gameTree = null;
			try {
				smtpSocket = new Socket("74.113.215.53", 2148);
				os = new DataOutputStream(smtpSocket.getOutputStream());
			} catch (UnknownHostException e) {
				System.err.println("Don't know about host: hostname");
			} catch (IOException e) {
				System.err.println("Couldn't get I/O for the connection to: hostname");
			}
			if (smtpSocket != null && os != null) {
				try {
					String connectionType = "-sd";
					String filePath = "~/game_data";
					os.writeUTF(connectionType);
					os.writeUTF(filePath);
					os.close();
					smtpSocket.close();
					
					System.out.println("Sent shutdown signal to server.");
				} catch (UnknownHostException e) {
					System.err.println("Trying to connect to unknown host: " + e);
				} catch (IOException e) {
					System.err.println("IOException:  " + e);
				}
			}
		}
	}
}