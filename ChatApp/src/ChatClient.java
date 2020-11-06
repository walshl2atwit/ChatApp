import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Stream;

public class ChatClient {

	private static class MessageReceiver implements Runnable{
		BufferedReader is;
		String clientName;
		public MessageReceiver(BufferedReader is, String name) {
			this.is = is;
			clientName = name;
		}
		@Override
		public void run() {
			try {
				process();
			} catch (Exception e) {
				System.out.println("MessageReceiver error");
			}
		}
		
		// Constantly receives messages from server and prints them to the console if they're from another user
		private void process() throws Exception{
			while(true) {
				String message = is.readLine();
			
				// If header is POST then read message from server as text message
				if (message.split("/")[0].equals("POST")) {
					message = message.split("/")[1];
					if (message != null && !message.split(":")[0].equals(clientName)) {
						System.out.println(message);
					}
				// If header is ALERT then read message from server as an alert
				} else if (message.split("/")[0].equals("ALERT")) {
					message = message.split("/")[1];
					System.out.println(message);
				}
			}
		}
	}
	
	private static class MessageSender implements Runnable{
		DataOutputStream os;
		String clientName;
		public MessageSender(DataOutputStream os, String name) {
			this.os = os;
			clientName = name;
		}
		@Override
		public void run() {
			try {
				process();
			} catch (Exception e) {
				System.out.println("MessageSender error");
			}
		}
		
		private void process() throws Exception{
			Scanner s = new Scanner(System.in);
			
			while(true) {
			
			    			
			    String input = null;
				input = s.nextLine();
				if(input.equals("quit"))
			    {
			    
			        os.writeBytes("POST/" + clientName + " has left the chat" + "\r\n\r\n");
			       
			        break;
			    }
				String message = "POST/" + clientName + ": " + input + "\r\n\r\n";
				os.writeBytes(message);
			
			} os.close();
			
			
		}
	}
	
	public static void main(String[] args) throws Exception {
		// Introduction
		Scanner s = new Scanner(System.in);
		System.out.println("Welsome to NP chatroom! Please type your name and press enter...");
		String clientName = s.nextLine();
		System.out.println("Hello " + clientName + "! If you ever want to quit, type {quit} to exit.");
		
		// Connection to server
		Socket socket = new Socket("localhost", 1234);
		DataOutputStream os = new DataOutputStream(socket.getOutputStream());
		BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
		// Tell server that you connected
		String connectionMessage = "CONNECT/" + clientName + "\r\n\r\n";
		os.writeBytes(connectionMessage);
		
		// Starts thread that receives messages from server
		MessageReceiver receiver = new MessageReceiver(is, clientName);
		Thread threadReceiver = new Thread(receiver);
		
		threadReceiver.start();
            
		// Starts thread that sends messages to server
		MessageSender sender = new MessageSender(os, clientName);
		Thread threadSender = new Thread(sender);
		
		threadSender.start();
	}

}
