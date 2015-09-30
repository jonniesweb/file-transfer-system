package ca.jonsimpson.comp3203.filetransfersystem;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	
	private boolean isRunning = true;
	private ServerSocket serverSocket;
	
	public Server(int port) throws IOException {
		serverSocket = new ServerSocket(port);
	}
	
	public void start() {
		while (isRunning) {
			try {
				System.out.println("Waiting for connections");
				Socket socket = serverSocket.accept();
				
				System.out.println("received connection from " + socket.getInetAddress());
				Connection connection = new Connection(socket);
				
				System.out.println("begin processing connection");
				connection.process();
				
			} catch (IOException e) {
				System.out.println("Error occurred with connection: " + e.getMessage());
			}
		}
		
	}
	
}
