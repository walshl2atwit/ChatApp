import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.ArrayList;

public class ChatServer {
	
	// ArrayList of all created threads
	static ArrayList<Thread> threads = new ArrayList<Thread>();
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
			
			// 
			while(true) {
				String clientMessage = br.readLine();
				if (clientMessage != null) {
					if (clientMessage.split("/")[0].equals("CONNECT")) {
						clientMessage = clientMessage.split("/")[1] + " has joined the chat!";
						System.out.println(clientMessage);
						log(clientMessage);
					} else if (clientMessage.split("/")[0].equals("POST")) {
						System.out.println("test");
						clientMessage = clientMessage.split("/")[1];
						System.out.println(clientMessage);
						log(clientMessage);
					}
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
			Thread thread = new Thread(client);
			thread.start();
		}
		
	}
	
	// Method to add message to log
	private static void log(String message) throws IOException {
		PrintWriter writer = new PrintWriter(new FileWriter(log));
		writer.write(message + "\n");
		writer.close();
	}

}
