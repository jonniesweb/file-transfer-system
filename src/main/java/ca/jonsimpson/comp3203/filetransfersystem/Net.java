package ca.jonsimpson.comp3203.filetransfersystem;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public abstract class Net {
	/*
	 * File transfer system network commands
	 */
	public static final String HELLO = "hello";
	public static final String LS = "ls";
	public static final String GET = "get";
	public static final String PUT = "put";
	public static final String CD = "cd";
	public static final String MKDIR = "mkdir";
	public static final String DISCONNECT = "disconnect";
	
	/*
	 * Server status codes/error codes
	 */
	public static final String OK = "ok";
	public static final String ACCESS_DENIED = "access_denied";
	public static final String INVALID_COMMAND = "invalid_command";
	public static final String INVALID_FILE = "invalid_file";
	public static final String ERROR = "error";
	
	/**
	 * The socket object of this connection.
	 */
	protected Socket socket;
	/**
	 * The output stream for writing data to the host at the other end of this
	 * socket.
	 */
	protected DataOutputStream outputStream;
	/**
	 * The input stream for reading data sent from the host at the other end of
	 * this socket.
	 */
	protected DataInputStream inputStream;
	
	/**
	 * Flush and close the input/output streams, then close the socket.
	 */
	public void close() {
		System.out.println("exiting program");
		try {
			outputStream.flush();
			outputStream.close();
			inputStream.close();
			socket.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Send a command, where the command is one of the commands listed in
	 * {@link Net}.
	 * 
	 * @param command
	 * @throws IOException
	 */
	protected void writeCommand(String command) throws IOException {
		outputStream.writeUTF(command);
		outputStream.flush();
	}
	
	/**
	 * Read a command from the underlying input stream. Returns a string of the
	 * result. String comparison with the commands in {@link Net} should then be
	 * used to determine the given command.
	 * 
	 * @return
	 * @throws IOException
	 */
	protected String readCommand() throws IOException {
		return inputStream.readUTF();
	}
}
