package ca.jonsimpson.comp3203.filetransfersystem;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.IOUtils;

/**
 * Communicate with the Server, containing the business logic for the file
 * transfer system.
 */
public class Client extends Net {
	
	public Client(String host, int port) throws UnknownHostException, IOException {
		socket = new Socket(host, port);
		
		outputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
		inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
	}
	
	/**
	 * Send the server a simple hello message.
	 * 
	 * <br>
	 * <h1>Sequence:</h1>
	 * <ol>
	 * <li>Client sends <code>HELLO</code> message to Server</li>
	 * <li>Server sends <code>HELLO</code> message to Client</li>
	 * </ol>
	 */
	public void hello() throws IOException {
		writeCommand(HELLO);
		
		if (HELLO.equals(readCommand())) {
			System.out.println("received hello from server");
		} else {
			throw new IOException("didn't receive hello message");
		}
	}
	
	/**
	 * Get the current listing of files and directories from the current
	 * directory.
	 * 
	 * <br>
	 * <ol>
	 * <li>Client sends <code>LS</code> command to Server
	 * <li>Server sends <code>OK</code> status code if valid command
	 * <li>Server sends String containing list of files
	 * </ol>
	 * 
	 * @return
	 * @throws IOException
	 */
	public void getDirListing() throws IOException {
		writeCommand(LS);
		
		// get status of command
		if (OK.equals(readCommand())) {
			System.out.println();
			System.out.println(readCommand());
			System.out.println();
			
		} else {
			System.out.println("unable to read contents of directory");
		}
	}
	
	/**
	 * Get the file on the remote server with the name <code>fileName</code>.
	 * Files will be placed in the client's home directory as specified by the
	 * <code>user.dir</code> property set by the JVM.
	 * 
	 * <br>
	 * <h1>Valid filenames include:</h1>
	 * <ul>
	 * <li>file.txt
	 * <li>Downloads/file2.txt
	 * </ul>
	 * 
	 * @param fileName
	 * @throws IOException
	 */
	public void getFile(String fileName) throws IOException {
		writeCommand(GET);
		writeCommand(fileName);
		
		String command = readCommand();
		if (OK.equals(command)) {
			// read the file length
			int fileLength = inputStream.readInt();
			byte[] bytes = new byte[fileLength];
			
			Path pathName = Paths.get("");
			Path path = pathName.resolve(fileName);
			
			IOUtils.read(inputStream, bytes, 0, fileLength);
			
			// get the filename since we're able to request a file in a
			// subdirectory (eg. target/pom.xml) but want to put it in the
			// current directory (eg. pom.xml)
			File file = path.getFileName().toFile();
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			fileOutputStream.write(bytes);
			fileOutputStream.close();
			
			System.out.println("successfully copied file " + fileName + " to "
					+ pathName.toAbsolutePath());
			
		} else if (INVALID_FILE.equals(command)) {
			System.out.println("file does not exist: " + fileName);
		} else {
			System.out.println("An error occurred. Unable to get file" + fileName);
		}
	}
	
	/**
	 * Change the current directory to that of <code>directory</code>, relative
	 * to the current directory, or absolutely if <code>directory</code> starts
	 * with a '/'. If the specified directory is <code>..</code>, then the CWD
	 * is moved up a directory to the parent.
	 * 
	 * @param directory
	 * @throws IOException
	 */
	public void changeDirectory(String directory) throws IOException {
		writeCommand(CD);
		writeCommand(directory);
		
		if (OK.equals(readCommand())) {
			System.out.println("Changed directories successfully");
		} else {
			System.out.println("Failed to change directories");
		}
		
	}
	
	/**
	 * Copy the file, represented by <code>fileName</code>, from the directory
	 * where you launched the program into the server's current working
	 * directory. The same filename is kept.
	 * 
	 * @param fileName
	 * @throws IOException
	 */
	public void putFile(String fileName) throws IOException {
		
		// get the directory where this program was started
		Path currentDir = Paths.get("");
		
		try {
			Path filePath = currentDir.resolve(fileName);
			// check if path is readable and is a file
			if (Files.isReadable(filePath) && Files.isRegularFile(filePath)) {
				writeCommand(PUT);
				writeCommand(fileName);
				System.out.println("wrote PUT command and fileName");
				
				System.out.println("waiting for server's OK");
				// verfiy server is cool with the PUT command
				if (!OK.equals(readCommand())) {
					System.out.println("Failed to copy file to server");
					return;
				}
				
				System.out.println("server said OK, lets send the file");
				
				// send file size
				File file = filePath.toFile();
				outputStream.writeInt((int) file.length());
				
				// send file
				System.out.println("begin copying file");
				Files.copy(filePath, outputStream);
				outputStream.flush();
				System.out.println("finished copying file");
				
			} else {
				System.out.println("file does not exist or is not a file");
			}
		} catch (InvalidPathException e) {
			System.out.println("invalid path: " + currentDir + "/" + fileName);
		}
		
	}
	
}
