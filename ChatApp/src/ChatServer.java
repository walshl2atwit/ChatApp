import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatServer {
	
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

			// Get the request line of the HTTP request message.
			String requestLine = br.readLine();
			System.out.println("Request received from client");
			
			// Reads requested file name from request
			String fileName = requestLine.split("/")[1];
			
			// Reads html info from file
			try {
				Scanner s = new Scanner(new File(fileName));
				String fileContents = "";
				while (s.hasNextLine()) {
					fileContents += s.nextLine();
					if (s.hasNextLine()) {
						fileContents += "\r\n";
					}
				}
				
				// Sends response back to client with file and header
				String responseLine = "HTTP/1.1 200 OK\r\n\r\n" + fileContents;
				os.writeBytes(responseLine);
				s.close();
			} catch (Exception e) {
				String resposneLine = "HTTP/1.0 404 Not Found\r\n\r\n<HTML><BODY><H1>Not Found</H1></BODY></HTML>";
				os.writeBytes(resposneLine);
			}
			

			is.close();
			os.close();
		}
	}
	
	public static void main(String[] args) throws Exception {
		int mainPort = 1234;
		
		ServerSocket serverSocket = new ServerSocket(mainPort);
		
		System.out.println("The server is ready to receive.");
		
		while (true) {
			Socket connectionSocket = serverSocket.accept();
			
			ClientHandler client = new ClientHandler(connectionSocket);
			Thread thread = new Thread(client);
			thread.start();
		}
		
	}

}
