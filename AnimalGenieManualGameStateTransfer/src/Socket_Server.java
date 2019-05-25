import com.binarytree.gametree.*;
import java.io.*;
import java.net.*;
import java.time.LocalDateTime;

public class Socket_Server {
	public static void main(String args[]) throws UnknownHostException {
		boolean run = true;
		System.out.println("SERVER: ONLINE AT " + InetAddress.getLocalHost() + " AWAITING CONNECTIONS");
		do {
			// declaration section:
			// declare a server socket and a client socket for the server
			// declare an input and an output stream
			ServerSocket objectServer = null;
			String line;
			DataInputStream is = null;
			PrintStream os = null;
			Socket clientSocket = null;
			String gameTreeFile = "gameTree.dat";
			// Try to open a server socket on port 9999
			// Note that we can't choose a port less than 1023 if we are not
			// privileged users (root)
			try {
				objectServer = new ServerSocket(2148);
			} catch (IOException e) {
				System.out.println(e);
			}
			// Create a socket object from the ServerSocket to listen and accept
			// connections.
			// Open input and output streams
			try {
				clientSocket = objectServer.accept();
				is = new DataInputStream(clientSocket.getInputStream());
				// As long as we receive data, echo that data back to the client.

				String connectionType = is.readUTF();
				String filePath = is.readUTF();
				
				System.out.println("SERVER: RECIEVED COMMAND -> " + connectionType);
				
				if (connectionType.equals("-u")) {
					GameTree gameTree = null;
					System.out.println("SERVER: CONNECTION TYPE - UPLOAD");
					try {
						ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
						gameTree = (GameTree) ois.readObject();
						ois.close();
						System.out.println("SERVER: OBJECT RECEIVED\nSERVER: OBJECT SAMPLE: "
								+ gameTree.getRoot().getItem().getName() + "\nSERVER: ATTEMPTING TO SERIALIZE. . .");
						ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(gameTreeFile));
						oos.writeObject(gameTree);
						oos.close();
						System.out.println("SERVER: UPLOAD SUCCESS");

					} catch (Exception e) {
						System.out.println("SERVER: UPLOAD FAILED");
						e.printStackTrace();
					}
				} else if (connectionType.equals("-d")) {
					Object gameTree = null;
					try {
						FileInputStream fis = new FileInputStream(gameTreeFile);
						ObjectInputStream ois = new ObjectInputStream(fis);
						gameTree = ois.readObject();
						ois.close();
					} catch (ClassNotFoundException | IOException e) { // if the obj$
						gameTree = new GameTree(new TreeNode(new Item("Dog")));
					}
					System.out.println("SERVER: CONNECTION TYPE - DOWNLOAD");
					ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
					System.out.println("SERVER: BROADCASTING REQUESTED FILE");
					outputStream.writeObject(gameTree);
					System.out.println("SERVER: FILE SENT SUCCESSFULLY");
				} else if (connectionType.equals("-sd")) {
					System.out.println("SERVER: SHUTDOWN COMMAND RECEIVED FROM -> " + clientSocket.getRemoteSocketAddress().toString());
					run = false;					
				} else {
					System.err.println("[Invalid command]\n\n\tUse: -u\tupload object\n\t-d\tdownload object");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if(!run)
					System.out.println("SERVER: CLOSING CONNECTIONS AND SHUTTING DOWN");
				objectServer.close();
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(run)
				System.out.println("SERVER: TASK COMPLETE, AWAITING CONNECTIONS\n");
		} while (run);
		System.out.println("SERVER: OFFLINE AT -> " + LocalDateTime.now());
	}
}
