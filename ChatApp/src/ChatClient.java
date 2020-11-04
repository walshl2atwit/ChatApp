import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.stream.Stream;

public class ChatClient {

	public static void main(String[] args) throws Exception {
		Socket socket = new Socket("localhost", 1234);

		DataOutputStream os = new DataOutputStream(socket.getOutputStream());
		BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
		String requestLine = "GET /HelloWorld.html/ HTTP/1.1\r\n\r\n";
		os.writeBytes(requestLine);
		
		String responseLine = "";
		String[] lines = is.lines().toArray(String[]::new);
		for (int i = 0; i < lines.length; i++) {
			responseLine += lines[i];
			if (i < lines.length - 1) {
				responseLine += "\r\n";
			}
		}
		
		System.out.print("FROM SERVER: " + responseLine);
	}

}
