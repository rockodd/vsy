/**
 * 
 */
package Uebung2;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * @author Robert Ullmann
 *
 */
public class ServerMain {
	static ServerSocket serverSocket = null;
	static boolean exit = false;

	/**
	 * @param args
	 */
	public static void main(String[] args) {



		try {
			
			URL url = null;
			int port = 9865;
			// eigener Hostname
			String host = InetAddress.getLocalHost().getHostName();
			
			// Portzuweisung
			serverSocket = new ServerSocket(port);
			
			System.out.println("HTTP Server Waiting for client on port: "+port+" Host: "+host);
			while (!exit) {
				Socket connected = serverSocket.accept();
				(new Server(connected,serverSocket)).start();
			}
			

		} catch (IOException e) {

		
			e.printStackTrace();
		}
	

	}

}
