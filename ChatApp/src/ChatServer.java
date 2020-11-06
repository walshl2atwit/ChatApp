import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.ArrayList;

public class ChatServer {
	
	// ArrayList of all created threads
	static ArrayList<Thread> threads = new ArrayList<Thread>();
	static ArrayList<ClientHandler> clients = new ArrayList<ClientHandler>();
	static File log = new File("log.txt");
	
	// Class that handles each client in order to make server multi-threaded
	private static class ClientHandler implements Runnable{
		Socket socket;
		public ClientHandler(Socket socket) {
			this.socket = socket;
		}
		@Override
		public void run() {
			try {
				process();
			} catch (Exception e) {
				
			}
		}
		
		private void process() throws Exception{
			// Get a reference to the socket's input and output streams.
			InputStream is = socket.getInputStream();
			DataOutputStream os = new DataOutputStream(socket.getOutputStream());

			// Set up input stream filters.
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			
			// Loop that constantly receives messages and manages the clients
			
			while(true) {
				String clientMessage = br.readLine();
				
				
					// If message header is CONNECT, that signifies a user has connected and alerts the chat
				   if (clientMessage.split("/")[0].equals("CONNECT")) {
						// Alerts that a new client as joined
						clientMessage = clientMessage.split("/")[1] + " has joined the chat!";
						System.out.println(clientMessage);
						log(clientMessage);
						sendToClients("ALERT/" + clientMessage + "\r\n\r\n");
					// If message header is POST, that signifies the client is sending a text in the chat
					} else if (clientMessage.split("/")[0].equals("POST")) {
						clientMessage = clientMessage.split("/")[1];
						log(clientMessage);
						sendToClients("POST/" + clientMessage + "\r\n\r\n");
					// If message header is DISCONNECT, that signifies the client is trying to quit
					} else if (clientMessage.split("/")[0].equals("DISCONNECT")) {
						// Alerts that a new client as joined
						clientMessage = clientMessage.split("/")[1] + " has left the chat!";
						System.out.println(clientMessage);
						log(clientMessage);
						sendToClients("ALERT/" + clientMessage + "\r\n\r\n");
						threads.remove(clients.indexOf(this));
						clients.remove(this);
					}
				}
			}
		}
	
	public static void main(String[] args) throws Exception {
		int mainPort = 1234;
		
		ServerSocket serverSocket = new ServerSocket(mainPort);
		
		System.out.println("Waiting for connection...");
		
		// Creates new thread after each connection
		while (true) {
			Socket connectionSocket = serverSocket.accept();
			
			ClientHandler client = new ClientHandler(connectionSocket);
			clients.add(client);
			Thread thread = new Thread(client);
			threads.add(thread);
			thread.start();
		}
	}
	
	// Method to add message to log
	private static void log(String message) throws IOException {
		Scanner s = new Scanner(log);
		//PrintWriter writer = new PrintWriter(new FileWriter(log));
		BufferedWriter writer = new BufferedWriter(new FileWriter(log, true));
		writer.write(message + "\n");
		writer.close();
	}
	
	// Method that sends a message to all connected clients
	private static void sendToClients(String message) throws IOException {
		for (int i = 0; i < clients.size(); i++) {
			DataOutputStream os = new DataOutputStream(clients.get(i).socket.getOutputStream());
			os.writeBytes(message);
		}
	}
}
