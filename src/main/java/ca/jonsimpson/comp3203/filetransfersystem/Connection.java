package ca.jonsimpson.comp3203.filetransfersystem;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

public class Connection extends Net {
	
	public Connection(Socket socket) throws IOException {
		this.socket = socket;
		outputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
		inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
		
	}
	
	/**
	 * Accept and process incoming commands from the client.
	 */
	public void process() {
		boolean isRunning = true;
		// initial start-up procedure
		try {
			initialHello();
			while (isRunning) {
				
				switch (readCommand()) {
				case LS:
					processDirListing();
					break;
				
				default:
					break;
				}
				
				
			}
		} catch (EOFException e) {
			System.out.println("client disconnected");
			isRunning = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Send the client the current directory's listing of files.
	 */
	private void processDirListing() {
		
	}

	private void initialHello() throws IOException {
		System.out.println("waiting for hello message");
		String command = readCommand();
		System.out.println("received response");
		
		String hello2 = HELLO;
		if (hello2.equals(command)) {
			System.out.println("received hello from client");
			writeCommand(hello2);
			
		} else {
			throw new IOException("Didn't receive proper hello message");
		}
	}
	
}
