package ca.jonsimpson.comp3203.filetransfersystem;

import java.io.IOException;

/**
 * Launch a server which allows for clients to connect and download/upload
 * files.
 */
public class ServerMain {
	
	public static void main(String[] args) {
		if (args.length >= 1) {
			new ServerMain(Integer.parseInt(args[0]));
		} else {
			new ServerMain(45000);
		}
		
	}
	
	/**
	 * Start a server instance at the given port and start accepting
	 * connections.
	 * 
	 * @param port
	 */
	public ServerMain(int port) {
		try {
			Server server = new Server(port);
			server.start();
		} catch (IOException e) {
			System.out.println("unable to create the server: " + e.getMessage());
		}
	}
}
