package ca.jonsimpson.comp3203.filetransfersystem;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

public class ServerConnection extends Net {
	
	private Path path = Paths.get("");
	
	public ServerConnection(Socket socket) throws IOException {
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
				
				String command = readCommand();
				switch (command) {
				case LS:
					processDirListing();
					break;
				
				case GET:
					processFileDownload();
					break;
				
				case PUT:
					processFileUpload();
					break;
				
				case CD:
					processChangeDirectory(readCommand());
					break;
				
				default:
					sendErrorCommand();
					System.out.println("received invalid command: " + command);
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
	
	private void processFileUpload() throws IOException {
		System.out.println("processing file upload");
		
		// read filename
		String fileName = readCommand();
		System.out.println("read in fileName");
		
		sendOKCommand();
		System.out.println("sent OK command");
		
		// read file size
		int fileSize = inputStream.readInt();
		System.out.println("read file length");
		
		Path filePath = path.resolve(fileName);
		
		// read in file
		byte[] bytes = new byte[fileSize];
		IOUtils.read(inputStream, bytes, 0, fileSize);
		
		System.out.println("copying file into " + filePath);
		
		File file = filePath.toFile();
		System.out.println(file);
		
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		
		fileOutputStream.write(bytes);
		fileOutputStream.close();
		
		System.out.println("successfully copied file " + fileName + " to " + filePath);
		
	}
	
	private void processFileDownload() throws IOException {
		String fileName = readCommand();
		try {
			Path filePath = path.resolve(fileName);
			System.out.println("processing GET for: " + filePath);
			
			if (Files.isReadable(filePath)) {
				sendOKCommand();
				
				// file exists and is readable
				
				// send the file length
				long fileLength = filePath.toFile().length();
				outputStream.writeInt((int) fileLength);
				outputStream.flush();
				
				// send the file
				System.out.println("beginning to copy file");
				Files.copy(filePath, outputStream);
				outputStream.flush();
				System.out.println("finished copying file");
			} else {
				// file is not valid
				System.out.println("file does not exist");
				sendInvalidFileCommand();
			}
			
		} catch (InvalidPathException e) {
			System.out.println("invalid path: " + path + "/" + fileName);
			sendErrorCommand();
		}
	}
	
	private void processChangeDirectory(String directory) throws IOException {
		
		String parsedPath = FilenameUtils.concat(path.toString(), directory);
		Path newPath = Paths.get(parsedPath);
		
		// verify that this directory exists
		if (!Files.exists(newPath)) {
			System.out.println("path does not exist: " + newPath);
			sendErrorCommand();
			return;
		}
		
		// verify that this path is a directory
		if (!Files.isDirectory(newPath)) {
			System.out.println("path is not a directory: " + newPath);
			sendErrorCommand();
			return;
		}
		
		if (!Files.isReadable(newPath)) {
			System.out.println("path is not readable: " + newPath);
			sendErrorCommand();
			return;
		}
		
		// set the path
		path = newPath;
		
		System.out.println("changing directory to: " + newPath);
		sendOKCommand();
	}
	
	private void sendInvalidFileCommand() throws IOException {
		writeCommand(INVALID_FILE);
	}
	
	private void sendErrorCommand() throws IOException {
		writeCommand(ERROR);
		
	}
	
	/**
	 * Let the client know that the command they sent is correct and valid
	 * 
	 * @throws IOException
	 */
	private void sendOKCommand() throws IOException {
		writeCommand(OK);
	}
	
	/**
	 * Send the client the current directory's listing of files.
	 * 
	 * @throws IOException
	 */
	private void processDirListing() throws IOException {
		System.out.println("processing directory listing");
		
		Path currentDirectory = getCurrentDirectory();
		if (!Files.isReadable(currentDirectory)) {
			System.out.println("directory is unreadable - access denied");
			sendErrorCommand();
			return;
		}
		
		DirectoryStream<Path> directoryStream = Files.newDirectoryStream(currentDirectory);
		
		sendOKCommand();
		
		StringBuffer stringBuffer = new StringBuffer();
		for (Path path : directoryStream) {
			stringBuffer.append(path.getFileName());
			
			// if the path is a directory, append the '/' character
			if (Files.isDirectory(path)) {
				stringBuffer.append('/');
			}
			// insert a newline character
			stringBuffer.append("\n");
		}
		
		writeCommand(stringBuffer.toString());
	}
	
	private Path getCurrentDirectory() {
		return path;
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
