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
				if (message.split("/")[0].equals("POST")) {
					message = message.split("/")[1];
					if (message != null && !message.split(":")[0].equals(clientName)) {
						System.out.println(message + "\n\n");
					}
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
				String message = "POST/" + clientName + ": " + s.nextLine();
				os.writeBytes(message);
			}
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
		String connectionMessage = "CONNECT/" + clientName;
		os.writeBytes(connectionMessage);
		
		// Receives chat log and prints to client
		String log = "";
		String[] lines = is.lines().toArray(String[]::new);
		for (int i = 0; i < lines.length; i++) {
			log += lines[i];
			if (i < lines.length - 1) {
				log += "\r\n";
			}
		}
		System.out.print(log);
		
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
