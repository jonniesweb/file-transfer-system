package ca.jonsimpson.comp3203.filetransfersystem;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client extends Net {
	
	public Client(String host, int port) throws UnknownHostException, IOException {
		socket = new Socket(host, port);
		
		outputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
		inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
	}
	
	public void hello() throws IOException {
		writeCommand(HELLO);
		
		if (HELLO.equals(readCommand())) {
			System.out.println("received hello from server");
		} else {
			throw new IOException("didn't receive hello message");
		}
	}

	public String getDirListing() throws IOException {
		writeCommand(LS);
		
		// get status of command
		String command = readCommand();
		if (OK.equals(command)) {
			return readCommand();
		} else {
			System.out.println("unable to read contents of directory");
			return null;
		}
	}
}
