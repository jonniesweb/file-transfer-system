package ca.jonsimpson.comp3203.filetransfersystem;

import java.io.IOException;
import java.net.UnknownHostException;

/**
 * Launch a client which connects to a server to download/upload files.
 */
public class ClientMain {
	
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
		Client client = null;
		try {
			System.out.println("connecting to client on " + host + " " + port);
			client = new Client(host, port);
			
			System.out.println("sending server hello message");
			client.hello();
			System.out.println("received hello response");
			
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
}
