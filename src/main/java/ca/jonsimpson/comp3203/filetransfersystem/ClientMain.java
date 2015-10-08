package ca.jonsimpson.comp3203.filetransfersystem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.nio.file.Paths;

/**
 * Launch a client which connects to a server to download/upload files.
 */
public class ClientMain extends Net {
	
	private BufferedReader reader;
	Client client = null;
	
	public static void main(String[] args) {
		try {
			new ClientMain(getParam0(args), Integer.parseInt(getParam1(args)));
		} catch (NumberFormatException e) {
			System.out.println("Invalid port number");
			printUsage();
		} catch (ParameterException e) {
			System.out.println("Must use two parameters");
			printUsage();
		}
	}
	
	private static void printUsage() {
		System.out.println("Usage: java -jar client.jar <ip address> <port number>");
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
			
			printCWD();
			
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
	
	private void printCWD() {
		System.out.println("Current local working directory is: " + Paths.get("").toAbsolutePath());
	}
	
	private void processCommands() throws IOException {
		
		String input = reader.readLine();
		String[] strings = input.split(" ");
		
		// dont do anything if the line is empty
		if (strings.length == 0) {
			return;
		}
		
		try {
			switch (getParam0(strings)) {
			case LS:
				client.getDirListing();
				break;
			
			case GET:
				client.getFile(getParam1(strings));
				break;
			
			case CD:
				client.changeDirectory(getParam1(strings));
				break;
			
			case PUT:
				client.putFile(getParam1(strings));
				
			case MKDIR:
				client.makeDirectory(getParam1(strings));
			default:
				break;
			}
			
		} catch (ParameterException e) {
			System.out.println("Invalid number of parameters," + e.getMessage());
		}
		
	}
	
	private static String getParam1(String[] strings) throws ParameterException {
		if (strings.length < 2) {
			throw new ParameterException("Need 2 parameters");
		}
		
		return strings[1];
	}
	
	private static String getParam0(String[] strings) throws ParameterException {
		if (strings.length < 1) {
			throw new ParameterException("Needs 1 parameter");
		}
		
		return strings[0];
	}
	
	/**
	 * An error thrown when the number of parameters required isn't met.
	 */
	static final class ParameterException extends Exception {
		
		public ParameterException(String message) {
			super(message);
		}
		
	}
	
}
