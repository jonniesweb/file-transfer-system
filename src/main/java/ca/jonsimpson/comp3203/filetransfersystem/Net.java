package ca.jonsimpson.comp3203.filetransfersystem;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Net {
	// file transfer system network commands
	public static final String HELLO = "hello";
	public static final String LS = "ls";
	public static final String GET = "get";
	public static final String PUT = "put";
	public static final String CD = "cd";
	public static final String MKDIR = "mkdir";
	public static final String DISCONNECT = "disconnect";
	
	// server status codes/error codes
	public static final String OK = "ok";
	public static final String ACCESS_DENIED = "access_denied";
	public static final String INVALID_COMMAND = "invalid_command";
	public static final String ERROR = "error";
	
	// 
	protected Socket socket;
	protected DataOutputStream outputStream;
	protected DataInputStream inputStream;
	
	public void close() {
		System.out.println("exiting program");
		try {
			outputStream.flush();
			outputStream.close();
			inputStream.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	protected void writeCommand(String command) throws IOException {
		outputStream.writeUTF(command);
		outputStream.flush();
	}

	protected String readCommand() throws IOException {
		return inputStream.readUTF();
	}
}
