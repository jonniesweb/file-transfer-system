package ca.jonsimpson.comp3203.filetransfersystem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;

/**
 * Launch a client which connects to a server to download/upload files.
 */
public class ClientMain extends Net {
	
	private BufferedReader reader;
	Client client = null;

	public static void main(String[] args) {
		new ClientMain(args[0], Integer.parseInt(args[1]));
	}
	
	/**
	 * Connect to the server located at host:port and begin the file transfer
	 * system.
	 * 
	 * @param host
	 * @param port
	 */
	public ClientMain(String host, int port) {
		try {
			System.out.println("connecting to client on " + host + " " + port);
			client = new Client(host, port);
			
			// do initial hello with server
			System.out.println("sending server hello message");
			client.hello();
			System.out.println("received hello response");
			
			// setup stdin
			reader = new BufferedReader(new InputStreamReader(System.in));
			
			// read commands from stdin indefinitely
			boolean isRunning = true;
			while (isRunning) {
				processCommands();
			}
			
			
		} catch (UnknownHostException e) {
			System.out.println("unknown host: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("error occurred with connection: " + e.getMessage());
			e.printStackTrace();
		}
		if (client != null) {
			client.close();
			
		}
	}

	private void processCommands() throws IOException {
		
		String input = reader.readLine();
		String[] strings = input.split(" ");
		
		// dont do anything if the line is empty
		if (strings.length == 0) {
			return;
		}
		
		switch (strings[0]) {
		case LS:
			client.getDirListing();
			break;
		
		case GET:
			client.getFile(strings[1]);
			break;
			
		case CD:
			client.changeDirectory(strings[1]);
			break;
		default:
			break;
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
