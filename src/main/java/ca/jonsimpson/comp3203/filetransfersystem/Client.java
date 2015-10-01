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
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.IOUtils;

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
		if (OK.equals(readCommand())) {
			return readCommand();
		} else {
			System.out.println("unable to read contents of directory");
			return null;
		}
	}
	
	public void getFile(String fileName) throws IOException {
		writeCommand(GET);
		writeCommand(fileName);
		
		String command = readCommand();
		if (OK.equals(command)) {
			// read the file length
			int fileLength = inputStream.readInt();
			byte[] bytes = new byte[fileLength];
			
			Path pathName = Paths.get(System.getProperty("user.dir") + "/target/");
			Path path = pathName.resolve(fileName);
			
			IOUtils.read(inputStream, bytes, 0, fileLength);
			
			// get the filename since we're able to request a file in a
			// subdirectory (eg. target/pom.xml) but want to put it in the
			// current directory (eg. pom.xml)
			File file = path.getFileName().toFile();
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			fileOutputStream.write(bytes);
			fileOutputStream.close();
			
			System.out.println("successfully copied file " + fileName + " to " + pathName);
			
		} else if (INVALID_FILE.equals(command)) {
			System.out.println("file does not exist: " + fileName);
		} else {
			System.out.println("An error occurred. Unable to get file" + fileName);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
}
